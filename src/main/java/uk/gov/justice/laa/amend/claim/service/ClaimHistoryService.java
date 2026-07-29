package uk.gov.justice.laa.amend.claim.service;

import static java.lang.Boolean.TRUE;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_ASSESSED_ESCAPE_CASE;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_ASSESSED_STAGE_DISBURSEMENT;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_CREATED;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_CREATED_AND_ESCAPED;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_INQUEST_DATA_UPDATED;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_VOIDED;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.config.InquestConfig;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimHistory;
import uk.gov.justice.laa.amend.claim.models.ClaimHistoryEvent;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.utils.InquestEligibility;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimInquestData;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;
import uk.gov.justice.laadata.providers.model.ProviderFirmSummary;

@Service
@RequiredArgsConstructor
public class ClaimHistoryService {

  public static final int MAXIMUM_ASSESSMENTS = 100;

  private final AssessmentService assessmentService;
  private final ProviderService providerService;
  private final UserRetrievalService userRetrievalService;
  private final InquestDataService inquestDataService;
  private final InquestConfig inquestConfig;

  public ClaimHistory getClaimHistory(ClaimDetails claim) {
    List<AssessmentInfo> assessments =
        claim.isHasAssessment()
            ? assessmentService.getLatestAssessmentsByClaim(claim.getClaimId(), MAXIMUM_ASSESSMENTS)
            : List.of();

    var userIdToUser = getUserIdToUser(assessments);

    var events =
        Stream.of(
                Stream.of(getClaimCreatedEvent(claim)),
                toClaimHistoryEvents(assessments, userIdToUser),
                getInquestDataEvent(claim).stream())
            .flatMap(eventStream -> eventStream)
            .sorted(comparing(ClaimHistoryEvent::eventDateTime).reversed())
            .toList();

    var latestAssessmentUser =
        assessments.stream().findFirst().map(AssessmentInfo::lastAssessedBy).map(userIdToUser::get);

    return new ClaimHistory(events, latestAssessmentUser);
  }

  private Optional<ClaimHistoryEvent> getInquestDataEvent(ClaimDetails claim) {
    if (!InquestEligibility.isEligible(claim, inquestConfig.getMatterTypeCodes())) {
      return Optional.empty();
    }

    return inquestDataService.get(claim.getClaimId()).map(this::toInquestHistoryEvent);
  }

  private ClaimHistoryEvent toInquestHistoryEvent(ClaimInquestData inquestData) {
    var eventDateTime = latestOf(inquestData.getCreatedOn(), inquestData.getUpdatedOn());
    var user =
        Optional.ofNullable(userRetrievalService.getUser(inquestData.getActorUserId()))
            .map(MicrosoftApiUser::name)
            .orElse(null);
    return new ClaimHistoryEvent(CLAIM_INQUEST_DATA_UPDATED, eventDateTime, user, Optional.empty());
  }

  private static OffsetDateTime latestOf(OffsetDateTime createdOn, OffsetDateTime updatedOn) {
    return updatedOn == null || createdOn.isAfter(updatedOn) ? createdOn : updatedOn;
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
}
