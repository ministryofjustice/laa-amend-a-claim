package uk.gov.justice.laa.payments.amend.service;

import static java.lang.Boolean.TRUE;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.AMENDMENT;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryResultSet;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;
import uk.gov.justice.laa.payments.amend.models.AmendmentConfirmation;
import uk.gov.justice.laa.payments.amend.models.AssessmentInfo;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.ClaimHistorySummary;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;
import uk.gov.justice.laa.payments.amend.models.history.BaseClaimHistoryEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistory;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAssessedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryCreatedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryVoidedEvent;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;
import uk.gov.justice.laadata.providers.model.ProviderFirmSummary;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimHistoryService {

  public static final int MAXIMUM_ASSESSMENTS = 100;

  private final AssessmentService assessmentService;
  private final ClaimsApiClient claimsApiClient;
  private final ProviderService providerService;
  private final UserRetrievalService userRetrievalService;
  private final ClaimHistoryAmendmentsService claimHistoryAmendmentsService;

  public ClaimHistory getClaimHistory(ClaimDetails claim) {
    List<AssessmentInfo> assessments =
        claim.isHasAssessment()
            ? assessmentService.getLatestAssessmentsByClaim(claim.getClaimId(), MAXIMUM_ASSESSMENTS)
            : List.of();

    var history = claimsApiClient.getClaimHistory(claim.getClaimId()).block();

    var userIdToUser = getUserIdToUser(assessments);

    var events =
        Stream.of(
                Stream.of(getClaimCreatedEvent(claim)),
                toClaimHistoryEvents(assessments, userIdToUser),
                claimHistoryAmendmentsService.toAmendmentClaimHistoryEvents(history, claim))
            .flatMap(s -> s)
            .sorted(comparing(BaseClaimHistoryEvent::eventDateTime).reversed())
            .toList();

    var lastUpdated = getLastUpdated(claim, history);
    return new ClaimHistory(events, lastUpdated.user(), lastUpdated.dateTime());
  }

  public ClaimHistorySummary getClaimHistorySummary(ClaimDetails claim) {
    var history = claimsApiClient.getClaimHistory(claim.getClaimId()).block();

    if (history == null || history.getEvents() == null) {
      log.error("Could not get claim history for claim {}", claim.getClaimId());
      return ClaimHistorySummary.builder()
          .lastUpdatedUser(userRetrievalService.getUser(claim.getLastUpdatedUser()))
          .lastUpdatedDateTime(claim.getLastUpdatedDateTime())
          .amendedFields(Set.of())
          .build();
    }

    var builder = ClaimHistorySummary.builder();
    var lastUpdated = getLastUpdated(claim, history);
    builder.lastUpdatedUser(lastUpdated.user()).lastUpdatedDateTime(lastUpdated.dateTime());

    if (claim.isAmended()) {
      builder.amendedFields(
          history.getEvents().stream()
              .filter(event -> event.getEventType() == AMENDMENT)
              .map(ClaimHistoryService::getChanges)
              .flatMap(Collection::stream)
              .filter(ClaimHistoryService::isRequested)
              .map(ClaimHistoryService::getFieldIdentifier)
              .collect(toSet()));
    } else {
      builder.amendedFields(Set.of());
    }

    return builder.build();
  }

  private LastUpdated getLastUpdated(
      ClaimDetails claim, ClaimHistoryResultSet claimHistoryResultSet) {
    if (claimHistoryResultSet == null || claimHistoryResultSet.getEvents() == null) {
      return getLastUpdated(claim);
    }

    return getLatestRelevantEvent(claim, claimHistoryResultSet)
        .map(
            event ->
                new LastUpdated(
                    Optional.ofNullable(event.getActorId())
                        .map(userRetrievalService::getUser)
                        .orElse(null),
                    event.getEventTimestamp()))
        .orElse(getLastUpdated(claim));
  }

  private LastUpdated getLastUpdated(ClaimDetails claim) {
    return new LastUpdated(
        userRetrievalService.getUser(claim.getLastUpdatedUser()), claim.getLastUpdatedDateTime());
  }

  private static Optional<ClaimHistoryEvent> getLatestRelevantEvent(
      ClaimDetails claim, ClaimHistoryResultSet claimHistoryResultSet) {
    if (claim.getDerivedClaimStatus() == null) {
      return Optional.empty();
    }

    var eventType =
        switch (claim.getDerivedClaimStatus()) {
          case ACCEPTED -> ClaimHistoryEventType.SUBMISSION;
          case AMENDED -> ClaimHistoryEventType.AMENDMENT;
          case ASSESSED -> ClaimHistoryEventType.ASSESSMENT;
          case VOIDED -> ClaimHistoryEventType.VOID;
          default -> null;
        };

    if (eventType == null) {
      return Optional.empty();
    }

    return claimHistoryResultSet.getEvents().stream()
        .filter(event -> event.getEventType() == eventType)
        .findFirst();
  }

  public AmendmentConfirmation getAmendmentConfirmation(ClaimDetails claim) {
    var history = claimsApiClient.getClaimHistory(claim.getClaimId()).block();

    if (history == null || history.getEvents() == null) {
      log.error("Could not get claim history for claim {}", claim.getClaimId());
      return new AmendmentConfirmation(false, Set.of());
    }

    var amendmentEvent =
        history.getEvents().stream().filter(event -> event.getEventType() == AMENDMENT).findFirst();

    if (amendmentEvent.isEmpty()) {
      log.error("Could not get claim history for claim {}", claim.getClaimId());
      return new AmendmentConfirmation(false, Set.of());
    }

    var amendment = amendmentEvent.get();

    if (!TRUE.equals(amendment.getMetadata().get("price_changed"))) {
      return new AmendmentConfirmation(false, Set.of());
    }

    var changedCalculatedCosts =
        getChanges(amendment).stream()
            .map(ClaimHistoryService::getFieldIdentifier)
            .collect(toSet());

    return new AmendmentConfirmation(true, changedCalculatedCosts);
  }

  private Map<String, MicrosoftApiUser> getUserIdToUser(final List<AssessmentInfo> assessments) {
    var userIds =
        assessments.stream()
            .map(AssessmentInfo::lastAssessedBy)
            .filter(Objects::nonNull)
            .collect(toSet());
    var userIdToUser = new HashMap<String, MicrosoftApiUser>();
    userIds.forEach(
        userId -> {
          var user = userRetrievalService.getUser(userId);
          if (user != null) {
            userIdToUser.put(userId, user);
          }
        });
    return userIdToUser;
  }

  private BaseClaimHistoryEvent getClaimCreatedEvent(ClaimDetails claim) {
    var user = getClaimCreatedUser(claim);
    return new ClaimHistoryCreatedEvent(
        claim.getSubmittedDate(), user, TRUE.equals(claim.getEscaped()));
  }

  private String getClaimCreatedUser(ClaimDetails claim) {
    return Optional.ofNullable(providerService.getProviderFirm(claim.getOfficeCode()))
        .map(ProviderFirmOfficeDto::getFirm)
        .map(ProviderFirmSummary::getFirmName)
        .orElse(claim.getOfficeCode());
  }

  private static Stream<BaseClaimHistoryEvent> toClaimHistoryEvents(
      List<AssessmentInfo> assessments, Map<String, MicrosoftApiUser> userIdToUser) {
    return assessments.stream().map(assessment -> toClaimHistoryEvent(assessment, userIdToUser));
  }

  private static BaseClaimHistoryEvent toClaimHistoryEvent(
      AssessmentInfo assessment, Map<String, MicrosoftApiUser> userIdToUser) {
    var userName =
        Optional.ofNullable(userIdToUser.get(assessment.lastAssessedBy()))
            .map(MicrosoftApiUser::name)
            .orElse(null);

    return switch (assessment.assessmentType()) {
      case ESCAPE_CASE_ASSESSMENT, STAGE_DISBURSEMENT_ASSESSMENT ->
          new ClaimHistoryAssessedEvent(
              assessment.lastAssessmentDate(),
              userName,
              assessment.assessmentType(),
              assessment.lastAssessmentOutcome());
      case VOID -> new ClaimHistoryVoidedEvent(assessment.lastAssessmentDate(), userName);
    };
  }

  @SuppressWarnings("unchecked")
  private static List<LinkedHashMap<String, String>> getChanges(
      ClaimHistoryEvent claimHistoryEvent) {
    return ((List<LinkedHashMap<String, String>>)
        claimHistoryEvent.getMetadata().getOrDefault("changes", List.of()));
  }

  private static boolean isRequested(LinkedHashMap<String, String> change) {
    return "REQUESTED".equals(change.get("change_source"));
  }

  private static String getFieldIdentifier(LinkedHashMap<String, String> change) {
    return change.get("field_identifier");
  }

  private record LastUpdated(MicrosoftApiUser user, OffsetDateTime dateTime) {}
}
