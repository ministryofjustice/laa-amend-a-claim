package uk.gov.justice.laa.amend.claim.service;

import static java.lang.Boolean.TRUE;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_ASSESSED_ESCAPE_CASE;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_ASSESSED_STAGE_DISBURSEMENT;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_CREATED;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_CREATED_AND_ESCAPED;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_VOIDED;
import static uk.gov.justice.laa.amend.claim.models.enums.DerivedClaimStatus.ACCEPTED;
import static uk.gov.justice.laa.amend.claim.models.enums.DerivedClaimStatus.AMENDED;
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
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimHistory;
import uk.gov.justice.laa.amend.claim.models.ClaimHistoryEvent;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryResultSet;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;
import uk.gov.justice.laadata.providers.model.ProviderFirmSummary;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimHistoryService {

  public static final int MAXIMUM_ASSESSMENTS = 100;

  private final AssessmentService assessmentService;
  private final ClaimsApiClient claimsApiClient;
  private final FeatureFlagsConfig featureFlagsConfig;
  private final ProviderService providerService;
  private final UserRetrievalService userRetrievalService;

  public ClaimHistory getClaimHistory(ClaimDetails claim) {
    List<AssessmentInfo> assessments =
        claim.isHasAssessment()
            ? assessmentService.getLatestAssessmentsByClaim(claim.getClaimId(), MAXIMUM_ASSESSMENTS)
            : List.of();

    var userIdToUser = getUserIdToUser(assessments);

    var events =
        Stream.concat(
                Stream.of(getClaimCreatedEvent(claim)),
                toClaimHistoryEvents(assessments, userIdToUser))
            .sorted(comparing(ClaimHistoryEvent::eventDateTime).reversed())
            .toList();

    var latestAssessmentUser =
        assessments.stream().findFirst().map(AssessmentInfo::lastAssessedBy).map(userIdToUser::get);

    return new ClaimHistory(events, latestAssessmentUser);
  }

  @Builder
  public record ClaimHistorySummary(
      MicrosoftApiUser lastUpdatedUser,
      OffsetDateTime lastUpdatedDateTime,
      Set<String> amendedFields) {

    public static ClaimHistorySummary empty() {
      return ClaimHistorySummary.builder().amendedFields(Set.of()).build();
    }
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
    var latestEvent = getLatestRelevantEvent(claim, history);
    latestEvent.ifPresent(
        event -> {
          builder.lastUpdatedDateTime(event.getEventTimestamp());
          builder.lastUpdatedUser(userRetrievalService.getUser(event.getActorId()));
        });

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

  private static Optional<uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent>
      getLatestRelevantEvent(ClaimDetails claim, ClaimHistoryResultSet claimHistoryResultSet) {
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

  public record AmendmentConfirmation(
      Boolean hasCalculatedCostsChanged, Set<String> amendedFields) {}

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

  private ClaimHistoryEvent getClaimCreatedEvent(ClaimDetails claim) {
    var user = getClaimCreatedUser(claim);
    var type = TRUE.equals(claim.getEscaped()) ? CLAIM_CREATED_AND_ESCAPED : CLAIM_CREATED;
    return new ClaimHistoryEvent(type, claim.getSubmittedDate(), user, Optional.empty());
  }

  private String getClaimCreatedUser(ClaimDetails claim) {
    return Optional.ofNullable(providerService.getProviderFirm(claim.getOfficeCode()))
        .map(ProviderFirmOfficeDto::getFirm)
        .map(ProviderFirmSummary::getFirmName)
        .orElse(claim.getOfficeCode());
  }

  private static Stream<ClaimHistoryEvent> toClaimHistoryEvents(
      List<AssessmentInfo> assessments, Map<String, MicrosoftApiUser> userIdToUser) {
    return assessments.stream().map(assessment -> toClaimHistoryEvent(assessment, userIdToUser));
  }

  private static ClaimHistoryEvent toClaimHistoryEvent(
      AssessmentInfo assessment, Map<String, MicrosoftApiUser> userIdToUser) {
    var userName =
        Optional.ofNullable(userIdToUser.get(assessment.lastAssessedBy()))
            .map(MicrosoftApiUser::name)
            .orElse(null);

    var type =
        switch (assessment.assessmentType()) {
          case ESCAPE_CASE_ASSESSMENT -> CLAIM_ASSESSED_ESCAPE_CASE;
          case STAGE_DISBURSEMENT_ASSESSMENT -> CLAIM_ASSESSED_STAGE_DISBURSEMENT;
          case VOID -> CLAIM_VOIDED;
        };

    return new ClaimHistoryEvent(
        type,
        assessment.lastAssessmentDate(),
        userName,
        Optional.ofNullable(assessment.lastAssessmentOutcome()));
  }

  @SuppressWarnings("unchecked")
  private static List<LinkedHashMap<String, String>> getChanges(
      uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent claimHistoryEvent) {
    return ((List<LinkedHashMap<String, String>>)
        claimHistoryEvent.getMetadata().getOrDefault("changes", List.of()));
  }

  private static boolean isRequested(LinkedHashMap<String, String> change) {
    return "REQUESTED".equals(change.get("change_source"));
  }

  private static String getFieldIdentifier(LinkedHashMap<String, String> change) {
    return change.get("field_identifier");
  }
}
