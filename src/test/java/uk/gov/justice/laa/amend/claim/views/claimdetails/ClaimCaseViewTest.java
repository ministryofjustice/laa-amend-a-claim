package uk.gov.justice.laa.amend.claim.views.claimdetails;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.claimdetails.ClaimCaseController;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;

@WebMvcTest(ClaimCaseController.class)
class ClaimCaseViewTest extends ClaimDetailsBaseTest {

  private static final String FEE_CODE = "feeCode";
  private static final String MATTER_TYPE = "matterType";

  private static final String STAGE_REACHED = "stageReached";
  private static final String UFN = "ufn";
  private static final String OUTCOME = "outcome";
  private static final String STANDARD_FEE_CATEGORY = "standardFeeCategory";
  private static final LocalDate CASE_END_DATE = LocalDate.of(2026, 5, 28);
  private static final String CASE_END_DATE_RENDERED = "28 May 2026";

  // Crime fields
  private static final LocalDate REPRESENTATION_ORDER_DATE = LocalDate.of(2026, 5, 27);
  private static final String REPRESENTATION_ORDER_DATE_RENDERED = "27 May 2026";
  private static final Integer SUSPECTS_DEFENDANTS_COUNT = 1;
  private static final Integer POLICE_STATION_COURT_ATTENDANCES_COUNT = 2;
  private static final String POLICE_STATION_COURT_PRISON_ID = "policeStationCourtPrisonId";
  private static final String SCHEME_ID = "schemeId";
  private static final String DSCC_NUMBER = "dsccNumber";
  private static final String MAAT_ID = "maatId";
  private static final String PRISON_LAW_PRIOR_APPROVAL_NUMBER = "prisonLawPriorApprovalNumber";

  // Mediation fields
  private static final String CASE_REFERENCE_NUMBER = "caseReferenceNumber";
  private static final LocalDate CASE_START_DATE = LocalDate.of(2026, 5, 27);
  private static final String CASE_START_DATE_RENDERED = "27 May 2026";
  private static final String CASE_ID = "caseId";
  private static final String UNIQUE_CASE_ID = "uniqueCaseId";
  private static final String OUTREACH_LOCATION = "outreachLocation";
  private static final String REFERRAL_SOURCE = "referralSource";
  private static final String SCHEDULE_REFERENCE = "scheduleReference";

  // Civil fields
  private static final LocalDate CASE_CONCLUDED_DATE = LocalDate.of(2026, 7, 27);
  private static final String CASE_CONCLUDED_DATE_RENDERED = "27 July 2026";
  private static final String UNIQUE_FILE_NUMBER = "1234";
  private static final String CASE_STAGE = "caseStage";
  private static final BigDecimal VALUE_OF_COSTS = BigDecimal.valueOf(120);
  private static final String VALUE_OF_COSTS_RENDERED = "£120.00";
  private static final String PROCUREMENT_AREA = "procurementArea";
  private static final String ACCESS_POINT = "accessPoint";
  private static final String EXCEPTIONAL_CASE_FUNDING_REFERENCE = "EX_REF";
  private static final String CLA_REFERENCE = "CLA_REF";
  private static final String CLA_EXEMPTION = "CLA_EX";
  private static final String DELIVERY_LOCATION = "deliveryLocation";
  private static final String COURT_LOCATION = "courtLocation";
  private static final String AIT_HEARING_CENTRE = "aitHearingCentre";
  private static final String LOCAL_AUTHORITY_NUMBER = "localAuthorityNum";
  private static final String DESIGNATED_ACCREDITED_REPRESENTATIVE =
      "designatedAccreditedRepresentative";
  private static final Integer ADVICE_TIME = 60;
  private static final Integer TRAVEL_TIME = 90;
  private static final Integer WAITING_TIME = 15;
  private static final Boolean IS_ADDITIONAL_TRAVEL_PAYMENT = true;
  private static final String FOLLOW_ON_WORK = "No";
  private static final Boolean IS_TOLERANCE_APPLICABLE = false;
  private static final Boolean IS_LEGACY_CASE = false;
  private static final String MEETINGS_ATTENDED = "meetingsAttended";
  private static final String ADVICE_TYPE = "adviceType";
  private static final LocalDate TRANSFER_DATE = LocalDate.of(2026, 5, 26);
  private static final String TRANSFER_DATE_RENDERED = "26 May 2026";
  private static final Integer MEDICAL_REPORTS_CLAIMED = 0;
  private static final String EXEMPTION_CRITERIA_SATISFIED = "exceptionCriteriaSatisfied";
  private static final Boolean IS_IRC_SURGERY = true;
  private static final LocalDate SURGERY_DATE = LocalDate.of(2026, 5, 30);
  private static final String SURGERY_DATE_RENDERED = "30 May 2026";
  private static final Integer SURGERY_CLIENTS_COUNT = 1;
  private static final Integer SURGERY_MATTERS_COUNT = 1;
  private static final String MENTAL_HEALTH_TRIBUNAL_REFERENCE = "MENTAL_HEALTH_REF";
  private static final Boolean IS_NRM_ADVICE = false;
  private static final String MATTER_TYPE1 = "matterType1";
  private static final String MATTER_TYPE2 = "matterType2";

  @MockitoBean private AssessmentService assessmentService;
  @MockitoBean private UserRetrievalService userRetrievalService;

  @BeforeEach
  public void setup() {
    super.setup();
    when(featureFlagsConfig.getIsFullClaimDetailsEnabled()).thenReturn(true);
    mapping = caseUrl;
  }

  @Test
  void testShowsCrimeClientDetails() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE);
    claim.setStageReached(STAGE_REACHED);
    claim.setUniqueFileNumber(UFN);
    claim.setRepresentationOrderDate(REPRESENTATION_ORDER_DATE);
    claim.setCaseEndDate(CASE_END_DATE);
    claim.setStandardFeeCategory(STANDARD_FEE_CATEGORY);
    claim.setOutcome(OUTCOME);
    claim.setSuspectsDefendantsCount(SUSPECTS_DEFENDANTS_COUNT);
    claim.setPoliceStationCourtAttendancesCount(POLICE_STATION_COURT_ATTENDANCES_COUNT);
    claim.setPoliceStationCourtPrisonId(POLICE_STATION_COURT_PRISON_ID);
    claim.setSchemeId(SCHEME_ID);
    claim.setDsccNumber(DSCC_NUMBER);
    claim.setMaatId(MAAT_ID);
    claim.setPrisonLawPriorApprovalNumber(PRISON_LAW_PRIOR_APPROVAL_NUMBER);
    claim.setIsDutySolicitor(true);
    claim.setIsYouthCourt(false);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var caseType = getSummaryListInCard(doc, "Case type");
    assertSummaryListRowContainsValues(caseType.getFirst(), "Fee code", FEE_CODE);
    assertSummaryListRowContainsValues(caseType.get(1), "Matter type", MATTER_TYPE);

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(caseDetails.getFirst(), "Stage reached", STAGE_REACHED);
    assertSummaryListRowContainsValues(caseDetails.get(1), "Unique file number (UFN)", UFN);
    assertSummaryListRowContainsValues(
        caseDetails.get(2), "Representation order date", REPRESENTATION_ORDER_DATE_RENDERED);
    assertSummaryListRowContainsValues(
        caseDetails.get(3), "Case concluded date", CASE_END_DATE_RENDERED);
    assertSummaryListRowContainsValues(
        caseDetails.get(4), "Standard fee category", STANDARD_FEE_CATEGORY);
    assertSummaryListRowContainsValues(caseDetails.get(5), "Outcome for client", OUTCOME);
    assertSummaryListRowContainsValues(
        caseDetails.get(6),
        "Number of suspects or defendants",
        SUSPECTS_DEFENDANTS_COUNT.toString());
    assertSummaryListRowContainsValues(
        caseDetails.get(7),
        "Number of police station or court attendances",
        POLICE_STATION_COURT_ATTENDANCES_COUNT.toString());
    assertSummaryListRowContainsValues(
        caseDetails.get(8), "Police station/Court ID/Prison ID", POLICE_STATION_COURT_PRISON_ID);
    assertSummaryListRowContainsValues(caseDetails.get(9), "Scheme ID", SCHEME_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(10), "Defence Solicitor Call Centre (DSCC) number", DSCC_NUMBER);
    assertSummaryListRowContainsValues(caseDetails.get(11), "MAAT ID", MAAT_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(12), "Prison Law Prior Approval number", PRISON_LAW_PRIOR_APPROVAL_NUMBER);
    assertSummaryListRowContainsValues(caseDetails.get(13), "Duty solicitor", "Yes");
    assertSummaryListRowContainsValues(caseDetails.get(14), "Youth court", "No");
  }

  @Test
  void testShowsMediationClientDetails() {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE1);
    claim.setMatterType2(MATTER_TYPE2);

    claim.setCaseReferenceNumber(CASE_REFERENCE_NUMBER);
    claim.setCaseStartDate(CASE_START_DATE);
    claim.setCaseId(CASE_ID);
    claim.setUniqueCaseId(UNIQUE_CASE_ID);
    claim.setCaseEndDate(CASE_END_DATE);
    claim.setMediationSessionsCount(1);
    claim.setMediationTimeMinutes(2);
    claim.setOutcome(OUTCOME);
    claim.setOutreachLocation(OUTREACH_LOCATION);
    claim.setReferralSource(REFERRAL_SOURCE);
    claim.setScheduleReference(SCHEDULE_REFERENCE);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var caseType = getSummaryListInCard(doc, "Case type");
    assertSummaryListRowContainsValues(caseType.getFirst(), "Fee code", FEE_CODE);
    assertSummaryListRowContainsValues(caseType.get(1), "Matter type 1", MATTER_TYPE1);
    assertSummaryListRowContainsValues(caseType.get(2), "Matter type 2", MATTER_TYPE2);

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(
        caseDetails.getFirst(), "Case reference number (CRN)", CASE_REFERENCE_NUMBER);
    assertSummaryListRowContainsValues(
        caseDetails.get(1), "Case start date", CASE_START_DATE_RENDERED);
    assertSummaryListRowContainsValues(caseDetails.get(2), "Claim ID", CASE_ID);
    assertSummaryListRowContainsValues(caseDetails.get(3), "Unique case ID", UNIQUE_CASE_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(4), "Case concluded date", CASE_END_DATE_RENDERED);
    assertSummaryListRowContainsValues(caseDetails.get(5), "Number of mediation sessions", "1");
    assertSummaryListRowContainsValues(caseDetails.get(6), "Mediation time (minutes)", "2");
    assertSummaryListRowContainsValues(caseDetails.get(7), "Outcome", OUTCOME);
    assertSummaryListRowContainsValues(caseDetails.get(8), "Outreach location", OUTREACH_LOCATION);
    assertSummaryListRowContainsValues(caseDetails.get(9), "Referral", REFERRAL_SOURCE);
    assertSummaryListRowContainsValues(
        caseDetails.get(10), "Schedule reference (outcome)", SCHEDULE_REFERENCE);
  }

  @Test
  void testShowsCivilClientDetails() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE1);
    claim.setMatterType2(MATTER_TYPE2);

    claim.setScheduleReference(SCHEDULE_REFERENCE);
    claim.setCaseId(CASE_ID);
    claim.setCaseReferenceNumber(CASE_REFERENCE_NUMBER);
    claim.setCaseStartDate(CASE_START_DATE);
    claim.setCaseConcludedDate(CASE_CONCLUDED_DATE);
    claim.setUniqueFileNumber(UNIQUE_FILE_NUMBER);
    claim.setCaseStage(CASE_STAGE);
    claim.setValueOfCosts(VALUE_OF_COSTS);
    claim.setProcurementArea(PROCUREMENT_AREA);
    claim.setAccessPoint(ACCESS_POINT);
    claim.setStageReached(STAGE_REACHED);
    claim.setOutcome(OUTCOME);
    claim.setExceptionalCaseFundingReference(EXCEPTIONAL_CASE_FUNDING_REFERENCE);
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
    claim.setIsAdditionalTravelPayment(IS_ADDITIONAL_TRAVEL_PAYMENT);
    claim.setFollowOnWork(FOLLOW_ON_WORK);
    claim.setIsToleranceApplicable(IS_TOLERANCE_APPLICABLE);
    claim.setIsLegacyCase(IS_LEGACY_CASE);
    claim.setMeetingsAttended(MEETINGS_ATTENDED);
    claim.setAdviceType(ADVICE_TYPE);
    claim.setTransferDate(TRANSFER_DATE);
    claim.setMedicalReportsClaimed(MEDICAL_REPORTS_CLAIMED);
    claim.setExemptionCriteriaSatisfied(EXEMPTION_CRITERIA_SATISFIED);
    claim.setIsIrcSurgery(IS_IRC_SURGERY);
    claim.setSurgeryDate(SURGERY_DATE);
    claim.setSurgeryClientsCount(SURGERY_CLIENTS_COUNT);
    claim.setSurgeryMattersCount(SURGERY_MATTERS_COUNT);
    claim.setMentalHealthTribunalReference(MENTAL_HEALTH_TRIBUNAL_REFERENCE);
    claim.setIsNrmAdvice(IS_NRM_ADVICE);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var caseType = getSummaryListInCard(doc, "Case type");
    assertSummaryListRowContainsValues(caseType.getFirst(), "Fee code", FEE_CODE);
    assertSummaryListRowContainsValues(caseType.get(1), "Matter type 1", MATTER_TYPE1);
    assertSummaryListRowContainsValues(caseType.get(2), "Matter type 2", MATTER_TYPE2);

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(
        caseDetails.getFirst(), "Schedule reference", SCHEDULE_REFERENCE);
    assertSummaryListRowContainsValues(caseDetails.get(1), "Case ID", CASE_ID);
    assertSummaryListRowContainsValues(
        caseDetails.get(2), "Case reference number (CRN)", CASE_REFERENCE_NUMBER);
    assertSummaryListRowContainsValues(
        caseDetails.get(3), "Case start date", CASE_START_DATE_RENDERED);
    assertSummaryListRowContainsValues(
        caseDetails.get(4),
        "Case concluded date or case claimed date",
        CASE_CONCLUDED_DATE_RENDERED);
    assertSummaryListRowContainsValues(
        caseDetails.get(5), "Unique file number (UFN)", UNIQUE_FILE_NUMBER);
    assertSummaryListRowContainsValues(caseDetails.get(6), "Case stage or level", CASE_STAGE);
    assertSummaryListRowContainsValues(
        caseDetails.get(7), "Value of costs or damages recovered", VALUE_OF_COSTS_RENDERED);
    assertSummaryListRowContainsValues(caseDetails.get(8), "Procurement area", PROCUREMENT_AREA);
    assertSummaryListRowContainsValues(caseDetails.get(9), "Access point", ACCESS_POINT);
    assertSummaryListRowContainsValues(caseDetails.get(10), "Stage reached", STAGE_REACHED);
    assertSummaryListRowContainsValues(caseDetails.get(11), "Outcome for client", OUTCOME);
    assertSummaryListRowContainsValues(
        caseDetails.get(12),
        "Exceptional case funding (ECF) reference",
        EXCEPTIONAL_CASE_FUNDING_REFERENCE);
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
    assertSummaryListRowContainsValues(caseDetails.get(20), "Advice time", ADVICE_TIME.toString());
    assertSummaryListRowContainsValues(caseDetails.get(21), "Travel time", TRAVEL_TIME.toString());
    assertSummaryListRowContainsValues(
        caseDetails.get(22), "Waiting time", WAITING_TIME.toString());
    assertSummaryListRowContainsValues(caseDetails.get(23), "Additional travel payment", "Yes");
    assertSummaryListRowContainsValues(caseDetails.get(24), "Follow on work", FOLLOW_ON_WORK);
    assertSummaryListRowContainsValues(caseDetails.get(25), "Tolerance indicator", "No");
    assertSummaryListRowContainsValues(caseDetails.get(26), "Legacy case", "No");
    assertSummaryListRowContainsValues(caseDetails.get(27), "Meetings attended", MEETINGS_ATTENDED);
    assertSummaryListRowContainsValues(caseDetails.get(28), "Type of advice", ADVICE_TYPE);
    assertSummaryListRowContainsValues(
        caseDetails.get(29), "Transfer date", TRANSFER_DATE_RENDERED);
    assertSummaryListRowContainsValues(
        caseDetails.get(30), "Medical reports claimed", MEDICAL_REPORTS_CLAIMED.toString());
    assertSummaryListRowContainsValues(
        caseDetails.get(31), "Exemption criteria satisfied", EXEMPTION_CRITERIA_SATISFIED);
    assertSummaryListRowContainsValues(
        caseDetails.get(32), "Immigration removal centre (IRC) surgery", "Yes");
    assertSummaryListRowContainsValues(caseDetails.get(33), "Surgery date", SURGERY_DATE_RENDERED);
    assertSummaryListRowContainsValues(
        caseDetails.get(34), "Number of clients seen at surgery", SURGERY_CLIENTS_COUNT.toString());
    assertSummaryListRowContainsValues(
        caseDetails.get(35),
        "Number of clients resulting in legal help matter opened",
        SURGERY_MATTERS_COUNT.toString());
    assertSummaryListRowContainsValues(
        caseDetails.get(36), "Mental health tribunal reference", MENTAL_HEALTH_TRIBUNAL_REFERENCE);
    assertSummaryListRowContainsValues(
        caseDetails.get(37), "National referral mechanism (NRM) advice", "No");
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Claim details");
    assertPageHasHeading(doc, "Claim details");
    assertPageDoesNotHaveBackLink(doc);

    assertPageHasNoActiveServiceNavigationItems(doc);
    assertPageHasInactiveSubNavigationItem(doc, "Overview", overviewUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Client", clientUrl);
    assertPageHasActiveSubNavigationItem(doc, "Case", caseUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Costs", costsUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Claim history", historyUrl);

    assertH2Exists(doc, "Case");
  }
}
