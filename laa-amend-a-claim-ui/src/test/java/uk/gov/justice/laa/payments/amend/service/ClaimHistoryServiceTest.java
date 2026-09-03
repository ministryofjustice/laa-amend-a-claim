package uk.gov.justice.laa.payments.amend.service;

import static java.time.ZoneOffset.UTC;
import static java.util.stream.Stream.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.AMENDMENT;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.ASSESSMENT;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.SUBMISSION;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.VOID;
import static uk.gov.justice.laa.payments.amend.models.enums.AssessmentTypeEnum.ESCAPE_CASE_ASSESSMENT;
import static uk.gov.justice.laa.payments.amend.models.enums.AssessmentTypeEnum.STAGE_DISBURSEMENT_ASSESSMENT;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.ACCEPTED;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.AMENDED;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.ASSESSED;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.INVALID;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.VOIDED;
import static uk.gov.justice.laa.payments.amend.models.enums.OutcomeType.PAID_IN_FULL;
import static uk.gov.justice.laa.payments.amend.models.enums.OutcomeType.REDUCED;

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
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryResultSet;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;
import uk.gov.justice.laa.payments.amend.config.FeatureFlagsConfig;
import uk.gov.justice.laa.payments.amend.models.AmendmentConfirmation;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAssessedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryCreatedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryFspEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryVoidedEvent;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;
import uk.gov.justice.laadata.providers.model.ProviderFirmSummary;

@ExtendWith(MockitoExtension.class)
public class ClaimHistoryServiceTest {

  private static final String PROVIDER_NAME = "Some Provider";
  private static final String OFFICE_CODE = "123456";

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

  @Mock private ClaimsApiClient claimsApiClient;

  @Mock private ProviderService providerService;

  @Mock private UserRetrievalService userRetrievalService;

  @Mock private ClaimHistoryAmendmentsService claimHistoryAmendmentsService;

  @Mock private FeatureFlagsConfig featureFlagsConfig;

  private ClaimHistoryService claimHistoryService;

  @BeforeEach
  void setUp() {
    claimHistoryService =
        new ClaimHistoryService(
            claimsApiClient,
            providerService,
            userRetrievalService,
            claimHistoryAmendmentsService,
            featureFlagsConfig);
  }

  @Test
  void getClaimHistory() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setEscaped(true);

    var providerFirm =
        ProviderFirmOfficeDto.builder()
            .firm(ProviderFirmSummary.builder().firmName(PROVIDER_NAME).build())
            .build();
    when(providerService.getProviderFirm(OFFICE_CODE)).thenReturn(providerFirm);

    when(userRetrievalService.getUser(ESCAPE_CASE_ASSESSED_USER.id()))
        .thenReturn(ESCAPE_CASE_ASSESSED_USER);
    when(userRetrievalService.getUser(STAGE_DISBURSEMENT_ASSESSED_USER.id()))
        .thenReturn(STAGE_DISBURSEMENT_ASSESSED_USER);
    when(userRetrievalService.getUser(VOIDED_USER.id())).thenReturn(VOIDED_USER);

    when(claimsApiClient.getClaimHistory(claim.getClaimId()))
        .thenReturn(
            Mono.just(
                new ClaimHistoryResultSet()
                    .claimId(claim.getClaimId())
                    .events(
                        List.of(
                            historyEvent(
                                SUBMISSION,
                                UUID.randomUUID().toString(),
                                CREATED_DATE_TIME,
                                Map.of("office_account_number", OFFICE_CODE)),
                            historyEvent(
                                ASSESSMENT,
                                ESCAPE_CASE_ASSESSED_USER.id(),
                                ESCAPE_CASE_ASSESSED_DATE_TIME,
                                Map.of(
                                    "assessment_type",
                                    "ESCAPE_CASE_ASSESSMENT",
                                    "assessment_outcome",
                                    "REDUCED_STILL_ESCAPED")),
                            historyEvent(
                                ASSESSMENT,
                                STAGE_DISBURSEMENT_ASSESSED_USER.id(),
                                STAGE_DISBURSEMENT_ASSESSED_DATE_TIME,
                                Map.of(
                                    "assessment_type",
                                    "STAGE_DISBURSEMENT_ASSESSMENT",
                                    "assessment_outcome",
                                    "PAID_IN_FULL")),
                            historyEvent(
                                VOID,
                                VOIDED_USER.id(),
                                VOIDED_DATE_TIME,
                                Map.of(
                                    "assessment_type", "VOID", "assessment_reason", "Voided"))))));
    when(claimHistoryAmendmentsService.toAmendmentClaimHistoryEventsFromApiEvents(anyList(), any()))
        .thenReturn(empty());
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

    assertThat(claimHistory.lastUpdatedUser()).isEqualTo(ESCAPE_CASE_ASSESSED_USER);
    assertThat(claimHistory.lastUpdatedDateTime()).isEqualTo(ESCAPE_CASE_ASSESSED_DATE_TIME);
    assertThat(claimHistory.events())
        .containsExactly(
            voidedEvent, assessedStageDisbursementEvent, assessedEscapeCaseEvent, createdEvent);
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
                    new ClaimHistoryEvent()
                        .eventType(ASSESSMENT)
                        .metadata(Map.of("changes", requestedChanges)),
                    new ClaimHistoryEvent()
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

    claim.setLastUpdatedUser(null); // No user is set until the first non submission event occurs
    claim.setLastUpdatedDateTime(submittedDateTime);

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    historyEvent(
                        SUBMISSION, "Data-Claims-Event-Service", submittedDateTime, Map.of()),
                    historyEvent(
                        AMENDMENT,
                        UUID.randomUUID().toString(),
                        CREATED_DATE_TIME.plusDays(1),
                        Map.of())));

    when(claimsApiClient.getClaimHistory(claim.getClaimId())).thenReturn(Mono.just(history));
    verifyNoInteractions(userRetrievalService);

    var summary = claimHistoryService.getClaimHistorySummary(claim);

    assertThat(summary.lastUpdatedUser()).isNull();
    assertThat(summary.lastUpdatedDateTime()).isEqualTo(submittedDateTime);
    assertThat(summary.amendedFields()).isEmpty();

    verifyNoInteractions(userRetrievalService);
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
            .events(List.of(historyEvent(VOID, voidedUser.id(), VOIDED_DATE_TIME, Map.of())));

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
  void getClaimHistoryIncludesFspEventWhenFeatureFlagEnabled() {
    var claim = MockClaimsFunctions.createMockCivilClaim();

    var providerFirm =
        ProviderFirmOfficeDto.builder()
            .firm(ProviderFirmSummary.builder().firmName(PROVIDER_NAME).build())
            .build();
    when(providerService.getProviderFirm(OFFICE_CODE)).thenReturn(providerFirm);

    when(featureFlagsConfig.isFspHistoryEnabled()).thenReturn(true);
    when(claimsApiClient.getClaimHistory(claim.getClaimId()))
        .thenReturn(
            Mono.just(
                new ClaimHistoryResultSet()
                    .claimId(claim.getClaimId())
                    .events(
                        List.of(
                            historyEvent(
                                SUBMISSION,
                                UUID.randomUUID().toString(),
                                CREATED_DATE_TIME,
                                Map.of("office_account_number", OFFICE_CODE)),
                            historyEvent(
                                AMENDMENT,
                                UUID.randomUUID().toString(),
                                CREATED_DATE_TIME.plusDays(1),
                                Map.of())))));

    var fspEvent =
        new ClaimHistoryFspEvent(CREATED_DATE_TIME.plusDays(1), null, null, null, List.of());
    var amendedEvent =
        new uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAmendedEvent(
            CREATED_DATE_TIME.plusDays(1), null, List.of(), null, null);

    when(claimHistoryAmendmentsService.toFspClaimHistoryEventsFromApiEvents(anyList(), any()))
        .thenReturn(java.util.stream.Stream.of(fspEvent));
    when(claimHistoryAmendmentsService.toAmendmentClaimHistoryEventsFromApiEvents(anyList(), any()))
        .thenReturn(java.util.stream.Stream.of(amendedEvent));

    var claimHistory = claimHistoryService.getClaimHistory(claim);

    assertThat(claimHistory.events())
        .containsExactly(
            fspEvent,
            amendedEvent,
            new ClaimHistoryCreatedEvent(
                CREATED_DATE_TIME, PROVIDER_NAME, Boolean.TRUE.equals(claim.getEscaped())));
  }

  @Test
  void getClaimHistorySkipsFspEventsWhenFeatureFlagDisabled() {
    var claim = MockClaimsFunctions.createMockCivilClaim();

    var providerFirm =
        ProviderFirmOfficeDto.builder()
            .firm(ProviderFirmSummary.builder().firmName(PROVIDER_NAME).build())
            .build();
    when(providerService.getProviderFirm(OFFICE_CODE)).thenReturn(providerFirm);

    when(featureFlagsConfig.isFspHistoryEnabled()).thenReturn(false);
    when(claimsApiClient.getClaimHistory(claim.getClaimId()))
        .thenReturn(
            Mono.just(
                new ClaimHistoryResultSet()
                    .claimId(claim.getClaimId())
                    .events(
                        List.of(
                            historyEvent(
                                SUBMISSION,
                                UUID.randomUUID().toString(),
                                CREATED_DATE_TIME,
                                Map.of("office_account_number", OFFICE_CODE)),
                            historyEvent(
                                AMENDMENT,
                                UUID.randomUUID().toString(),
                                CREATED_DATE_TIME.plusDays(1),
                                Map.of())))));

    when(claimHistoryAmendmentsService.toAmendmentClaimHistoryEventsFromApiEvents(anyList(), any()))
        .thenReturn(empty());

    claimHistoryService.getClaimHistory(claim);

    verify(claimHistoryAmendmentsService, never())
        .toFspClaimHistoryEventsFromApiEvents(anyList(), any());
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
                    new ClaimHistoryEvent()
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
                    new ClaimHistoryEvent()
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

  private static ClaimHistoryEvent historyEvent(
      uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType eventType,
      String actorId,
      OffsetDateTime eventTimestamp,
      Map<String, Object> metadata) {
    return new ClaimHistoryEvent()
        .eventType(eventType)
        .actorId(actorId)
        .eventTimestamp(eventTimestamp)
        .metadata(metadata);
  }
}
