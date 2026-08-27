package uk.gov.justice.laa.payments.amend.service;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.AMENDMENT;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.ASSESSMENT;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.SUBMISSION;
import static uk.gov.justice.laa.payments.amend.models.enums.AssessmentTypeEnum.ESCAPE_CASE_ASSESSMENT;
import static uk.gov.justice.laa.payments.amend.models.enums.AssessmentTypeEnum.STAGE_DISBURSEMENT_ASSESSMENT;
import static uk.gov.justice.laa.payments.amend.models.enums.AssessmentTypeEnum.VOID;
import static uk.gov.justice.laa.payments.amend.models.enums.ClaimHistoryEventType.CLAIM_ASSESSED_ESCAPE_CASE;
import static uk.gov.justice.laa.payments.amend.models.enums.ClaimHistoryEventType.CLAIM_ASSESSED_STAGE_DISBURSEMENT;
import static uk.gov.justice.laa.payments.amend.models.enums.ClaimHistoryEventType.CLAIM_CREATED_AND_ESCAPED;
import static uk.gov.justice.laa.payments.amend.models.enums.ClaimHistoryEventType.CLAIM_VOIDED;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.ACCEPTED;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.AMENDED;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.ASSESSED;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.INVALID;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.VOIDED;
import static uk.gov.justice.laa.payments.amend.models.enums.OutcomeType.PAID_IN_FULL;
import static uk.gov.justice.laa.payments.amend.models.enums.OutcomeType.REDUCED;
import static uk.gov.justice.laa.payments.amend.service.ClaimHistoryService.MAXIMUM_ASSESSMENTS;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryAmendedEvent;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryAssessedEvent;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryCreatedEvent;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryVoidedEvent;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReferenceList;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryResultSet;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;
import uk.gov.justice.laa.payments.amend.config.FeatureFlagsConfig;
import uk.gov.justice.laa.payments.amend.models.AmendmentConfirmation;
import uk.gov.justice.laa.payments.amend.models.AssessmentInfo;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;
import uk.gov.justice.laa.payments.amend.models.enums.GenderCode;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
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

  @Mock private FeatureFlagsConfig featureFlagsConfig;

  @Mock private ProviderService providerService;

  @Mock private UserRetrievalService userRetrievalService;

  @Mock private SystemReferenceService systemReferenceService;

  private ClaimHistoryService claimHistoryService;

  @BeforeEach
  void setUp() {
    claimHistoryService =
        new ClaimHistoryService(
            assessmentService,
            claimsApiClient,
            featureFlagsConfig,
            providerService,
            userRetrievalService,
            systemReferenceService);
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
    when(claimsApiClient.getClaimHistory(claim.getClaimId()))
        .thenReturn(
            Mono.just(new ClaimHistoryResultSet().claimId(claim.getClaimId()).events(List.of())));
    when(systemReferenceService.getAmendmentRequestedByReferenceList())
        .thenReturn(new AmendmentRequestedByReferenceList().requestedBy(List.of()));

    var claimHistory = claimHistoryService.getClaimHistory(claim);

    var voidedEvent = new ClaimHistoryVoidedEvent(VOIDED_DATE_TIME, VOIDED_USER.displayName());
    var assessedStageDisbursementEvent =
        new ClaimHistoryAssessedEvent(
            STAGE_DISBURSEMENT_ASSESSED_DATE_TIME,
            STAGE_DISBURSEMENT_ASSESSED_USER.displayName(),
            STAGE_DISBURSEMENT_ASSESSMENT,
            PAID_IN_FULL);
    var assessedEscapeCaseEvent =
        new ClaimHistoryAssessedEvent(
            ESCAPE_CASE_ASSESSED_DATE_TIME,
            ESCAPE_CASE_ASSESSED_USER.displayName(),
            ESCAPE_CASE_ASSESSMENT,
            REDUCED);
    var createdEvent = new ClaimHistoryCreatedEvent(CREATED_DATE_TIME, PROVIDER_NAME, true);

    assertThat(claimHistory.latestAssessmentUser()).contains(VOIDED_USER);
    assertThat(claimHistory.events())
        .containsExactly(
            voidedEvent, assessedStageDisbursementEvent, assessedEscapeCaseEvent, createdEvent);
  }

  @Test
  void getClaimHistoryIncludesResolvedRequestedAmendmentChanges() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setSubmittedDate(CREATED_DATE_TIME);
    claim.setHasAssessment(false);

    var amendedDateTime = CREATED_DATE_TIME.plusDays(1);
    var amendedUser =
        new MicrosoftApiUser(UUID.randomUUID().toString(), "Amended user", null, null);
    when(userRetrievalService.getUser(amendedUser.id())).thenReturn(amendedUser);
    when(systemReferenceService.getAmendmentRequestedByReferenceList())
        .thenReturn(new AmendmentRequestedByReferenceList().requestedBy(List.of()));
    when(systemReferenceService.getAmendmentRequestedByOptions(org.mockito.ArgumentMatchers.any()))
        .thenReturn(Map.of("PROVIDER", "Provider"));
    when(systemReferenceService.getAmendmentRequestReason(
            org.mockito.ArgumentMatchers.eq("PROVIDER"), org.mockito.ArgumentMatchers.any()))
        .thenReturn(Map.of("CORRECTION", "Correction"));

    var changes =
        List.of(
            change("REQUESTED", "client.genderCode", "M", "F"),
            change("REQUESTED", "claim.caseStartDate", "2026-04-01", "2026-04-02"),
            change("REQUESTED", "unknown.field", "before", "after"),
            change("FSP", "claim.caseConcludedDate", "2026-04-05", "2026-04-06"));

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .actorId(amendedUser.id())
                        .eventTimestamp(amendedDateTime)
                        .metadata(
                            Map.of(
                                "requested_by_code",
                                "PROVIDER",
                                "amendment_reason_code",
                                "CORRECTION",
                                "changes",
                                changes))));

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.just(history));

    var claimHistory = claimHistoryService.getClaimHistory(claim);

    var amendmentEvent =
        claimHistory.events().stream()
            .filter(ClaimHistoryAmendedEvent.class::isInstance)
            .map(ClaimHistoryAmendedEvent.class::cast)
            .findFirst()
            .orElseThrow();

    assertThat(amendmentEvent.user()).isEqualTo(amendedUser.displayName());
    assertThat(amendmentEvent.requestedByCode()).isEqualTo("Provider");
    assertThat(amendmentEvent.amendmentReasonCode()).isEqualTo("Correction");
    assertThat(amendmentEvent.amendmentChanges()).hasSize(3);

    var genderChange =
        amendmentEvent.amendmentChanges().stream()
            .filter(change -> "client.genderCode".equals(change.fieldIdentifier()))
            .findFirst()
            .orElseThrow();
    assertThat(genderChange.field()).isNotNull();
    assertThat(genderChange.field().name()).isEqualTo("GENDER");
    assertThat(genderChange.fieldMessageKey()).isEqualTo("claimHistory.amended.GENDER");
    assertThat(genderChange.before()).isEqualTo(GenderCode.MALE);
    assertThat(genderChange.after()).isEqualTo(GenderCode.FEMALE);

    var unknownFieldChange =
        amendmentEvent.amendmentChanges().stream()
            .filter(change -> "unknown.field".equals(change.fieldIdentifier()))
            .findFirst()
            .orElseThrow();
    assertThat(unknownFieldChange.field()).isNull();
    assertThat(unknownFieldChange.fieldMessageKey()).isNull();
    assertThat(unknownFieldChange.before()).isEqualTo("before");
    assertThat(unknownFieldChange.after()).isEqualTo("after");
  }

  @Test
  void getClaimHistorySetsAmendmentFieldMessageKeyForKnownField() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setSubmittedDate(CREATED_DATE_TIME);
    claim.setHasAssessment(false);

    var amendedDateTime = CREATED_DATE_TIME.plusDays(1);
    var amendedUser =
        new MicrosoftApiUser(UUID.randomUUID().toString(), "Amended user", null, null);
    when(userRetrievalService.getUser(amendedUser.id())).thenReturn(amendedUser);
    when(systemReferenceService.getAmendmentRequestedByReferenceList())
        .thenReturn(new AmendmentRequestedByReferenceList().requestedBy(List.of()));

    var changes = List.of(change("REQUESTED", "claim.maatId", "before", "after"));

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .actorId(amendedUser.id())
                        .eventTimestamp(amendedDateTime)
                        .metadata(Map.of("changes", changes))));

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.just(history));

    var claimHistory = claimHistoryService.getClaimHistory(claim);

    var amendmentEvent =
        claimHistory.events().stream()
            .filter(ClaimHistoryAmendedEvent.class::isInstance)
            .map(ClaimHistoryAmendedEvent.class::cast)
            .findFirst()
            .orElseThrow();

    assertThat(amendmentEvent.amendmentChanges()).hasSize(1);
    assertThat(amendmentEvent.amendmentChanges().getFirst().field()).isNotNull();
    assertThat(amendmentEvent.amendmentChanges().getFirst().field().name()).isEqualTo("MAAT_ID");
    assertThat(amendmentEvent.amendmentChanges().getFirst().fieldMessageKey())
        .isEqualTo("claimHistory.amended.MAAT_ID");
  }

  @Test
  void getHistorySummaryReturnsEmptySetWhenClaimHistoryIsNull() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setAmended(true);
    claim.setDerivedClaimStatus(AMENDED);

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.empty());

    var summary = claimHistoryService.getClaimHistorySummary(claim);

    assertThat(summary.lastUpdatedUser()).isNull();
    assertThat(summary.lastUpdatedDateTime()).isNull();
    assertThat(summary.amendedFields()).isEmpty();
  }

  @Test
  void getHistorySummaryReturnsEmptySetWhenClaimHistoryEventsAreNull() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setAmended(true);
    claim.setDerivedClaimStatus(AMENDED);

    when(claimsApiClient.getClaimHistory(claim.getClaimId()))
        .thenReturn(Mono.just(new ClaimHistoryResultSet().claimId(claim.getClaimId())));

    var summary = claimHistoryService.getClaimHistorySummary(claim);

    assertThat(summary.lastUpdatedUser()).isNull();
    assertThat(summary.lastUpdatedDateTime()).isNull();
    assertThat(summary.amendedFields()).isEmpty();
  }

  @Test
  void getClaimHistorySummaryReturnsRequestedAmendmentFieldIdentifiers() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setAmended(true);
    claim.setDerivedClaimStatus(AMENDED);

    var amendedDateTime = CREATED_DATE_TIME.plusDays(4);
    var amendedUser =
        new MicrosoftApiUser(UUID.randomUUID().toString(), "Amended user", null, null);

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
                        .actorId(amendedUser.id())
                        .eventTimestamp(amendedDateTime)
                        .metadata(Map.of("changes", requestedChanges))));

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.just(history));
    when(userRetrievalService.getUser(amendedUser.id())).thenReturn(amendedUser);

    var summary = claimHistoryService.getClaimHistorySummary(claim);

    assertThat(summary.lastUpdatedUser()).isEqualTo(amendedUser);
    assertThat(summary.lastUpdatedDateTime()).isEqualTo(amendedDateTime);
    assertThat(summary.amendedFields())
        .containsExactlyInAnyOrder("claim.feeCode", "claimSummaryFee.netProfitCostsAmount");
  }

  @Test
  void getClaimHistorySummaryReturnsSubmissionEventDetailsForAcceptedClaim() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setDerivedClaimStatus(ACCEPTED);

    var submittedUser =
        new MicrosoftApiUser(UUID.randomUUID().toString(), "Submitted user", null, null);
    var submittedDateTime = CREATED_DATE_TIME;

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    historyEvent(SUBMISSION, submittedUser.id(), submittedDateTime, Map.of()),
                    historyEvent(
                        AMENDMENT,
                        UUID.randomUUID().toString(),
                        CREATED_DATE_TIME.plusDays(1),
                        Map.of())));

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.just(history));
    when(userRetrievalService.getUser(submittedUser.id())).thenReturn(submittedUser);

    var summary = claimHistoryService.getClaimHistorySummary(claim);

    assertThat(summary.lastUpdatedUser()).isEqualTo(submittedUser);
    assertThat(summary.lastUpdatedDateTime()).isEqualTo(submittedDateTime);
    assertThat(summary.amendedFields()).isEmpty();
  }

  @Test
  void getClaimHistorySummaryReturnsAssessmentEventDetailsForAssessedClaim() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setDerivedClaimStatus(ASSESSED);

    var assessedUser =
        new MicrosoftApiUser(UUID.randomUUID().toString(), "Assessed user", null, null);

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    historyEvent(
                        ASSESSMENT, assessedUser.id(), ESCAPE_CASE_ASSESSED_DATE_TIME, Map.of()),
                    historyEvent(
                        SUBMISSION, UUID.randomUUID().toString(), CREATED_DATE_TIME, Map.of())));

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.just(history));
    when(userRetrievalService.getUser(assessedUser.id())).thenReturn(assessedUser);

    var summary = claimHistoryService.getClaimHistorySummary(claim);

    assertThat(summary.lastUpdatedUser()).isEqualTo(assessedUser);
    assertThat(summary.lastUpdatedDateTime()).isEqualTo(ESCAPE_CASE_ASSESSED_DATE_TIME);
    assertThat(summary.amendedFields()).isEmpty();
  }

  @Test
  void getClaimHistorySummaryReturnsVoidEventDetailsForVoidedClaim() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setDerivedClaimStatus(VOIDED);

    var voidedUser =
        new MicrosoftApiUser(UUID.randomUUID().toString(), "Voided event user", null, null);

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    historyEvent(
                        uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType
                            .VOID,
                        voidedUser.id(),
                        VOIDED_DATE_TIME,
                        Map.of())));

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.just(history));
    when(userRetrievalService.getUser(voidedUser.id())).thenReturn(voidedUser);

    var summary = claimHistoryService.getClaimHistorySummary(claim);

    assertThat(summary.lastUpdatedUser()).isEqualTo(voidedUser);
    assertThat(summary.lastUpdatedDateTime()).isEqualTo(VOIDED_DATE_TIME);
    assertThat(summary.amendedFields()).isEmpty();
  }

  @Test
  void getClaimHistorySummaryReturnsNoLatestEventWhenDerivedClaimStatusIsUnsupported() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setDerivedClaimStatus(INVALID);

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    historyEvent(
                        SUBMISSION, UUID.randomUUID().toString(), CREATED_DATE_TIME, Map.of())));

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.just(history));

    var summary = claimHistoryService.getClaimHistorySummary(claim);

    assertThat(summary.lastUpdatedUser()).isNull();
    assertThat(summary.lastUpdatedDateTime()).isNull();
    assertThat(summary.amendedFields()).isEmpty();
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
            new AmendmentConfirmation(
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
        .isEqualTo(new AmendmentConfirmation(false, Set.of()));
  }

  private static LinkedHashMap<String, String> change(String source, String fieldIdentifier) {
    var change = new LinkedHashMap<String, String>();
    change.put("change_source", source);
    change.put("field_identifier", fieldIdentifier);
    return change;
  }

  private static LinkedHashMap<String, Object> change(
      String source, String fieldIdentifier, Object before, Object after) {
    var change = new LinkedHashMap<String, Object>();
    change.put("change_source", source);
    change.put("field_identifier", fieldIdentifier);
    change.put("before", before);
    change.put("after", after);
    return change;
  }

  private static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent historyEvent(
      uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType eventType,
      String actorId,
      OffsetDateTime eventTimestamp,
      Map<String, Object> metadata) {
    return new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
        .eventType(eventType)
        .actorId(actorId)
        .eventTimestamp(eventTimestamp)
        .metadata(metadata);
  }
}
