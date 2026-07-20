package uk.gov.justice.laa.amend.claim.views.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.amendments.AmendCaseDetailsController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;

@WebMvcTest(AmendCaseDetailsController.class)
class AmendCaseDetailsViewTest extends AmendmentsBaseTest {

  private static final String FEE_CODE = "feecode";
  private static final String MATTER_TYPE_CODE = "matter";
  private static final String MATTER_TYPE_CODE_1 = "matterone";
  private static final String MATTER_TYPE_CODE_2 = "mattertwo";
  private static final String SCHEDULE_REFERENCE = "schedulereference";
  private static final String CASE_ID = "caseid";
  private static final String CASE_REFERENCE_NUMBER = "casereferencenum";
  private static final LocalDate CASE_START_DATE = LocalDate.of(2020, 1, 1);
  private static final LocalDate CASE_CONCLUDED_DATE = LocalDate.of(2020, 2, 1);
  private static final String UFN = "ufn";
  private static final String CASE_STAGE = "MHL04";
  private static final BigDecimal VALUE_OF_COSTS = BigDecimal.valueOf(10.12);
  private static final String PROCUREMENT_AREA = "procurementarea";
  private static final String ACCESS_POINT = "accesspoint";
  private static final String STAGE_REACHED = "INVA";
  private static final String OUTCOME_FOR_CLIENT = "A";
  private static final String EXCEPTIONAL_CASE_FUNDING = "exceptionalcasefunding";
  private static final String CLA_REFERENCE = "clareference";
  private static final String CLA_EXEMPTION = "claexemption";
  private static final String DELIVERY_LOCATION = "deliverylocation";
  private static final String COURT_LOCATION = "courtlocation";
  private static final String AIT_HEARING_CENTRE = "16";
  private static final String AIT_HEARING_CENTRE_LABEL = "16 - Other";
  private static final String LOCAL_AUTHORITY_NUMBER = "localauthoritynumber";
  private static final String DESIGNATED_ACCREDITED_REPRESENTATIVE = "1";
  private static final int ADVICE_TIME = 1;
  private static final int TRAVEL_TIME = 2;
  private static final int WAITING_TIME = 3;
  private static final boolean ADDITIONAL_TRAVEL_PAYMENT = true;
  private static final String FOLLOW_ON_WORK = "followonwork";
  private static final boolean TOLERANCE_INDICATOR = true;
  private static final boolean LEGACY_CASE = true;
  private static final String MEETINGS_ATTENDED = "MTGA02";
  private static final String ADVICE_TYPE = "FTF";
  private static final LocalDate TRANSFER_DATE = LocalDate.of(2020, 3, 1);
  private static final int MEDICAL_REPORTS_CLAIMED = 4;
  private static final String EXEMPTION_CRITERIA_SATISFIED = "DV001";
  private static final boolean IRC_SURGERY = true;
  private static final LocalDate SURGERY_DATE = LocalDate.of(2020, 4, 1);
  private static final int SURGERY_CLIENTS_COUNT = 5;
  private static final int SURGERY_MATTERS_COUNT = 6;
  private static final String MENTAL_HEALTH_TRIBUNAL_REFERENCE = "mentalhealthtribunalreference";
  private static final boolean IS_NRM_ADVICE = true;
  private static final LocalDate REPRESENTATION_ORDER_DATE = LocalDate.of(2020, 5, 1);
  private static final String STANDARD_FEE_CATEGORY = "1EW";
  private static final int SUSPECTS_DEFENDANTS_COUNT = 7;
  private static final int POLICE_ATTENDANCES_COURT = 8;
  private static final String POLICE_STATION_COURT_PRISON_ID = "policestationcourtprisonid";
  private static final String SCHEME_ID = "schemeid";
  private static final String DSCC_NUMBER = "dsccnumber";
  private static final String MAAT_ID = "maatid";
  private static final String PRISON_LAW_PRIOR_APPROVAL_NUMBER = "prisonlawpriorapprovalnumber";
  private static final boolean IS_DUTY_SOLICITOR = true;
  private static final boolean IS_YOUTH_COURT = true;
  private static final String UNIQUE_CASE_ID = "uniquecaseid";
  private static final int MEDIATION_SESSIONS_COUNT = 9;
  private static final int MEDIATION_TIME_MINUTES = 10;
  private static final String OUTREACH_LOCATION = "outreachlocation";
  private static final String REFERRAL_SOURCE = "08";
  private static final String REFERRAL_SOURCE_LABEL = "08 - Client self-referred";
  public static final String YES = "Yes";

  AmendCaseDetailsViewTest() {
    this.mapping = amendCaseDetailsUrl;
  }

  @Test
  void testShowsCivilCaseDetails() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    claim.setScheduleReference(SCHEDULE_REFERENCE);
    claim.setCaseId(CASE_ID);
    claim.setCaseReferenceNumber(CASE_REFERENCE_NUMBER);
    claim.setCaseStartDate(CASE_START_DATE);
    claim.setCaseConcludedDate(CASE_CONCLUDED_DATE);
    claim.setUniqueFileNumber(UFN);
    claim.setCaseStage(CASE_STAGE);
    claim.setValueOfCosts(VALUE_OF_COSTS);
    claim.setProcurementArea(PROCUREMENT_AREA);
    claim.setAccessPoint(ACCESS_POINT);
    claim.setStageReached(STAGE_REACHED);
    claim.setOutcome(OUTCOME_FOR_CLIENT);
    claim.setExceptionalCaseFundingReference(EXCEPTIONAL_CASE_FUNDING);
    claim.setCivilLegalAdviceReference(CLA_REFERENCE);
    claim.setCivilLegalAdviceExemption(CLA_EXEMPTION);
    claim.setDeliveryLocation(DELIVERY_LOCATION);
    claim.setCourtLocation(COURT_LOCATION);
    claim.setAitHearingCentre(AIT_HEARING_CENTRE);
    claim.setLocalAuthorityNumber(LOCAL_AUTHORITY_NUMBER);
    claim.setDesignatedAccreditedRepresentative(DESIGNATED_ACCREDITED_REPRESENTATIVE);
    claim.setAdviceTime(ADVICE_TIME);
    claim.setTravelTime(TRAVEL_TIME);
    claim.setWaitingTime(WAITING_TIME);
    claim.setIsAdditionalTravelPayment(ADDITIONAL_TRAVEL_PAYMENT);
    claim.setFollowOnWork(FOLLOW_ON_WORK);
    claim.setIsToleranceApplicable(TOLERANCE_INDICATOR);
    claim.setIsLegacyCase(LEGACY_CASE);
    claim.setMeetingsAttended(MEETINGS_ATTENDED);
    claim.setAdviceType(ADVICE_TYPE);
    claim.setTransferDate(TRANSFER_DATE);
    claim.setMedicalReportsClaimed(MEDICAL_REPORTS_CLAIMED);
    claim.setExemptionCriteriaSatisfied(EXEMPTION_CRITERIA_SATISFIED);
    claim.setIsIrcSurgery(IRC_SURGERY);
    claim.setSurgeryDate(SURGERY_DATE);
    claim.setSurgeryClientsCount(SURGERY_CLIENTS_COUNT);
    claim.setSurgeryMattersCount(SURGERY_MATTERS_COUNT);
    claim.setMentalHealthTribunalReference(MENTAL_HEALTH_TRIBUNAL_REFERENCE);
    claim.setIsNrmAdvice(IS_NRM_ADVICE);

    var forms = createCaseForms(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(caseDetails.getFirst(), "Item", "Current", "Amended");
    assertSummaryListRowContainsValues(
        caseDetails.get(1), "Schedule reference", SCHEDULE_REFERENCE, SCHEDULE_REFERENCE);
    assertSummaryListRowContainsValues(caseDetails.get(2), "Case ID", CASE_ID, CASE_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(3),
        "Case reference number (CRN)",
        CASE_REFERENCE_NUMBER,
        CASE_REFERENCE_NUMBER);

    assertDateRow(caseDetails.get(4), "Case start date", CASE_START_DATE, "CASE_START_DATE");
    assertDateRow(
        caseDetails.get(5),
        "Case concluded date or case claimed date",
        CASE_CONCLUDED_DATE,
        "CASE_CONCLUDED_CLAIMED_DATE");
    assertSummaryListRowContainsValues(caseDetails.get(6), "Unique file number (UFN)", UFN, UFN);
    assertEnumTypeaheadRow(
        caseDetails.get(7), "Case stage or level", CASE_STAGE, "CASE_STAGE", CASE_STAGE);
    assertSummaryListRowContainsValues(
        caseDetails.get(8),
        "Value of costs or damages recovered",
        "£" + VALUE_OF_COSTS.toString(),
        "TODO");
    assertSummaryListRowContainsValues(
        caseDetails.get(9), "Procurement area", PROCUREMENT_AREA, PROCUREMENT_AREA);
    assertSummaryListRowContainsValues(
        caseDetails.get(10), "Access point", ACCESS_POINT, ACCESS_POINT);
    assertEnumTypeaheadRow(
        caseDetails.get(11), "Stage reached", STAGE_REACHED, "STAGE_REACHED", STAGE_REACHED);
    assertEnumTypeaheadRow(
        caseDetails.get(12),
        "Outcome for client",
        OUTCOME_FOR_CLIENT,
        "OUTCOME_FOR_CLIENT",
        OUTCOME_FOR_CLIENT);
    assertSummaryListRowContainsValues(
        caseDetails.get(13),
        "Exceptional case funding (ECF) reference",
        EXCEPTIONAL_CASE_FUNDING,
        EXCEPTIONAL_CASE_FUNDING);
    assertSummaryListRowContainsValues(
        caseDetails.get(14),
        "Civil Legal Advice (CLA) reference number",
        CLA_REFERENCE,
        CLA_REFERENCE);
    assertSummaryListRowContainsValues(
        caseDetails.get(15),
        "Civil Legal Advice (CLA) exemption code",
        CLA_EXEMPTION,
        CLA_EXEMPTION);
    assertSummaryListRowContainsValues(
        caseDetails.get(16), "Delivery location", DELIVERY_LOCATION, DELIVERY_LOCATION);
    assertSummaryListRowContainsValues(
        caseDetails.get(17),
        "Court location (Housing Possession Court Duty Scheme (HPCDS))",
        COURT_LOCATION,
        COURT_LOCATION);
    assertEnumTypeaheadRow(
        caseDetails.get(18),
        "Asylum and Immigration Tribunal (AIT) hearing centre",
        AIT_HEARING_CENTRE_LABEL,
        "AIT_HEARING_CENTRE",
        AIT_HEARING_CENTRE);
    assertSummaryListRowContainsValues(
        caseDetails.get(19),
        "Local authority number",
        LOCAL_AUTHORITY_NUMBER,
        LOCAL_AUTHORITY_NUMBER);
    assertEnumTypeaheadRow(
        caseDetails.get(20),
        "Designated accredited representative",
        DESIGNATED_ACCREDITED_REPRESENTATIVE,
        "DESIGNATED_ACCREDITED_REPRESENTATIVE",
        DESIGNATED_ACCREDITED_REPRESENTATIVE);
    assertSummaryListRowContainsValues(
        caseDetails.get(21), "Advice time", String.valueOf(ADVICE_TIME), "TODO");
    assertSummaryListRowContainsValues(
        caseDetails.get(22), "Travel time", String.valueOf(TRAVEL_TIME), "TODO");
    assertSummaryListRowContainsValues(
        caseDetails.get(23), "Waiting time", String.valueOf(WAITING_TIME), "TODO");
    assertBooleanSelectRow(
        caseDetails.get(24), "Additional travel payment", YES, "ADDITIONAL_TRAVEL_PAYMENT", true);
    assertSummaryListRowContainsValues(
        caseDetails.get(25), "Follow on work", FOLLOW_ON_WORK, FOLLOW_ON_WORK);
    assertBooleanSelectRow(
        caseDetails.get(26), "Tolerance indicator", YES, "TOLERANCE_INDICATOR", true);
    assertBooleanSelectRow(caseDetails.get(27), "Legacy case", YES, "LEGACY_CASE", true);
    assertEnumTypeaheadRow(
        caseDetails.get(28),
        "Meetings attended",
        MEETINGS_ATTENDED,
        "MEETINGS_ATTENDED",
        MEETINGS_ATTENDED);
    assertEnumTypeaheadRow(
        caseDetails.get(29), "Type of advice", ADVICE_TYPE, "ADVICE_TYPE", ADVICE_TYPE);
    assertDateRow(caseDetails.get(30), "Transfer date", TRANSFER_DATE, "TRANSFER_DATE");
    assertSummaryListRowContainsValues(
        caseDetails.get(31),
        "Medical reports claimed",
        String.valueOf(MEDICAL_REPORTS_CLAIMED),
        "TODO");
    assertEnumTypeaheadRow(
        caseDetails.get(32),
        "Exemption criteria satisfied",
        EXEMPTION_CRITERIA_SATISFIED,
        "EXEMPTION_CRITERIA_SATISFIED",
        EXEMPTION_CRITERIA_SATISFIED);
    assertBooleanSelectRow(
        caseDetails.get(33), "Immigration removal centre (IRC) surgery", YES, "IRC_SURGERY", true);
    assertDateRow(caseDetails.get(34), "Surgery date", SURGERY_DATE, "SURGERY_DATE");
    assertSummaryListRowContainsValues(
        caseDetails.get(35),
        "Number of clients seen at surgery",
        String.valueOf(SURGERY_CLIENTS_COUNT),
        "TODO");
    assertSummaryListRowContainsValues(
        caseDetails.get(36),
        "Number of clients resulting in legal help matter opened",
        String.valueOf(SURGERY_MATTERS_COUNT),
        "TODO");
    assertSummaryListRowContainsValues(
        caseDetails.get(37),
        "Mental health tribunal reference",
        MENTAL_HEALTH_TRIBUNAL_REFERENCE,
        MENTAL_HEALTH_TRIBUNAL_REFERENCE);
    assertBooleanSelectRow(
        caseDetails.get(38),
        "National referral mechanism (NRM) advice",
        YES,
        "IS_NRM_ADVICE",
        true);
  }

  @Test
  void testShowsCrimeCaseDetails() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE);
    claim.setStageReached(STAGE_REACHED);
    claim.setUniqueFileNumber(UFN);
    claim.setRepresentationOrderDate(REPRESENTATION_ORDER_DATE);
    claim.setCaseEndDate(CASE_CONCLUDED_DATE);
    claim.setStandardFeeCategory(STANDARD_FEE_CATEGORY);
    claim.setOutcome(OUTCOME_FOR_CLIENT);
    claim.setSuspectsDefendantsCount(SUSPECTS_DEFENDANTS_COUNT);
    claim.setPoliceStationCourtAttendancesCount(POLICE_ATTENDANCES_COURT);
    claim.setPoliceStationCourtPrisonId(POLICE_STATION_COURT_PRISON_ID);
    claim.setSchemeId(SCHEME_ID);
    claim.setDsccNumber(DSCC_NUMBER);
    claim.setMaatId(MAAT_ID);
    claim.setPrisonLawPriorApprovalNumber(PRISON_LAW_PRIOR_APPROVAL_NUMBER);
    claim.setIsDutySolicitor(IS_DUTY_SOLICITOR);
    claim.setIsYouthCourt(IS_YOUTH_COURT);

    var forms = createCaseForms(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var caseDetails = getSummaryListInCard(doc, "Case details");

    assertSummaryListRowContainsValues(caseDetails.getFirst(), "Item", "Current", "Amended");
    assertEnumTypeaheadRow(
        caseDetails.get(1), "Stage reached", STAGE_REACHED, "STAGE_REACHED", STAGE_REACHED);
    assertSummaryListRowContainsValues(caseDetails.get(2), "Unique file number (UFN)", UFN, UFN);
    assertDateRow(
        caseDetails.get(3),
        "Representation order date",
        REPRESENTATION_ORDER_DATE,
        "REPRESENTATION_ORDER_DATE");
    assertDateRow(
        caseDetails.get(4), "Case concluded date", CASE_CONCLUDED_DATE, "CASE_CONCLUDED_DATE");
    assertEnumTypeaheadRow(
        caseDetails.get(5),
        "Standard fee category",
        STANDARD_FEE_CATEGORY,
        "STANDARD_FEE_CATEGORY",
        STANDARD_FEE_CATEGORY);
    assertEnumTypeaheadRow(
        caseDetails.get(6),
        "Outcome for client",
        OUTCOME_FOR_CLIENT,
        "OUTCOME_FOR_CLIENT",
        OUTCOME_FOR_CLIENT);
    assertSummaryListRowContainsValues(
        caseDetails.get(7),
        "Number of suspects or defendants",
        String.valueOf(SUSPECTS_DEFENDANTS_COUNT),
        "TODO");
    assertSummaryListRowContainsValues(
        caseDetails.get(8),
        "Number of police station or court attendances",
        String.valueOf(POLICE_ATTENDANCES_COURT),
        "TODO");
    assertSummaryListRowContainsValues(
        caseDetails.get(9),
        "Police station/Court ID/Prison ID",
        POLICE_STATION_COURT_PRISON_ID,
        POLICE_STATION_COURT_PRISON_ID);
    assertSummaryListRowContainsValues(caseDetails.get(10), "Scheme ID", SCHEME_ID, SCHEME_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(11),
        "Defence Solicitor Call Centre (DSCC) number",
        DSCC_NUMBER,
        DSCC_NUMBER);
    assertSummaryListRowContainsValues(caseDetails.get(12), "MAAT ID", MAAT_ID, MAAT_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(13),
        "Prison Law Prior Approval number",
        PRISON_LAW_PRIOR_APPROVAL_NUMBER,
        PRISON_LAW_PRIOR_APPROVAL_NUMBER);
    assertBooleanSelectRow(caseDetails.get(14), "Duty solicitor", YES, "IS_DUTY_SOLICITOR", true);
    assertBooleanSelectRow(caseDetails.get(15), "Youth court", YES, "IS_YOUTH_COURT", true);
  }

  @Test
  void testShowsMediationCaseDetails() {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setCaseId(CASE_ID);
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType(MATTER_TYPE_CODE_1);
    claim.setCaseReferenceNumber(CASE_REFERENCE_NUMBER);
    claim.setCaseStartDate(CASE_START_DATE);
    claim.setUniqueCaseId(UNIQUE_CASE_ID);
    claim.setCaseEndDate(CASE_CONCLUDED_DATE);
    claim.setMediationSessionsCount(MEDIATION_SESSIONS_COUNT);
    claim.setMediationTimeMinutes(MEDIATION_TIME_MINUTES);
    claim.setOutcome(OUTCOME_FOR_CLIENT);
    claim.setOutreachLocation(OUTREACH_LOCATION);
    claim.setReferralSource(REFERRAL_SOURCE);
    claim.setScheduleReference(SCHEDULE_REFERENCE);

    var forms = createCaseForms(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(caseDetails.getFirst(), "Item", "Current", "Amended");
    assertSummaryListRowContainsValues(
        caseDetails.get(1),
        "Case reference number (CRN)",
        CASE_REFERENCE_NUMBER,
        CASE_REFERENCE_NUMBER);
    assertDateRow(caseDetails.get(2), "Case start date", CASE_START_DATE, "CASE_START_DATE");
    assertSummaryListRowContainsValues(caseDetails.get(3), "Claim ID", CASE_ID, CASE_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(4), "Unique case ID", UNIQUE_CASE_ID, UNIQUE_CASE_ID);
    assertDateRow(
        caseDetails.get(5), "Case concluded date", CASE_CONCLUDED_DATE, "CASE_CONCLUDED_DATE");
    assertSummaryListRowContainsValues(
        caseDetails.get(6),
        "Number of mediation sessions",
        String.valueOf(MEDIATION_SESSIONS_COUNT),
        "TODO");
    assertSummaryListRowContainsValues(
        caseDetails.get(7),
        "Mediation time (minutes)",
        String.valueOf(MEDIATION_TIME_MINUTES),
        "TODO");
    assertEnumTypeaheadRow(
        caseDetails.get(8), "Outcome", OUTCOME_FOR_CLIENT, "OUTCOME", OUTCOME_FOR_CLIENT);
    assertSummaryListRowContainsValues(
        caseDetails.get(9), "Outreach location", OUTREACH_LOCATION, OUTREACH_LOCATION);
    assertEnumTypeaheadRow(
        caseDetails.get(10), "Referral", REFERRAL_SOURCE_LABEL, "REFERRAL_SOURCE", REFERRAL_SOURCE);
    assertSummaryListRowContainsValues(
        caseDetails.get(11),
        "Schedule reference (outcome)",
        SCHEDULE_REFERENCE,
        SCHEDULE_REFERENCE);
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Amend claim details");
    assertPageHasHeading(doc, "Amend claim details");
    assertPageHasBackLink(doc);

    assertH2Exists(doc, "Case");

    assertPageHasPrimaryButton(doc, "Continue");
    assertPageHasLink(doc, "cancel", "Cancel", overviewUrl);
  }

  private static @NonNull AmendmentForms createCaseForms(ClaimDetails claimDetails) {
    var view = ClaimCaseViewFactory.create(claimDetails);
    return new AmendmentForms(
        new AmendmentForm(new LinkedHashMap<>()),
        new AmendmentForm(new LinkedHashMap<>()),
        new AmendmentForm(view.caseDetailsRows()));
  }

  private void assertDateRow(
      List<Element> row, String label, LocalDate expectedDate, String inputPrefix) {
    assertCellContainsText(row.getFirst(), label);
    assertCellContainsText(row.get(1), expectedDate.format(testFormatter));

    Element amended = row.get(2);
    Element dateInput = selectFirst(amended, ".govuk-date-input");

    Element day = selectFirst(dateInput, "input.govuk-input--width-2");
    Assertions.assertEquals(
        String.valueOf(expectedDate.getDayOfMonth()), day.attr("value"), "Day input value");
    Assertions.assertEquals(inputPrefix + "-day", day.attr("id"), "Day input id");

    Element year = selectFirst(dateInput, "input.govuk-input--width-4");
    Assertions.assertEquals(
        String.valueOf(expectedDate.getYear()), year.attr("value"), "Year input value");
    Assertions.assertEquals(inputPrefix + "-year", year.attr("id"), "Year input id");

    Element monthSelect = selectFirst(dateInput, "select");
    Assertions.assertEquals(inputPrefix + "-month", monthSelect.attr("id"), "Month select id");
    Assertions.assertEquals(
        12, monthSelect.select("option[value~=^[0-9]+$]").size(), "Twelve month options");
    Element selectedMonth = selectFirst(monthSelect, "option[selected]");
    Assertions.assertEquals(
        expectedDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
        selectedMonth.text(),
        "Selected month name");
    Assertions.assertEquals(
        String.valueOf(expectedDate.getMonthValue()),
        selectedMonth.attr("value"),
        "Selected month value");
  }
}
