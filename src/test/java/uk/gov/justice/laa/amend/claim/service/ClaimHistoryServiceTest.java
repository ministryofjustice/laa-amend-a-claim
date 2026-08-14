package uk.gov.justice.laa.amend.claim.service;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.models.enums.AssessmentTypeEnum.ESCAPE_CASE_ASSESSMENT;
import static uk.gov.justice.laa.amend.claim.models.enums.AssessmentTypeEnum.STAGE_DISBURSEMENT_ASSESSMENT;
import static uk.gov.justice.laa.amend.claim.models.enums.AssessmentTypeEnum.VOID;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_ASSESSED_ESCAPE_CASE;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_ASSESSED_STAGE_DISBURSEMENT;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_CREATED_AND_ESCAPED;
import static uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType.CLAIM_VOIDED;
import static uk.gov.justice.laa.amend.claim.models.enums.OutcomeType.PAID_IN_FULL;
import static uk.gov.justice.laa.amend.claim.models.enums.OutcomeType.REDUCED;
import static uk.gov.justice.laa.amend.claim.service.ClaimHistoryService.MAXIMUM_ASSESSMENTS;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.AMENDMENT;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.ASSESSMENT;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.ClaimHistoryEvent;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryResultSet;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;
import uk.gov.justice.laadata.providers.model.ProviderFirmSummary;

@ExtendWith(MockitoExtension.class)
public class ClaimHistoryServiceTest {

  private static final String PROVIDER_NAME = "Some Provider";

  private static final MicrosoftApiUser ESCAPE_CASE_ASSESSED_USER =
      new MicrosoftApiUser(UUID.randomUUID().toString(), "Escape case assessment user", null, null);
  private static final MicrosoftApiUser STAGE_DISBURSEMENT_ASSESSED_USER =
      new MicrosoftApiUser(
          UUID.randomUUID().toString(), "Stage disbursement assessment user", null, null);
  private static final MicrosoftApiUser VOIDED_USER =
      new MicrosoftApiUser(UUID.randomUUID().toString(), "Voided user", null, null);

  private static final OffsetDateTime CREATED_DATE_TIME =
      OffsetDateTime.of(2026, 4, 15, 10, 0, 0, 0, UTC);
  private static final OffsetDateTime ESCAPE_CASE_ASSESSED_DATE_TIME =
      CREATED_DATE_TIME.plusDays(1);
  private static final OffsetDateTime STAGE_DISBURSEMENT_ASSESSED_DATE_TIME =
      CREATED_DATE_TIME.plusDays(2);
  private static final OffsetDateTime VOIDED_DATE_TIME = CREATED_DATE_TIME.plusDays(3);

  @Mock private AssessmentService assessmentService;

  @Mock private ClaimsApiClient claimsApiClient;

  @Mock private ProviderService providerService;

  @Mock private UserRetrievalService userRetrievalService;

  private ClaimHistoryService claimHistoryService;

  @BeforeEach
  void setUp() {
    claimHistoryService =
        new ClaimHistoryService(
            assessmentService, claimsApiClient, providerService, userRetrievalService);
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
  void getAmendedFieldsReturnsEmptySetWhenHistoryIsNull() {
    var claim = MockClaimsFunctions.createMockCivilClaim();

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.empty());

    assertThat(claimHistoryService.getAmendedFields(claim)).isEmpty();
  }

  @Test
  void getAmendedFieldsReturnsEmptySetWhenHistoryEventsAreNull() {
    var claim = MockClaimsFunctions.createMockCivilClaim();

    when(claimsApiClient.getClaimHistory(claim.getClaimId()))
        .thenReturn(Mono.just(new ClaimHistoryResultSet().claimId(claim.getClaimId())));

    assertThat(claimHistoryService.getAmendedFields(claim)).isEmpty();
  }

  @Test
  void getAmendedFieldsReturnsRequestedAmendmentFieldIdentifiers() {
    var claim = MockClaimsFunctions.createMockCivilClaim();

    var requestedChanges =
        List.of(
            change("REQUESTED", "claim.feeCode"),
            change("REQUESTED", "claimSummaryFee.netProfitCostsAmount"),
            change("CALCULATED", "claim.caseStartDate"),
            change("REQUESTED", "claim.feeCode"));

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(ASSESSMENT)
                        .metadata(Map.of("changes", requestedChanges)),
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .metadata(Map.of("changes", requestedChanges))));

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.just(history));

    assertThat(claimHistoryService.getAmendedFields(claim))
        .containsExactlyInAnyOrder("claim.feeCode", "claimSummaryFee.netProfitCostsAmount");
  }

  @Test
  void getAmendmentConfirmationReturnsChangedCalculatedCostFieldIdentifiers() {
    var claim = MockClaimsFunctions.createMockCivilClaim();

    var changes =
        List.of(
            change("REQUESTED", "claimSummaryFee.netProfitCostsAmount"),
            change("FSP", "fee.netProfitCostsAmount"),
            change("FSP", "fee.disbursementAmount"));

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .metadata(Map.of("price_changed", true, "changes", changes))));

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.just(history));

    assertThat(claimHistoryService.getAmendmentConfirmation(claim))
        .isEqualTo(
            new ClaimHistoryService.AmendmentConfirmation(
                true,
                Set.of(
                    "claimSummaryFee.netProfitCostsAmount",
                    "fee.netProfitCostsAmount",
                    "fee.disbursementAmount")));
  }

  @Test
  void getAmendmentConfirmationReturnsEmptyFieldSetWhenPriceHasNotChanged() {
    var claim = MockClaimsFunctions.createMockCivilClaim();

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .metadata(Map.of("price_changed", false, "changes", List.of()))));

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.just(history));

    assertThat(claimHistoryService.getAmendmentConfirmation(claim))
        .isEqualTo(new ClaimHistoryService.AmendmentConfirmation(false, Set.of()));
  }

  private static LinkedHashMap<String, String> change(String source, String fieldIdentifier) {
    var change = new LinkedHashMap<String, String>();
    change.put("change_source", source);
    change.put("field_identifier", fieldIdentifier);
    return change;
  }
}
