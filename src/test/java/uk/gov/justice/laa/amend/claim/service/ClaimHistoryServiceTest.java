package uk.gov.justice.laa.amend.claim.service;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.models.AssessmentTypeEnum.ESCAPE_CASE_ASSESSMENT;
import static uk.gov.justice.laa.amend.claim.models.AssessmentTypeEnum.STAGE_DISBURSEMENT_ASSESSMENT;
import static uk.gov.justice.laa.amend.claim.models.AssessmentTypeEnum.VOID;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_ASSESSED_ESCAPE_CASE;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_ASSESSED_STAGE_DISBURSEMENT;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_CREATED;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_CREATED_AND_ESCAPED;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_INQUEST_DATA_UPDATED;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_VOIDED;
import static uk.gov.justice.laa.amend.claim.models.OutcomeType.PAID_IN_FULL;
import static uk.gov.justice.laa.amend.claim.models.OutcomeType.REDUCED;
import static uk.gov.justice.laa.amend.claim.service.ClaimHistoryService.MAXIMUM_ASSESSMENTS;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.amend.claim.config.InquestConfig;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimHistoryEvent;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimInquestData;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;
import uk.gov.justice.laadata.providers.model.ProviderFirmSummary;

@ExtendWith(MockitoExtension.class)
public class ClaimHistoryServiceTest {

  private static final String PROVIDER_NAME = "Some Provider";
  private static final String INQUEST_MATTER_TYPE = "INQUEST";

  private static final MicrosoftApiUser ESCAPE_CASE_ASSESSED_USER =
      new MicrosoftApiUser(UUID.randomUUID().toString(), "Escape case assessment user", null, null);
  private static final MicrosoftApiUser STAGE_DISBURSEMENT_ASSESSED_USER =
      new MicrosoftApiUser(
          UUID.randomUUID().toString(), "Stage disbursement assessment user", null, null);
  private static final MicrosoftApiUser VOIDED_USER =
      new MicrosoftApiUser(UUID.randomUUID().toString(), "Voided user", null, null);
  private static final MicrosoftApiUser INQUEST_ACTOR_USER =
      new MicrosoftApiUser(UUID.randomUUID().toString(), "Inquest actor user", null, null);

  private static final OffsetDateTime CREATED_DATE_TIME =
      OffsetDateTime.of(2026, 4, 15, 10, 0, 0, 0, UTC);
  private static final OffsetDateTime ESCAPE_CASE_ASSESSED_DATE_TIME =
      CREATED_DATE_TIME.plusDays(1);
  private static final OffsetDateTime STAGE_DISBURSEMENT_ASSESSED_DATE_TIME =
      CREATED_DATE_TIME.plusDays(2);
  private static final OffsetDateTime VOIDED_DATE_TIME = CREATED_DATE_TIME.plusDays(3);

  @Mock private AssessmentService assessmentService;

  @Mock private ProviderService providerService;

  @Mock private UserRetrievalService userRetrievalService;

  @Mock private InquestDataService inquestDataService;

  private final InquestConfig inquestConfig = new InquestConfig();

  private ClaimHistoryService claimHistoryService;

  @BeforeEach
  void setUp() {
    inquestConfig.setMatterTypeCodes(Set.of(INQUEST_MATTER_TYPE));
    claimHistoryService =
        new ClaimHistoryService(
            assessmentService, providerService, userRetrievalService, inquestDataService,
            inquestConfig);
  }

  @Test
  void getClaimHistory() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setSubmittedDate(CREATED_DATE_TIME);
    claim.setHasAssessment(true);
    claim.setEscaped(true);

    var providerFirm =
        ProviderFirmOfficeDto.builder()
            .firm(ProviderFirmSummary.builder().firmName(PROVIDER_NAME).build())
            .build();
    when(providerService.getProviderFirm(claim.getOfficeCode())).thenReturn(providerFirm);

    when(userRetrievalService.getUser(ESCAPE_CASE_ASSESSED_USER.id()))
        .thenReturn(ESCAPE_CASE_ASSESSED_USER);
    when(userRetrievalService.getUser(STAGE_DISBURSEMENT_ASSESSED_USER.id()))
        .thenReturn(STAGE_DISBURSEMENT_ASSESSED_USER);
    when(userRetrievalService.getUser(VOIDED_USER.id())).thenReturn(VOIDED_USER);

    var voided =
        AssessmentInfo.builder()
            .assessmentType(VOID)
            .lastAssessedBy(VOIDED_USER.id())
            .lastAssessmentDate(VOIDED_DATE_TIME)
            .build();

    var assessedStageDisbursement =
        AssessmentInfo.builder()
            .assessmentType(STAGE_DISBURSEMENT_ASSESSMENT)
            .lastAssessedBy(STAGE_DISBURSEMENT_ASSESSED_USER.id())
            .lastAssessmentDate(STAGE_DISBURSEMENT_ASSESSED_DATE_TIME)
            .lastAssessmentOutcome(PAID_IN_FULL)
            .build();

    var assessedEscapeCase =
        AssessmentInfo.builder()
            .assessmentType(ESCAPE_CASE_ASSESSMENT)
            .lastAssessedBy(ESCAPE_CASE_ASSESSED_USER.id())
            .lastAssessmentDate(ESCAPE_CASE_ASSESSED_DATE_TIME)
            .lastAssessmentOutcome(REDUCED)
            .build();

    var assessments = List.of(voided, assessedStageDisbursement, assessedEscapeCase);
    when(assessmentService.getLatestAssessmentsByClaim(claim.getClaimId(), MAXIMUM_ASSESSMENTS))
        .thenReturn(assessments);

    var claimHistory = claimHistoryService.getClaimHistory(claim);

    var voidedEvent =
        new ClaimHistoryEvent(
            CLAIM_VOIDED, VOIDED_DATE_TIME, VOIDED_USER.displayName(), Optional.empty());
    var assessedStageDisbursementEvent =
        new ClaimHistoryEvent(
            CLAIM_ASSESSED_STAGE_DISBURSEMENT,
            STAGE_DISBURSEMENT_ASSESSED_DATE_TIME,
            STAGE_DISBURSEMENT_ASSESSED_USER.displayName(),
            Optional.of(PAID_IN_FULL));
    var assessedEscapeCaseEvent =
        new ClaimHistoryEvent(
            CLAIM_ASSESSED_ESCAPE_CASE,
            ESCAPE_CASE_ASSESSED_DATE_TIME,
            ESCAPE_CASE_ASSESSED_USER.displayName(),
            Optional.of(REDUCED));
    var createdEvent =
        new ClaimHistoryEvent(
            CLAIM_CREATED_AND_ESCAPED, CREATED_DATE_TIME, PROVIDER_NAME, Optional.empty());

    assertThat(claimHistory.latestAssessmentUser()).contains(VOIDED_USER);
    assertThat(claimHistory.events())
        .containsExactly(
            voidedEvent, assessedStageDisbursementEvent, assessedEscapeCaseEvent, createdEvent);
  }

  @Test
  void getClaimHistoryIncludesInquestEventUsingLaterUpdatedOnTimestamp() {
    var claim = eligibleClaimWithNoAssessments();
    var updatedOn = CREATED_DATE_TIME.plusDays(5);

    when(inquestDataService.get(claim.getClaimId()))
        .thenReturn(
            Optional.of(
                ClaimInquestData.builder()
                    .actorUserId(INQUEST_ACTOR_USER.id())
                    .createdOn(CREATED_DATE_TIME.plusDays(1))
                    .updatedOn(updatedOn)
                    .build()));
    when(userRetrievalService.getUser(INQUEST_ACTOR_USER.id())).thenReturn(INQUEST_ACTOR_USER);

    var claimHistory = claimHistoryService.getClaimHistory(claim);

    var inquestEvent =
        new ClaimHistoryEvent(
            CLAIM_INQUEST_DATA_UPDATED, updatedOn, INQUEST_ACTOR_USER.displayName(), Optional.empty());
    assertThat(claimHistory.events()).contains(inquestEvent);
  }

  @Test
  void getClaimHistoryInquestEventUsesCreatedOnWhenNeverUpdated() {
    var claim = eligibleClaimWithNoAssessments();
    var createdOn = CREATED_DATE_TIME.plusDays(1);

    when(inquestDataService.get(claim.getClaimId()))
        .thenReturn(
            Optional.of(
                ClaimInquestData.builder()
                    .actorUserId(INQUEST_ACTOR_USER.id())
                    .createdOn(createdOn)
                    .updatedOn(null)
                    .build()));
    when(userRetrievalService.getUser(INQUEST_ACTOR_USER.id())).thenReturn(INQUEST_ACTOR_USER);

    var claimHistory = claimHistoryService.getClaimHistory(claim);

    var inquestEvent =
        new ClaimHistoryEvent(
            CLAIM_INQUEST_DATA_UPDATED, createdOn, INQUEST_ACTOR_USER.displayName(), Optional.empty());
    assertThat(claimHistory.events()).contains(inquestEvent);
  }

  @Test
  void getClaimHistoryHasNoInquestEventWhenNoInquestDataExists() {
    var claim = eligibleClaimWithNoAssessments();
    when(inquestDataService.get(claim.getClaimId())).thenReturn(Optional.empty());

    var claimHistory = claimHistoryService.getClaimHistory(claim);

    assertThat(claimHistory.events())
        .noneMatch(event -> event.type() == CLAIM_INQUEST_DATA_UPDATED);
  }

  @Test
  void getClaimHistoryHasNoInquestEventWhenClaimIsNotInquestEligible() {
    var claim = claimWithNoAssessments(AreaOfLaw.CRIME_LOWER, null);

    var claimHistory = claimHistoryService.getClaimHistory(claim);

    verify(inquestDataService, never()).get(any());
    assertThat(claimHistory.events())
        .noneMatch(event -> event.type() == CLAIM_INQUEST_DATA_UPDATED);
  }

  @Test
  void getClaimHistoryInterleavesInquestEventWithAssessmentEventsByDate() {
    var claim = eligibleClaimWithNoAssessments();
    claim.setHasAssessment(true);

    var assessedStageDisbursement =
        AssessmentInfo.builder()
            .assessmentType(STAGE_DISBURSEMENT_ASSESSMENT)
            .lastAssessedBy(STAGE_DISBURSEMENT_ASSESSED_USER.id())
            .lastAssessmentDate(STAGE_DISBURSEMENT_ASSESSED_DATE_TIME)
            .lastAssessmentOutcome(PAID_IN_FULL)
            .build();
    when(assessmentService.getLatestAssessmentsByClaim(claim.getClaimId(), MAXIMUM_ASSESSMENTS))
        .thenReturn(List.of(assessedStageDisbursement));
    when(userRetrievalService.getUser(STAGE_DISBURSEMENT_ASSESSED_USER.id()))
        .thenReturn(STAGE_DISBURSEMENT_ASSESSED_USER);

    var inquestUpdatedOn = STAGE_DISBURSEMENT_ASSESSED_DATE_TIME.minusHours(1);
    when(inquestDataService.get(claim.getClaimId()))
        .thenReturn(
            Optional.of(
                ClaimInquestData.builder()
                    .actorUserId(INQUEST_ACTOR_USER.id())
                    .createdOn(CREATED_DATE_TIME.plusDays(1))
                    .updatedOn(inquestUpdatedOn)
                    .build()));
    when(userRetrievalService.getUser(INQUEST_ACTOR_USER.id())).thenReturn(INQUEST_ACTOR_USER);

    var claimHistory = claimHistoryService.getClaimHistory(claim);

    var assessedStageDisbursementEvent =
        new ClaimHistoryEvent(
            CLAIM_ASSESSED_STAGE_DISBURSEMENT,
            STAGE_DISBURSEMENT_ASSESSED_DATE_TIME,
            STAGE_DISBURSEMENT_ASSESSED_USER.displayName(),
            Optional.of(PAID_IN_FULL));
    var inquestEvent =
        new ClaimHistoryEvent(
            CLAIM_INQUEST_DATA_UPDATED,
            inquestUpdatedOn,
            INQUEST_ACTOR_USER.displayName(),
            Optional.empty());
    var createdEvent =
        new ClaimHistoryEvent(CLAIM_CREATED, CREATED_DATE_TIME, PROVIDER_NAME, Optional.empty());

    assertThat(claimHistory.events())
        .containsExactly(assessedStageDisbursementEvent, inquestEvent, createdEvent);
  }

  private CivilClaimDetails eligibleClaimWithNoAssessments() {
    return claimWithNoAssessments(AreaOfLaw.LEGAL_HELP, INQUEST_MATTER_TYPE);
  }

  private CivilClaimDetails claimWithNoAssessments(AreaOfLaw areaOfLaw, String matterType1) {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setSubmittedDate(CREATED_DATE_TIME);
    claim.setHasAssessment(false);
    claim.setEscaped(false);
    claim.setAreaOfLaw(areaOfLaw);
    claim.setMatterType1(matterType1);

    stubProviderFirm(claim.getOfficeCode());

    return claim;
  }

  private void stubProviderFirm(String officeCode) {
    var providerFirm =
        ProviderFirmOfficeDto.builder()
            .firm(ProviderFirmSummary.builder().firmName(PROVIDER_NAME).build())
            .build();
    when(providerService.getProviderFirm(officeCode)).thenReturn(providerFirm);
  }
}
