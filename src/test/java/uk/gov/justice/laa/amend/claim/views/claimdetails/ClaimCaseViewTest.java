package uk.gov.justice.laa.amend.claim.views.claimdetails;

import static org.mockito.Mockito.when;

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
    assertSummaryListRowContainsValues(caseDetails.get(5), "Outcome for the client", OUTCOME);
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
    claim.setMatterTypeCode(MATTER_TYPE);

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
    assertSummaryListRowContainsValues(caseType.get(1), "Matter type", MATTER_TYPE);

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
        var claim = MockClaimsFunctions.createMockMediationClaim();
        this.claim = claim;
        claim.setSubmissionId(submissionId);
        claim.setClaimId(claimId);
        claim.setFeeCode(FEE_CODE);
        claim.setMatterTypeCode(MATTER_TYPE);

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
        assertSummaryListRowContainsValues(caseType.get(1), "Matter type", MATTER_TYPE);

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
