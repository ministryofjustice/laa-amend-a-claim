package uk.gov.justice.laa.payments.amend.service;

import static java.lang.Boolean.TRUE;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryChangeEntry.ChangeSourceEnum.REQUESTED;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.AMENDMENT;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.ASSESSMENT;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.SUBMISSION;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.VOID;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryAmendmentMetadata;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryAssessmentMetadata;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryChangeEntry;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistorySubmissionMetadata;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;
import uk.gov.justice.laa.payments.amend.config.FeatureFlagsConfig;
import uk.gov.justice.laa.payments.amend.models.AmendmentConfirmation;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.ClaimHistorySummary;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;
import uk.gov.justice.laa.payments.amend.models.enums.AssessmentTypeEnum;
import uk.gov.justice.laa.payments.amend.models.enums.OutcomeType;
import uk.gov.justice.laa.payments.amend.models.history.BaseClaimHistoryEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistory;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryApiEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAssessedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryCreatedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryVoidedEvent;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;
import uk.gov.justice.laadata.providers.model.ProviderFirmSummary;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimHistoryService {

  private final ClaimsApiClient claimsApiClient;
  private final ProviderService providerService;
  private final UserRetrievalService userRetrievalService;
  private final ClaimHistoryAmendmentsService claimHistoryAmendmentsService;
  private final FeatureFlagsConfig featureFlagsConfig;

  public ClaimHistory getClaimHistory(ClaimDetails claim) {
    var history = claimsApiClient.getClaimHistory(claim.getClaimId()).block();
    var historyEvents = ClaimHistoryMetadataMapper.toApiEvents(history);

    var events =
        toClaimHistoryEvents(historyEvents, claim)
            .sorted(comparing(BaseClaimHistoryEvent::eventDateTime).reversed())
            .toList();

    var lastUpdated = getLastUpdated(claim, historyEvents);
    return new ClaimHistory(events, lastUpdated.user(), lastUpdated.dateTime());
  }

  public ClaimHistorySummary getClaimHistorySummary(ClaimDetails claim) {
    var history = claimsApiClient.getClaimHistory(claim.getClaimId()).block();
    var historyEvents = ClaimHistoryMetadataMapper.toApiEvents(history);

    if (history == null || history.getEvents() == null) {
      log.error("Could not get claim history for claim {}", claim.getClaimId());
      var lastUpdated = getLastUpdated(claim, historyEvents);
      return ClaimHistorySummary.builder()
          .lastUpdatedUser(lastUpdated.user())
          .lastUpdatedDateTime(lastUpdated.dateTime())
          .amendedFields(Set.of())
          .build();
    }

    var builder = ClaimHistorySummary.builder();
    var lastUpdated = getLastUpdated(claim, historyEvents);
    builder.lastUpdatedUser(lastUpdated.user()).lastUpdatedDateTime(lastUpdated.dateTime());

    if (claim.isAmended()) {
      builder.amendedFields(
          historyEvents.stream()
              .filter(event -> event.eventType() == AMENDMENT)
              .map(ClaimHistoryService::getChanges)
              .flatMap(List::stream)
              .filter(ClaimHistoryService::isRequested)
              .map(ClaimHistoryChangeEntry::getFieldIdentifier)
              .collect(toSet()));
    } else {
      builder.amendedFields(Set.of());
    }

    return builder.build();
  }

  private LastUpdated getLastUpdated(ClaimDetails claim, List<ClaimHistoryApiEvent> historyEvents) {
    if (historyEvents.isEmpty()) {
      return getLastUpdated(claim);
    }

    return getLatestRelevantEvent(claim, historyEvents)
        .map(
            event ->
                new LastUpdated(
                    Optional.ofNullable(event.actorId())
                        .map(userRetrievalService::getUser)
                        .orElse(null),
                    event.eventTimestamp()))
        .orElseGet(() -> getLastUpdated(claim));
  }

  private LastUpdated getLastUpdated(ClaimDetails claim) {
    if (claim.getLastUpdatedUser() == null) {
      return new LastUpdated(null, claim.getLastUpdatedDateTime());
    }
    return new LastUpdated(
        userRetrievalService.getUser(claim.getLastUpdatedUser()), claim.getLastUpdatedDateTime());
  }

  private static Optional<ClaimHistoryApiEvent> getLatestRelevantEvent(
      ClaimDetails claim, List<ClaimHistoryApiEvent> historyEvents) {
    if (claim.getDerivedClaimStatus() == null) {
      return Optional.empty();
    }

    var eventType =
        switch (claim.getDerivedClaimStatus()) {
          // We are intentionally not using the SUBMISSION event because this does not have
          // an Entra ID as the actor ID, it just hardcodes Data-Claims-Event-Service
          case ACCEPTED -> null;
          case AMENDED -> AMENDMENT;
          case ASSESSED -> ASSESSMENT;
          case VOIDED -> VOID;
          default -> null;
        };

    if (eventType == null) {
      return Optional.empty();
    }

    return historyEvents.stream().filter(event -> event.eventType() == eventType).findFirst();
  }

  public AmendmentConfirmation getAmendmentConfirmation(ClaimDetails claim) {
    var history = claimsApiClient.getClaimHistory(claim.getClaimId()).block();
    var historyEvents = ClaimHistoryMetadataMapper.toApiEvents(history);

    if (history == null || history.getEvents() == null) {
      log.error("Could not get claim history for claim {}", claim.getClaimId());
      return new AmendmentConfirmation(false, Set.of());
    }

    var amendmentEvent =
        historyEvents.stream().filter(event -> event.eventType() == AMENDMENT).findFirst();

    if (amendmentEvent.isEmpty()) {
      log.error("Could not get claim history for claim {}", claim.getClaimId());
      return new AmendmentConfirmation(false, Set.of());
    }

    var metadata = toAmendmentMetadata(amendmentEvent.get());

    if (!TRUE.equals(metadata.getPriceChanged())) {
      return new AmendmentConfirmation(false, Set.of());
    }

    var changedCalculatedCosts =
        Optional.ofNullable(metadata.getChanges()).orElse(List.of()).stream()
            .map(ClaimHistoryChangeEntry::getFieldIdentifier)
            .collect(toSet());

    return new AmendmentConfirmation(true, changedCalculatedCosts);
  }

  private Stream<BaseClaimHistoryEvent> toClaimHistoryEvents(
      List<ClaimHistoryApiEvent> historyEvents, ClaimDetails claim) {
    if (historyEvents.isEmpty()) {
      return Stream.empty();
    }
    var fspEvents =
        featureFlagsConfig.isFspHistoryEnabled()
            ? claimHistoryAmendmentsService.toFspClaimHistoryEventsFromApiEvents(
                historyEvents, claim)
            : Stream.<BaseClaimHistoryEvent>empty();
    return Stream.of(
            toCreatedEvents(historyEvents, claim),
            toAssessmentEvents(historyEvents),
            toVoidEvents(historyEvents),
            fspEvents,
            claimHistoryAmendmentsService.toAmendmentClaimHistoryEventsFromApiEvents(
                historyEvents, claim))
        .flatMap(s -> s);
  }

  private Stream<BaseClaimHistoryEvent> toCreatedEvents(
      List<ClaimHistoryApiEvent> historyEvents, ClaimDetails claim) {
    return historyEvents.stream()
        .filter(event -> event.eventType() == SUBMISSION)
        .map(event -> toCreatedEvent(event, claim));
  }

  private BaseClaimHistoryEvent toCreatedEvent(ClaimHistoryApiEvent event, ClaimDetails claim) {
    var submissionMetadata = event.submissionMetadata();
    var officeCode =
        Optional.ofNullable(submissionMetadata)
            .map(ClaimHistorySubmissionMetadata::getOfficeAccountNumber)
            .orElse(claim.getOfficeCode());
    var user = getClaimCreatedUser(officeCode, claim.getOfficeCode());
    return new ClaimHistoryCreatedEvent(
        event.eventTimestamp(), user, TRUE.equals(claim.getEscaped()));
  }

  private Stream<BaseClaimHistoryEvent> toAssessmentEvents(
      List<ClaimHistoryApiEvent> historyEvents) {
    return historyEvents.stream()
        .filter(event -> event.eventType() == ASSESSMENT)
        .map(this::toAssessmentEvent)
        .flatMap(Optional::stream);
  }

  private Optional<BaseClaimHistoryEvent> toAssessmentEvent(ClaimHistoryApiEvent event) {
    var metadata = event.assessmentMetadata();
    var assessmentType = toAssessmentType(metadata);
    var outcomeType = toOutcomeType(metadata);
    if (assessmentType == null || outcomeType == null) {
      log.warn("Skipping unparseable assessment history event metadata: {}", metadata);
      return Optional.empty();
    }

    return Optional.of(
        new ClaimHistoryAssessedEvent(
            event.eventTimestamp(),
            getHistoryUserName(event.actorId()),
            assessmentType,
            outcomeType));
  }

  private Stream<BaseClaimHistoryEvent> toVoidEvents(List<ClaimHistoryApiEvent> historyEvents) {
    return historyEvents.stream().filter(event -> event.eventType() == VOID).map(this::toVoidEvent);
  }

  private BaseClaimHistoryEvent toVoidEvent(ClaimHistoryApiEvent event) {
    return new ClaimHistoryVoidedEvent(event.eventTimestamp(), getHistoryUserName(event.actorId()));
  }

  private String getHistoryUserName(String actorId) {
    return Optional.ofNullable(actorId)
        .map(userRetrievalService::getUser)
        .map(MicrosoftApiUser::name)
        .orElse(null);
  }

  private static AssessmentTypeEnum toAssessmentType(ClaimHistoryAssessmentMetadata metadata) {
    if (metadata == null) {
      return null;
    }
    try {
      return Optional.ofNullable(metadata.getAssessmentType())
          .map(AssessmentTypeEnum::valueOf)
          .orElse(null);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private static OutcomeType toOutcomeType(ClaimHistoryAssessmentMetadata metadata) {
    if (metadata == null || metadata.getAssessmentOutcome() == null) {
      return null;
    }
    return switch (metadata.getAssessmentOutcome()) {
      case "PAID_IN_FULL" -> OutcomeType.PAID_IN_FULL;
      case "REDUCED_STILL_ESCAPED" -> OutcomeType.REDUCED;
      case "REDUCED_TO_FIXED_FEE" -> OutcomeType.REDUCED_TO_FIXED_FEE;
      case "NILLED" -> OutcomeType.NILLED;
      default -> null;
    };
  }

  private String getClaimCreatedUser(String officeCode, String fallbackOfficeCode) {
    return Optional.ofNullable(providerService.getProviderFirm(officeCode))
        .map(ProviderFirmOfficeDto::getFirm)
        .map(ProviderFirmSummary::getFirmName)
        .orElse(fallbackOfficeCode);
  }

  private static ClaimHistoryAmendmentMetadata toAmendmentMetadata(
      ClaimHistoryApiEvent historyEvent) {
    return Optional.ofNullable(historyEvent.amendmentMetadata())
        .orElseGet(() -> new ClaimHistoryAmendmentMetadata().changes(List.of()));
  }

  private static List<ClaimHistoryChangeEntry> getChanges(ClaimHistoryApiEvent historyEvent) {
    return Optional.ofNullable(toAmendmentMetadata(historyEvent).getChanges()).orElse(List.of());
  }

  private static boolean isRequested(ClaimHistoryChangeEntry change) {
    return change.getChangeSource() == REQUESTED;
  }

  private record LastUpdated(MicrosoftApiUser user, OffsetDateTime dateTime) {}
}
