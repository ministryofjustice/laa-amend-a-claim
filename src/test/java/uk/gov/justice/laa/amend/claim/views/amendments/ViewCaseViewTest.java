package uk.gov.justice.laa.amend.claim.views.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.amendments.CaseController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@WebMvcTest(CaseController.class)
class ViewCaseViewTest extends AmendmentsBaseTest {

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
  private static final String CASE_STAGE = "casestage";
  private static final BigDecimal VALUE_OF_COSTS = BigDecimal.valueOf(10.12);
  private static final String PROCUREMENT_AREA = "procurementarea";
  private static final String ACCESS_POINT = "accesspoint";
  private static final String STAGE_REACHED = "stagereached";
  private static final String OUTCOME_FOR_CLIENT = "outcomeforclient";
  private static final String EXCEPTIONAL_CASE_FUNDING = "exceptionalcasefunding";
  private static final String CLA_REFERENCE = "clareference";
  private static final String CLA_EXEMPTION = "claexemption";
  private static final String DELIVERY_LOCATION = "deliverylocation";
  private static final String COURT_LOCATION = "courtlocation";
  private static final String AIT_HEARING_CENTRE = "aithearingcentre";
  private static final String LOCAL_AUTHORITY_NUMBER = "localauthoritynumber";
  private static final String DESIGNATED_ACCREDITED_REPRESENTATIVE =
      "designatedaccreditedrepresentative";
  private static final int ADVICE_TIME = 1;
  private static final int TRAVEL_TIME = 2;
  private static final int WAITING_TIME = 3;
  private static final boolean ADDITIONAL_TRAVEL_PAYMENT = true;
  private static final String FOLLOW_ON_WORK = "followonwork";
  private static final boolean TOLERANCE_INDICATOR = true;
  private static final boolean LEGACY_CASE = true;
  private static final String MEETINGS_ATTENDED = "meetingsattended";
  private static final String ADVICE_TYPE = "advicetype";
  private static final LocalDate TRANSFER_DATE = LocalDate.of(2020, 3, 1);
  private static final int MEDICAL_REPORTS_CLAIMED = 4;
  private static final String EXEMPTION_CRITERIA_SATISFIED = "exemptioncriteriasatisfied";
  private static final boolean IRC_SURGERY = true;
  private static final LocalDate SURGERY_DATE = LocalDate.of(2020, 4, 1);
  private static final int SURGERY_CLIENTS_COUNT = 5;
  private static final int SURGERY_MATTERS_COUNT = 6;
  private static final String MENTAL_HEALTH_TRIBUNAL_REFERENCE = "mentalhealthtribunalreference";
  private static final boolean IS_NRM_ADVICE = true;
  private static final LocalDate REPRESENTATION_ORDER_DATE = LocalDate.of(2020, 5, 1);
  private static final String STANDARD_FEE_CATEGORY = "standardfeecategory";
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
  private static final String REFERRAL_SOURCE = "referralsource";
  public static final String YES = "Yes";

  ViewCaseViewTest() {
    this.mapping = caseUrl;
  }

  @Test
  void testShowsUnamendedCivilCaseDetails() {
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

    // TODO: Add forms once form pages have been implemented
    var forms = new AmendmentForms(new AmendmentForm(new LinkedHashMap<>()), new AmendmentForm());
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var caseType = getSummaryListInCard(doc, "Case type");
    assertSummaryListRowContainsValues(caseType.getFirst(), "Fee code", FEE_CODE);
    assertSummaryListRowContainsValues(caseType.get(1), "Matter type 1", MATTER_TYPE_CODE_1);
    assertSummaryListRowContainsValues(caseType.get(2), "Matter type 2", MATTER_TYPE_CODE_2);

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(
        caseDetails.getFirst(), "Schedule reference", SCHEDULE_REFERENCE);
    assertSummaryListRowContainsValues(caseDetails.get(1), "Case ID", CASE_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(2), "Case reference number (CRN)", CASE_REFERENCE_NUMBER);
    assertSummaryListRowContainsValues(
        caseDetails.get(3), "Case start date", CASE_START_DATE.format(testFormatter));
    assertSummaryListRowContainsValues(
        caseDetails.get(4),
        "Case concluded date or case claimed date",
        CASE_CONCLUDED_DATE.format(testFormatter));
    assertSummaryListRowContainsValues(caseDetails.get(5), "Unique file number (UFN)", UFN);
    assertSummaryListRowContainsValues(caseDetails.get(6), "Case stage or level", CASE_STAGE);
    assertSummaryListRowContainsValues(
        caseDetails.get(7), "Value of costs or damages recovered", "£" + VALUE_OF_COSTS.toString());
    assertSummaryListRowContainsValues(caseDetails.get(8), "Procurement area", PROCUREMENT_AREA);
    assertSummaryListRowContainsValues(caseDetails.get(9), "Access point", ACCESS_POINT);
    assertSummaryListRowContainsValues(caseDetails.get(10), "Stage reached", STAGE_REACHED);
    assertSummaryListRowContainsValues(
        caseDetails.get(11), "Outcome for client", OUTCOME_FOR_CLIENT);
    assertSummaryListRowContainsValues(
        caseDetails.get(12), "Exceptional case funding (ECF) reference", EXCEPTIONAL_CASE_FUNDING);
    assertSummaryListRowContainsValues(
        caseDetails.get(13), "Civil Legal Advice (CLA) reference number", CLA_REFERENCE);
    assertSummaryListRowContainsValues(
        caseDetails.get(14), "Civil Legal Advice (CLA) exemption code", CLA_EXEMPTION);
    assertSummaryListRowContainsValues(caseDetails.get(15), "Delivery location", DELIVERY_LOCATION);
    assertSummaryListRowContainsValues(
        caseDetails.get(16),
        "Court location (Housing Possession Court Duty Scheme (HPCDS))",
        COURT_LOCATION);
    assertSummaryListRowContainsValues(
        caseDetails.get(17),
        "Asylum and Immigration Tribunal (AIT) hearing centre",
        AIT_HEARING_CENTRE);
    assertSummaryListRowContainsValues(
        caseDetails.get(18), "Local authority number", LOCAL_AUTHORITY_NUMBER);
    assertSummaryListRowContainsValues(
        caseDetails.get(19),
        "Designated accredited representative",
        DESIGNATED_ACCREDITED_REPRESENTATIVE);
    assertSummaryListRowContainsValues(
        caseDetails.get(20), "Advice time", String.valueOf(ADVICE_TIME));
    assertSummaryListRowContainsValues(
        caseDetails.get(21), "Travel time", String.valueOf(TRAVEL_TIME));
    assertSummaryListRowContainsValues(
        caseDetails.get(22), "Waiting time", String.valueOf(WAITING_TIME));
    assertSummaryListRowContainsValues(caseDetails.get(23), "Additional travel payment", YES);
    assertSummaryListRowContainsValues(caseDetails.get(24), "Follow on work", FOLLOW_ON_WORK);
    assertSummaryListRowContainsValues(caseDetails.get(25), "Tolerance indicator", YES);
    assertSummaryListRowContainsValues(caseDetails.get(26), "Legacy case", YES);
    assertSummaryListRowContainsValues(caseDetails.get(27), "Meetings attended", MEETINGS_ATTENDED);
    assertSummaryListRowContainsValues(caseDetails.get(28), "Type of advice", ADVICE_TYPE);
    assertSummaryListRowContainsValues(
        caseDetails.get(29), "Transfer date", TRANSFER_DATE.format(testFormatter));
    assertSummaryListRowContainsValues(
        caseDetails.get(30), "Medical reports claimed", String.valueOf(MEDICAL_REPORTS_CLAIMED));
    assertSummaryListRowContainsValues(
        caseDetails.get(31), "Exemption criteria satisfied", EXEMPTION_CRITERIA_SATISFIED);
    assertSummaryListRowContainsValues(
        caseDetails.get(32), "Immigration removal centre (IRC) surgery", YES);
    assertSummaryListRowContainsValues(
        caseDetails.get(33), "Surgery date", SURGERY_DATE.format(testFormatter));
    assertSummaryListRowContainsValues(
        caseDetails.get(34),
        "Number of clients seen at surgery",
        String.valueOf(SURGERY_CLIENTS_COUNT));
    assertSummaryListRowContainsValues(
        caseDetails.get(35),
        "Number of clients resulting in legal help matter opened",
        String.valueOf(SURGERY_MATTERS_COUNT));
    assertSummaryListRowContainsValues(
        caseDetails.get(36), "Mental health tribunal reference", MENTAL_HEALTH_TRIBUNAL_REFERENCE);
    assertSummaryListRowContainsValues(
        caseDetails.get(37), "National referral mechanism (NRM) advice", YES);

    assertPageHasLink(doc, "back-to-claim-details", "Back to claim details", overviewUrl);
  }

  @Test
  void testShowsUnamendedCrimeCaseDetails() {
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

    // TODO: Add forms once form pages have been implemented
    var forms = new AmendmentForms(new AmendmentForm(new LinkedHashMap<>()), new AmendmentForm());
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var caseType = getSummaryListInCard(doc, "Case type");
    assertSummaryListRowContainsValues(caseType.getFirst(), "Fee code", FEE_CODE);
    assertSummaryListRowContainsValues(caseType.get(1), "Matter type", MATTER_TYPE_CODE);

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(caseDetails.getFirst(), "Stage reached", STAGE_REACHED);
    assertSummaryListRowContainsValues(caseDetails.get(1), "Unique file number (UFN)", UFN);
    assertSummaryListRowContainsValues(
        caseDetails.get(2),
        "Representation order date",
        REPRESENTATION_ORDER_DATE.format(testFormatter));
    assertSummaryListRowContainsValues(
        caseDetails.get(3), "Case concluded date", CASE_CONCLUDED_DATE.format(testFormatter));
    assertSummaryListRowContainsValues(
        caseDetails.get(4), "Standard fee category", STANDARD_FEE_CATEGORY);
    assertSummaryListRowContainsValues(
        caseDetails.get(5), "Outcome for client", OUTCOME_FOR_CLIENT);
    assertSummaryListRowContainsValues(
        caseDetails.get(6),
        "Number of suspects or defendants",
        String.valueOf(SUSPECTS_DEFENDANTS_COUNT));
    assertSummaryListRowContainsValues(
        caseDetails.get(7),
        "Number of police station or court attendances",
        String.valueOf(POLICE_ATTENDANCES_COURT));
    assertSummaryListRowContainsValues(
        caseDetails.get(8), "Police station/Court ID/Prison ID", POLICE_STATION_COURT_PRISON_ID);
    assertSummaryListRowContainsValues(caseDetails.get(9), "Scheme ID", SCHEME_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(10), "Defence Solicitor Call Centre (DSCC) number", DSCC_NUMBER);
    assertSummaryListRowContainsValues(caseDetails.get(11), "MAAT ID", MAAT_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(12), "Prison Law Prior Approval number", PRISON_LAW_PRIOR_APPROVAL_NUMBER);
    assertSummaryListRowContainsValues(caseDetails.get(13), "Duty solicitor", YES);
    assertSummaryListRowContainsValues(caseDetails.get(14), "Youth court", YES);

    assertPageHasLink(doc, "back-to-claim-details", "Back to claim details", overviewUrl);
  }

  @Test
  void testShowsUnamendedMediationCaseDetails() {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setCaseId(CASE_ID);
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
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
    // TODO: Add forms once form pages have been implemented
    var forms = new AmendmentForms(new AmendmentForm(new LinkedHashMap<>()), new AmendmentForm());
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var caseType = getSummaryListInCard(doc, "Case type");
    assertSummaryListRowContainsValues(caseType.getFirst(), "Fee code", FEE_CODE);
    assertSummaryListRowContainsValues(caseType.get(1), "Matter type 1", MATTER_TYPE_CODE_1);
    assertSummaryListRowContainsValues(caseType.get(2), "Matter type 2", MATTER_TYPE_CODE_2);

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(caseDetails.getFirst(), "Case reference number (CRN)");
    assertSummaryListRowContainsValues(
        caseDetails.get(1), "Case start date", CASE_START_DATE.format(testFormatter));
    assertSummaryListRowContainsValues(caseDetails.get(2), "Claim ID", CASE_ID);
    assertSummaryListRowContainsValues(caseDetails.get(3), "Unique case ID", UNIQUE_CASE_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(4), "Case concluded date", CASE_CONCLUDED_DATE.format(testFormatter));
    assertSummaryListRowContainsValues(
        caseDetails.get(5),
        "Number of mediation sessions",
        String.valueOf(MEDIATION_SESSIONS_COUNT));
    assertSummaryListRowContainsValues(
        caseDetails.get(6), "Mediation time (minutes)", String.valueOf(MEDIATION_TIME_MINUTES));
    assertSummaryListRowContainsValues(caseDetails.get(7), "Outcome", OUTCOME_FOR_CLIENT);
    assertSummaryListRowContainsValues(caseDetails.get(8), "Outreach location", OUTREACH_LOCATION);
    assertSummaryListRowContainsValues(caseDetails.get(9), "Referral", REFERRAL_SOURCE);
    assertSummaryListRowContainsValues(
        caseDetails.get(10), "Schedule reference (outcome)", SCHEDULE_REFERENCE);
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Amend claim details");
    assertPageHasHeading(doc, "Amend claim details");
    assertPageHasBackLink(doc);

    assertPageHasNoActiveServiceNavigationItems(doc);
    assertPageHasInactiveSubNavigationItem(doc, "Client", clientUrl);
    assertPageHasActiveSubNavigationItem(doc, "Case", caseUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Costs", costsUrl);

    assertH2Exists(doc, "Case");

    assertPageHasLink(doc, "amend-case-type-link", "Change", amendCaseTypeUrl);
    assertPageHasLink(doc, "amend-case-details-link", "Change", amendCaseDetailsUrl);
  }
}
