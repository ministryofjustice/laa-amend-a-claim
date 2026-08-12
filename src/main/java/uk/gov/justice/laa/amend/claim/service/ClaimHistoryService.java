package uk.gov.justice.laa.amend.claim.service;

import static java.lang.Boolean.TRUE;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_ASSESSED_ESCAPE_CASE;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_ASSESSED_STAGE_DISBURSEMENT;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_CREATED;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_CREATED_AND_ESCAPED;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_VOIDED;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.AMENDMENT;

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
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimHistory;
import uk.gov.justice.laa.amend.claim.models.ClaimHistoryEvent;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;
import uk.gov.justice.laadata.providers.model.ProviderFirmSummary;

@Service
@RequiredArgsConstructor
public class ClaimHistoryService {

  public static final int MAXIMUM_ASSESSMENTS = 100;

  private final AssessmentService assessmentService;
  private final ClaimsApiClient claimsApiClient;
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

  public Set<String> getAmendedFields(ClaimDetails claim) {
    // TODO: Reinstate this once isAmended is wired up in claims API (DSTEW-2140)
    //    if (!claim.isAmended()) {
    //      return Set.of();
    //    }
    var history = claimsApiClient.getClaimHistory(claim.getClaimId()).block();

    if (history == null || history.getEvents() == null) {
      return Set.of();
    }

    return history.getEvents().stream()
        .filter(event -> event.getEventType() == AMENDMENT)
        .map(ClaimHistoryService::getChanges)
        .flatMap(Collection::stream)
        .filter(ClaimHistoryService::isRequested)
        .map(ClaimHistoryService::getFieldIdentifier)
        .collect(toSet());
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
