package uk.gov.justice.laa.amend.claim.service;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.OriginalAndCurrent;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimPatch;

@ExtendWith(MockitoExtension.class)
class CheckAmendmentsServiceTest {

  private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  @Mock private ClaimsApiClient claimsApiClient;

  private CheckAmendmentsService checkAmendmentsService;

  @BeforeEach
  void setUp() {
    checkAmendmentsService = new CheckAmendmentsService(claimsApiClient);
  }

  @Test
  void submitPopulatesCrimePatchFromAllFormFields() {
    var submissionId = UUID.randomUUID();
    var claimId = UUID.randomUUID();
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setVersion(1L);

    var amendmentForms =
        amendmentForms(
            forms(
                Map.ofEntries(
                    entry("INITIAL", "OLD_INITIAL"),
                    entry("SURNAME", "OLD_SURNAME"),
                    entry("GENDER", "OLD_GENDER"),
                    entry("ETHNICITY", "OLD_ETHNICITY"),
                    entry("DISABILITY", "OLD_DISABILITY")),
                Map.ofEntries(
                    entry("INITIAL", "NEW_INITIAL"),
                    entry("SURNAME", "NEW_SURNAME"),
                    entry("GENDER", "NEW_GENDER"),
                    entry("ETHNICITY", "NEW_ETHNICITY"),
                    entry("DISABILITY", "NEW_DISABILITY"))),
            forms(
                Map.ofEntries(
                    entry("FEE_CODE", "OLD_FEE"), entry("MATTER_TYPE_CODE", "OLD_MATTER")),
                Map.ofEntries(
                    entry("FEE_CODE", "NEW_FEE"), entry("MATTER_TYPE_CODE", "NEW_MATTER"))),
            forms(
                Map.ofEntries(
                    entry("STAGE_REACHED", "OLD_STAGE"),
                    entry("UNIQUE_FILE_NUMBER", "OLD_UFN"),
                    entry("REPRESENTATION_ORDER_DATE-day", "1"),
                    entry("REPRESENTATION_ORDER_DATE-month", "1"),
                    entry("REPRESENTATION_ORDER_DATE-year", "2024"),
                    entry("CASE_CONCLUDED_DATE-day", "2"),
                    entry("CASE_CONCLUDED_DATE-month", "1"),
                    entry("CASE_CONCLUDED_DATE-year", "2024"),
                    entry("STANDARD_FEE_CATEGORY", "OLD_SFC"),
                    entry("OUTCOME_FOR_CLIENT", "OLD_OUTCOME"),
                    entry("SUSPECTS_DEFENDANTS_COUNT", "1"),
                    entry("POLICE_STATION_COURT_ATTENDANCES_COUNT", "1"),
                    entry("POLICE_STATION_COURT_PRISON_ID", "OLD_PRISON"),
                    entry("SCHEME_ID", "OLD_SCHEME"),
                    entry("DSCC_NUMBER", "OLD_DSCC"),
                    entry("MAAT_ID", "OLD_MAAT"),
                    entry("PRISON_LAW_PRIOR_APPROVAL_NUMBER", "OLD_PLPA"),
                    entry("IS_DUTY_SOLICITOR", "false"),
                    entry("IS_YOUTH_COURT", "true")),
                Map.ofEntries(
                    entry("STAGE_REACHED", "NEW_STAGE"),
                    entry("UNIQUE_FILE_NUMBER", "NEW_UFN"),
                    entry("REPRESENTATION_ORDER_DATE-day", "30"),
                    entry("REPRESENTATION_ORDER_DATE-month", "6"),
                    entry("REPRESENTATION_ORDER_DATE-year", "2026"),
                    entry("CASE_CONCLUDED_DATE-day", "1"),
                    entry("CASE_CONCLUDED_DATE-month", "7"),
                    entry("CASE_CONCLUDED_DATE-year", "2026"),
                    entry("STANDARD_FEE_CATEGORY", "NEW_SFC"),
                    entry("OUTCOME_FOR_CLIENT", "NEW_OUTCOME"),
                    entry("SUSPECTS_DEFENDANTS_COUNT", "7"),
                    entry("POLICE_STATION_COURT_ATTENDANCES_COUNT", "5"),
                    entry("POLICE_STATION_COURT_PRISON_ID", "NEW_PRISON"),
                    entry("SCHEME_ID", "NEW_SCHEME"),
                    entry("DSCC_NUMBER", "NEW_DSCC"),
                    entry("MAAT_ID", "NEW_MAAT"),
                    entry("PRISON_LAW_PRIOR_APPROVAL_NUMBER", "NEW_PLPA"),
                    entry("IS_DUTY_SOLICITOR", "true"),
                    entry("IS_YOUTH_COURT", "false"))),
            null,
            forms(
                Map.ofEntries(
                    entry("FIXED_FEE", "100.00"),
                    entry("PROFIT_COST", "10.00"),
                    entry("DISBURSEMENTS", "20.00"),
                    entry("TRAVEL_COSTS", "30.00"),
                    entry("WAITING_COSTS", "40.00"),
                    entry("VAT", "false"),
                    entry("DISBURSEMENTS_VAT", "50.00")),
                Map.ofEntries(
                    entry("FIXED_FEE", "100.00"),
                    entry("PROFIT_COST", "11.00"),
                    entry("DISBURSEMENTS", "21.00"),
                    entry("TRAVEL_COSTS", "31.00"),
                    entry("WAITING_COSTS", "41.00"),
                    entry("VAT", "true"),
                    entry("DISBURSEMENTS_VAT", "51.00"))));

    var patch = submitAndCapturePatch(submissionId, claimId, claim, amendmentForms);

    var expected =
        ClaimPatch.builder()
            .amendmentUserId(patch.getAmendmentUserId())
            .amendmentReasonCode("CASE_REOPENED_REBILLED")
            .amendmentRequestedBy("PROVIDER")
            .version(1L)
            .clientForename("NEW_INITIAL")
            .clientSurname("NEW_SURNAME")
            .genderCode("NEW_GENDER")
            .ethnicityCode("NEW_ETHNICITY")
            .disabilityCode("NEW_DISABILITY")
            .feeCode("NEW_FEE")
            .crimeMatterTypeCode("NEW_MATTER")
            .stageReachedCode("NEW_STAGE")
            .uniqueFileNumber("NEW_UFN")
            .representationOrderDate("30/06/2026")
            .caseConcludedDate("01/07/2026")
            .standardFeeCategoryCode("NEW_SFC")
            .outcomeCode("NEW_OUTCOME")
            .suspectsDefendantsCount(7)
            .policeStationCourtAttendancesCount(5)
            .policeStationCourtPrisonId("NEW_PRISON")
            .schemeId("NEW_SCHEME")
            .dsccNumber("NEW_DSCC")
            .maatId("NEW_MAAT")
            .prisonLawPriorApprovalNumber("NEW_PLPA")
            .isDutySolicitor(true)
            .isYouthCourt(false)
            .netProfitCostsAmount(new BigDecimal("11.00"))
            .netDisbursementAmount(new BigDecimal("21.00"))
            .travelWaitingCostsAmount(new BigDecimal("31.00"))
            .netWaitingCostsAmount(new BigDecimal("41.00"))
            .isVatApplicable(true)
            .disbursementsVatAmount(new BigDecimal("51.00"))
            .build();

    assertThat(patch).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void submitPopulatesCivilPatchFromAllFormFields() {
    var submissionId = UUID.randomUUID();
    var claimId = UUID.randomUUID();
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setVersion(1L);

    var amendmentForms =
        amendmentForms(
            forms(
                Map.ofEntries(
                    entry("FORENAME", "OLD_FORENAME"),
                    entry("SURNAME", "OLD_SURNAME"),
                    entry("DATE_OF_BIRTH-day", "1"),
                    entry("DATE_OF_BIRTH-month", "1"),
                    entry("DATE_OF_BIRTH-year", "1990"),
                    entry("GENDER", "OLD_GENDER"),
                    entry("ETHNICITY", "OLD_ETHNICITY"),
                    entry("DISABILITY", "OLD_DISABILITY"),
                    entry("POSTCODE", "OLD_POSTCODE"),
                    entry("IS_ELIGIBLE_CLIENT", "false"),
                    entry("CLIENT_TYPE", "OLD_CLIENT_TYPE"),
                    entry("UNIQUE_CLIENT_NUMBER", "OLD_UCN"),
                    entry("HOME_OFFICE_CLIENT_NUMBER", "OLD_HO"),
                    entry("IS_POSTAL_APPLICATION_ACCEPTED", "false")),
                Map.ofEntries(
                    entry("FORENAME", "NEW_FORENAME"),
                    entry("SURNAME", "NEW_SURNAME"),
                    entry("DATE_OF_BIRTH-day", "30"),
                    entry("DATE_OF_BIRTH-month", "6"),
                    entry("DATE_OF_BIRTH-year", "2026"),
                    entry("GENDER", "NEW_GENDER"),
                    entry("ETHNICITY", "NEW_ETHNICITY"),
                    entry("DISABILITY", "NEW_DISABILITY"),
                    entry("POSTCODE", "NEW_POSTCODE"),
                    entry("IS_ELIGIBLE_CLIENT", "true"),
                    entry("CLIENT_TYPE", "NEW_CLIENT_TYPE"),
                    entry("UNIQUE_CLIENT_NUMBER", "NEW_UCN"),
                    entry("HOME_OFFICE_CLIENT_NUMBER", "NEW_HO"),
                    entry("IS_POSTAL_APPLICATION_ACCEPTED", "true"))),
            forms(
                Map.ofEntries(
                    entry("FEE_CODE", "OLD_FEE"),
                    entry("MATTER_TYPE_CODE_1", "OLD1"),
                    entry("MATTER_TYPE_CODE_2", "OLD2")),
                Map.ofEntries(
                    entry("FEE_CODE", "NEW_FEE"),
                    entry("MATTER_TYPE_CODE_1", "ABCD"),
                    entry("MATTER_TYPE_CODE_2", "EFGH"))),
            forms(
                Map.ofEntries(
                    entry("SCHEDULE_REFERENCE_CIVIL", "OLD_SCHED"),
                    entry("CASE_ID", "OLD_CASE_ID"),
                    entry("CASE_REFERENCE_NUMBER", "OLD_CASE_REF"),
                    entry("CASE_START_DATE-day", "1"),
                    entry("CASE_START_DATE-month", "1"),
                    entry("CASE_START_DATE-year", "2024"),
                    entry("CASE_CONCLUDED_CLAIMED_DATE-day", "2"),
                    entry("CASE_CONCLUDED_CLAIMED_DATE-month", "1"),
                    entry("CASE_CONCLUDED_CLAIMED_DATE-year", "2024"),
                    entry("UNIQUE_FILE_NUMBER", "OLD_UFN"),
                    entry("CASE_STAGE", "OLD_CASE_STAGE"),
                    entry("VALUE_OF_COSTS", "100.00"),
                    entry("PROCUREMENT_AREA", "OLD_PROCUREMENT"),
                    entry("ACCESS_POINT", "OLD_ACCESS"),
                    entry("STAGE_REACHED", "OLD_STAGE_REACHED"),
                    entry("OUTCOME_FOR_CLIENT", "OLD_OUTCOME"),
                    entry("EXCEPTIONAL_CASE_FUNDING", "OLD_ECF"),
                    entry("CIVIL_LEGAL_ADVICE_REFERENCE", "OLD_CLA_REF"),
                    entry("CIVIL_LEGAL_ADVICE_EXEMPTION", "OLD_CLA_EXEMPT"),
                    entry("DELIVERY_LOCATION", "OLD_DELIVERY"),
                    entry("COURT_LOCATION", "OLD_COURT"),
                    entry("AIT_HEARING_CENTRE", "OLD_AIT"),
                    entry("LOCAL_AUTHORITY_NUMBER", "OLD_LA"),
                    entry("DESIGNATED_ACCREDITED_REPRESENTATIVE", "OLD_DAR"),
                    entry("ADVICE_TIME", "1"),
                    entry("TRAVEL_TIME", "2"),
                    entry("WAITING_TIME", "3"),
                    entry("ADDITIONAL_TRAVEL_PAYMENT", "false"),
                    entry("FOLLOW_ON_WORK", "OLD_FOLLOW"),
                    entry("TOLERANCE_INDICATOR", "true"),
                    entry("LEGACY_CASE", "false"),
                    entry("MEETINGS_ATTENDED", "OLD_MEET"),
                    entry("ADVICE_TYPE", "OLD_ADVICE_TYPE"),
                    entry("TRANSFER_DATE-day", "3"),
                    entry("TRANSFER_DATE-month", "1"),
                    entry("TRANSFER_DATE-year", "2024"),
                    entry("MEDICAL_REPORTS_CLAIMED", "1"),
                    entry("EXEMPTION_CRITERIA_SATISFIED", "OLD_EXEMPT"),
                    entry("IRC_SURGERY", "false"),
                    entry("SURGERY_DATE-day", "4"),
                    entry("SURGERY_DATE-month", "1"),
                    entry("SURGERY_DATE-year", "2024"),
                    entry("SURGERY_CLIENTS_COUNT", "1"),
                    entry("SURGERY_MATTERS_COUNT", "1"),
                    entry("MENTAL_HEALTH_TRIBUNAL_REFERENCE", "OLD_MHT"),
                    entry("IS_NRM_ADVICE", "true")),
                Map.ofEntries(
                    entry("SCHEDULE_REFERENCE_CIVIL", "NEW_SCHED"),
                    entry("CASE_ID", "NEW_CASE_ID"),
                    entry("CASE_REFERENCE_NUMBER", "NEW_CASE_REF"),
                    entry("CASE_START_DATE-day", "10"),
                    entry("CASE_START_DATE-month", "7"),
                    entry("CASE_START_DATE-year", "2026"),
                    entry("CASE_CONCLUDED_CLAIMED_DATE-day", "11"),
                    entry("CASE_CONCLUDED_CLAIMED_DATE-month", "7"),
                    entry("CASE_CONCLUDED_CLAIMED_DATE-year", "2026"),
                    entry("UNIQUE_FILE_NUMBER", "NEW_UFN"),
                    entry("CASE_STAGE", "NEW_CASE_STAGE"),
                    entry("VALUE_OF_COSTS", "123.45"),
                    entry("PROCUREMENT_AREA", "NEW_PROCUREMENT"),
                    entry("ACCESS_POINT", "NEW_ACCESS"),
                    entry("STAGE_REACHED", "NEW_STAGE_REACHED"),
                    entry("OUTCOME_FOR_CLIENT", "NEW_OUTCOME"),
                    entry("EXCEPTIONAL_CASE_FUNDING", "NEW_ECF"),
                    entry("CIVIL_LEGAL_ADVICE_REFERENCE", "NEW_CLA_REF"),
                    entry("CIVIL_LEGAL_ADVICE_EXEMPTION", "NEW_CLA_EXEMPT"),
                    entry("DELIVERY_LOCATION", "NEW_DELIVERY"),
                    entry("COURT_LOCATION", "NEW_COURT"),
                    entry("AIT_HEARING_CENTRE", "NEW_AIT"),
                    entry("LOCAL_AUTHORITY_NUMBER", "NEW_LA"),
                    entry("DESIGNATED_ACCREDITED_REPRESENTATIVE", "NEW_DAR"),
                    entry("ADVICE_TIME", "10"),
                    entry("TRAVEL_TIME", "20"),
                    entry("WAITING_TIME", "30"),
                    entry("ADDITIONAL_TRAVEL_PAYMENT", "true"),
                    entry("FOLLOW_ON_WORK", "NEW_FOLLOW"),
                    entry("TOLERANCE_INDICATOR", "false"),
                    entry("LEGACY_CASE", "true"),
                    entry("MEETINGS_ATTENDED", "NEW_MEET"),
                    entry("ADVICE_TYPE", "NEW_ADVICE_TYPE"),
                    entry("TRANSFER_DATE-day", "15"),
                    entry("TRANSFER_DATE-month", "8"),
                    entry("TRANSFER_DATE-year", "2026"),
                    entry("MEDICAL_REPORTS_CLAIMED", "4"),
                    entry("EXEMPTION_CRITERIA_SATISFIED", "NEW_EXEMPT"),
                    entry("IRC_SURGERY", "true"),
                    entry("SURGERY_DATE-day", "20"),
                    entry("SURGERY_DATE-month", "9"),
                    entry("SURGERY_DATE-year", "2026"),
                    entry("SURGERY_CLIENTS_COUNT", "5"),
                    entry("SURGERY_MATTERS_COUNT", "6"),
                    entry("MENTAL_HEALTH_TRIBUNAL_REFERENCE", "NEW_MHT"),
                    entry("IS_NRM_ADVICE", "false"))),
            null,
            forms(
                Map.ofEntries(
                    entry("FIXED_FEE", "200.00"),
                    entry("PROFIT_COST", "10.00"),
                    entry("DISBURSEMENTS", "20.00"),
                    entry("COUNSELS_COST", "30.00"),
                    entry("DISBURSEMENTS_VAT", "40.00"),
                    entry("TRAVEL_AND_WAITING_COSTS", "50.00"),
                    entry("VAT", "false"),
                    entry("ADJOURNED_HEARING_FEE", "1"),
                    entry("DETENTION_TRAVEL", "60.00"),
                    entry("JR_FORM_FILLING", "70.00"),
                    entry("SUBSTANTIVE_HEARING", "false"),
                    entry("HOME_OFFICE", "2"),
                    entry("CMRH_ORAL", "3"),
                    entry("CMRH_TELEPHONE", "4"),
                    entry("IS_LONDON_RATE", "false"),
                    entry("PRIOR_AUTHORITY_REFERENCE", "OLD_PRIOR")),
                Map.ofEntries(
                    entry("FIXED_FEE", "200.00"),
                    entry("PROFIT_COST", "11.00"),
                    entry("DISBURSEMENTS", "21.00"),
                    entry("COUNSELS_COST", "31.00"),
                    entry("DISBURSEMENTS_VAT", "41.00"),
                    entry("TRAVEL_AND_WAITING_COSTS", "51.00"),
                    entry("VAT", "true"),
                    entry("ADJOURNED_HEARING_FEE", "2"),
                    entry("DETENTION_TRAVEL", "61.00"),
                    entry("JR_FORM_FILLING", "71.00"),
                    entry("SUBSTANTIVE_HEARING", "true"),
                    entry("HOME_OFFICE", "3"),
                    entry("CMRH_ORAL", "4"),
                    entry("CMRH_TELEPHONE", "5"),
                    entry("IS_LONDON_RATE", "true"),
                    entry("PRIOR_AUTHORITY_REFERENCE", "NEW_PRIOR"))));

    var patch = submitAndCapturePatch(submissionId, claimId, claim, amendmentForms);

    var expected =
        ClaimPatch.builder()
            .amendmentUserId(patch.getAmendmentUserId())
            .amendmentReasonCode("CASE_REOPENED_REBILLED")
            .amendmentRequestedBy("PROVIDER")
            .version(1L)
            .clientForename("NEW_FORENAME")
            .clientSurname("NEW_SURNAME")
            .clientDateOfBirth("30/06/2026")
            .genderCode("NEW_GENDER")
            .ethnicityCode("NEW_ETHNICITY")
            .disabilityCode("NEW_DISABILITY")
            .clientPostcode("NEW_POSTCODE")
            .isEligibleClient(true)
            .clientTypeCode("NEW_CLIENT_TYPE")
            .uniqueClientNumber("NEW_UCN")
            .homeOfficeClientNumber("NEW_HO")
            .isPostalApplicationAccepted(true)
            .feeCode("NEW_FEE")
            .matterTypeCode("ABCD:EFGH")
            .scheduleReference("NEW_SCHED")
            .caseId("NEW_CASE_ID")
            .caseReferenceNumber("NEW_CASE_REF")
            .caseStartDate("10/07/2026")
            .caseConcludedDate("11/07/2026")
            .uniqueFileNumber("NEW_UFN")
            .caseStageCode("NEW_CASE_STAGE")
            .costsDamagesRecoveredAmount(new BigDecimal("123.45"))
            .procurementAreaCode("NEW_PROCUREMENT")
            .accessPointCode("NEW_ACCESS")
            .stageReachedCode("NEW_STAGE_REACHED")
            .outcomeCode("NEW_OUTCOME")
            .exceptionalCaseFundingReference("NEW_ECF")
            .claReferenceNumber("NEW_CLA_REF")
            .claExemptionCode("NEW_CLA_EXEMPT")
            .deliveryLocation("NEW_DELIVERY")
            .courtLocationCode("NEW_COURT")
            .aitHearingCentreCode("NEW_AIT")
            .localAuthorityNumber("NEW_LA")
            .designatedAccreditedRepresentativeCode("NEW_DAR")
            .adviceTime(10)
            .travelTime(20)
            .waitingTime(30)
            .isAdditionalTravelPayment(true)
            .followOnWork("NEW_FOLLOW")
            .isToleranceApplicable(false)
            .isLegacyCase(true)
            .meetingsAttendedCode("NEW_MEET")
            .adviceTypeCode("NEW_ADVICE_TYPE")
            .transferDate("15/08/2026")
            .medicalReportsCount(4)
            .exemptionCriteriaSatisfied("NEW_EXEMPT")
            .isIrcSurgery(true)
            .surgeryDate("20/09/2026")
            .surgeryClientsCount(5)
            .surgeryMattersCount(6)
            .mentalHealthTribunalReference("NEW_MHT")
            .isNrmAdvice(false)
            .netProfitCostsAmount(new BigDecimal("11.00"))
            .netDisbursementAmount(new BigDecimal("21.00"))
            .netCounselCostsAmount(new BigDecimal("31.00"))
            .disbursementsVatAmount(new BigDecimal("41.00"))
            .travelWaitingCostsAmount(new BigDecimal("51.00"))
            .isVatApplicable(true)
            .adjournedHearingFeeAmount(2)
            .detentionTravelWaitingCostsAmount(new BigDecimal("61.00"))
            .jrFormFillingAmount(new BigDecimal("71.00"))
            .isSubstantiveHearing(true)
            .hoInterview(3)
            .cmrhOralCount(4)
            .cmrhTelephoneCount(5)
            .isLondonRate(true)
            .priorAuthorityReference("NEW_PRIOR")
            .build();

    assertThat(patch).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void submitPopulatesMediationPatchFromAllFormFields() {
    var submissionId = UUID.randomUUID();
    var claimId = UUID.randomUUID();
    var claim = MockClaimsFunctions.createMockMediationClaim();
    claim.setVersion(1L);

    var amendmentForms =
        amendmentForms(
            forms(
                Map.ofEntries(
                    entry("FORENAME", "OLD_FORENAME"),
                    entry("SURNAME", "OLD_SURNAME"),
                    entry("DATE_OF_BIRTH-day", "1"),
                    entry("DATE_OF_BIRTH-month", "1"),
                    entry("DATE_OF_BIRTH-year", "1990"),
                    entry("UNIQUE_CLIENT_NUMBER", "OLD_UCN"),
                    entry("POSTCODE", "OLD_POSTCODE"),
                    entry("GENDER", "OLD_GENDER"),
                    entry("ETHNICITY", "OLD_ETHNICITY"),
                    entry("DISABILITY", "OLD_DISABILITY"),
                    entry("IS_LEGALLY_AIDED", "false"),
                    entry("IS_POSTAL_APPLICATION_ACCEPTED", "false")),
                Map.ofEntries(
                    entry("FORENAME", "NEW_FORENAME"),
                    entry("SURNAME", "NEW_SURNAME"),
                    entry("DATE_OF_BIRTH-day", "30"),
                    entry("DATE_OF_BIRTH-month", "6"),
                    entry("DATE_OF_BIRTH-year", "2026"),
                    entry("UNIQUE_CLIENT_NUMBER", "NEW_UCN"),
                    entry("POSTCODE", "NEW_POSTCODE"),
                    entry("GENDER", "NEW_GENDER"),
                    entry("ETHNICITY", "NEW_ETHNICITY"),
                    entry("DISABILITY", "NEW_DISABILITY"),
                    entry("IS_LEGALLY_AIDED", "true"),
                    entry("IS_POSTAL_APPLICATION_ACCEPTED", "true"))),
            forms(
                Map.ofEntries(
                    entry("FEE_CODE", "OLD_FEE"),
                    entry("MATTER_TYPE_CODE_1", "OLD_MATTER1"),
                    entry("MATTER_TYPE_CODE_2", "OLD_MATTER2")),
                Map.ofEntries(
                    entry("FEE_CODE", "NEW_FEE"),
                    entry("MATTER_TYPE_CODE_1", "NEW_MATTER1"),
                    entry("MATTER_TYPE_CODE_2", "NEW_MATTER2"))),
            forms(
                Map.ofEntries(
                    entry("CASE_REFERENCE_NUMBER", "OLD_CASE_REF"),
                    entry("CASE_START_DATE-day", "1"),
                    entry("CASE_START_DATE-month", "1"),
                    entry("CASE_START_DATE-year", "2024"),
                    entry("CLAIM_ID", "OLD_CLAIM_ID"),
                    entry("UNIQUE_CASE_ID", "OLD_UNIQUE_CASE"),
                    entry("CASE_CONCLUDED_DATE-day", "2"),
                    entry("CASE_CONCLUDED_DATE-month", "1"),
                    entry("CASE_CONCLUDED_DATE-year", "2024"),
                    entry("MEDIATION_SESSIONS_COUNT", "1"),
                    entry("MEDIATION_TIME_MINUTES", "15"),
                    entry("OUTCOME", "OLD_OUTCOME"),
                    entry("OUTREACH_LOCATION", "OLD_OUTREACH"),
                    entry("REFERRAL_SOURCE", "OLD_REFERRAL"),
                    entry("SCHEDULE_REFERENCE", "OLD_SCHEDULE")),
                Map.ofEntries(
                    entry("CASE_REFERENCE_NUMBER", "NEW_CASE_REF"),
                    entry("CASE_START_DATE-day", "5"),
                    entry("CASE_START_DATE-month", "7"),
                    entry("CASE_START_DATE-year", "2026"),
                    entry("CLAIM_ID", "NEW_CLAIM_ID"),
                    entry("UNIQUE_CASE_ID", "NEW_UNIQUE_CASE"),
                    entry("CASE_CONCLUDED_DATE-day", "6"),
                    entry("CASE_CONCLUDED_DATE-month", "7"),
                    entry("CASE_CONCLUDED_DATE-year", "2026"),
                    entry("MEDIATION_SESSIONS_COUNT", "8"),
                    entry("MEDIATION_TIME_MINUTES", "240"),
                    entry("OUTCOME", "NEW_OUTCOME"),
                    entry("OUTREACH_LOCATION", "NEW_OUTREACH"),
                    entry("REFERRAL_SOURCE", "NEW_REFERRAL"),
                    entry("SCHEDULE_REFERENCE", "NEW_SCHEDULE"))),
            forms(
                Map.ofEntries(
                    entry("CLIENT_2_FORENAME", "OLD_C2_FORENAME"),
                    entry("CLIENT_2_SURNAME", "OLD_C2_SURNAME"),
                    entry("CLIENT_2_DATE_OF_BIRTH-day", "1"),
                    entry("CLIENT_2_DATE_OF_BIRTH-month", "1"),
                    entry("CLIENT_2_DATE_OF_BIRTH-year", "1991"),
                    entry("CLIENT_2_UCN", "OLD_C2_UCN"),
                    entry("CLIENT_2_POSTCODE", "OLD_C2_POSTCODE"),
                    entry("CLIENT_2_GENDER", "OLD_C2_GENDER"),
                    entry("CLIENT_2_ETHNICITY", "OLD_C2_ETHNICITY"),
                    entry("CLIENT_2_DISABILITY", "OLD_C2_DISABILITY"),
                    entry("IS_CLIENT_2_LEGALLY_AIDED", "false"),
                    entry("IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED", "false")),
                Map.ofEntries(
                    entry("CLIENT_2_FORENAME", "NEW_C2_FORENAME"),
                    entry("CLIENT_2_SURNAME", "NEW_C2_SURNAME"),
                    entry("CLIENT_2_DATE_OF_BIRTH-day", "12"),
                    entry("CLIENT_2_DATE_OF_BIRTH-month", "7"),
                    entry("CLIENT_2_DATE_OF_BIRTH-year", "2026"),
                    entry("CLIENT_2_UCN", "NEW_C2_UCN"),
                    entry("CLIENT_2_POSTCODE", "NEW_C2_POSTCODE"),
                    entry("CLIENT_2_GENDER", "NEW_C2_GENDER"),
                    entry("CLIENT_2_ETHNICITY", "NEW_C2_ETHNICITY"),
                    entry("CLIENT_2_DISABILITY", "NEW_C2_DISABILITY"),
                    entry("IS_CLIENT_2_LEGALLY_AIDED", "true"),
                    entry("IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED", "true"))),
            forms(
                Map.ofEntries(
                    entry("FIXED_FEE", "300.00"),
                    entry("VAT", "false"),
                    entry("DISBURSEMENTS", "30.00"),
                    entry("DISBURSEMENTS_VAT", "40.00")),
                Map.ofEntries(
                    entry("FIXED_FEE", "300.00"),
                    entry("VAT", "true"),
                    entry("DISBURSEMENTS", "31.00"),
                    entry("DISBURSEMENTS_VAT", "41.00"))));

    var patch = submitAndCapturePatch(submissionId, claimId, claim, amendmentForms);

    var expected =
        ClaimPatch.builder()
            .amendmentUserId(USER_ID)
            .amendmentReasonCode("CASE_REOPENED_REBILLED")
            .amendmentRequestedBy("PROVIDER")
            .version(1L)
            .clientForename("NEW_FORENAME")
            .clientSurname("NEW_SURNAME")
            .clientDateOfBirth("30/06/2026")
            .uniqueClientNumber("NEW_UCN")
            .clientPostcode("NEW_POSTCODE")
            .genderCode("NEW_GENDER")
            .ethnicityCode("NEW_ETHNICITY")
            .disabilityCode("NEW_DISABILITY")
            .isLegallyAided(true)
            .isPostalApplicationAccepted(true)
            .client2Forename("NEW_C2_FORENAME")
            .client2Surname("NEW_C2_SURNAME")
            .client2DateOfBirth("12/07/2026")
            .client2Ucn("NEW_C2_UCN")
            .client2Postcode("NEW_C2_POSTCODE")
            .client2GenderCode("NEW_C2_GENDER")
            .client2EthnicityCode("NEW_C2_ETHNICITY")
            .client2DisabilityCode("NEW_C2_DISABILITY")
            .client2IsLegallyAided(true)
            .isClient2PostalApplicationAccepted(true)
            .feeCode("NEW_FEE")
            .matterTypeCode("NEW_MATTER1:NEW_MATTER2")
            .caseReferenceNumber("NEW_CASE_REF")
            .caseStartDate("05/07/2026")
            .caseId("NEW_CLAIM_ID")
            .uniqueCaseId("NEW_UNIQUE_CASE")
            .caseConcludedDate("06/07/2026")
            .mediationSessionsCount(8)
            .mediationTimeMinutes(240)
            .outcomeCode("NEW_OUTCOME")
            .outreachLocation("NEW_OUTREACH")
            .referralSource("NEW_REFERRAL")
            .scheduleReference("NEW_SCHEDULE")
            .isVatApplicable(true)
            .netDisbursementAmount(new BigDecimal("31.00"))
            .disbursementsVatAmount(new BigDecimal("41.00"))
            .build();

    assertThat(patch).usingRecursiveComparison().isEqualTo(expected);
  }

  private ClaimPatch submitAndCapturePatch(
      UUID submissionId, UUID claimId, ClaimDetails claim, AmendmentForms amendmentForms) {
    when(claimsApiClient.updateClaim(eq(submissionId), eq(claimId), any(ClaimPatch.class)))
        .thenReturn(Mono.empty());

    checkAmendmentsService.submitAmendments(submissionId, claimId, USER_ID, claim, amendmentForms);

    var patchCaptor = ArgumentCaptor.forClass(ClaimPatch.class);
    verify(claimsApiClient).updateClaim(eq(submissionId), eq(claimId), patchCaptor.capture());
    return patchCaptor.getValue();
  }

  private static AmendmentForms amendmentForms(
      OriginalAndCurrent client1Form,
      OriginalAndCurrent caseTypeForm,
      OriginalAndCurrent caseDetailsForm,
      OriginalAndCurrent client2Form,
      OriginalAndCurrent costsForm) {
    var amendmentForms = new AmendmentForms();
    amendmentForms.setClient1Form(client1Form);
    amendmentForms.setCaseTypeForm(caseTypeForm);
    amendmentForms.setCaseDetailsForm(caseDetailsForm);
    amendmentForms.setClient2Form(client2Form);
    amendmentForms.setCostsForm(costsForm);
    return amendmentForms;
  }

  private static OriginalAndCurrent forms(
      Map<String, String> originalInputs, Map<String, String> currentInputs) {
    var original = new AmendmentForm();
    original.setInputs(originalInputs);

    var current = new AmendmentForm();
    current.setInputs(currentInputs);

    return new OriginalAndCurrent(original, current);
  }
}
