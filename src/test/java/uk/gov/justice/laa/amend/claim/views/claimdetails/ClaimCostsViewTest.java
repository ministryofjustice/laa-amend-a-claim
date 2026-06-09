package uk.gov.justice.laa.amend.claim.views.claimdetails;

import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.models.OutcomeType.PAID_IN_FULL;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.claimdetails.ClaimCostsController;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;

@WebMvcTest(ClaimCostsController.class)
class ClaimCostsViewTest extends ClaimDetailsBaseTest {

  private static final String NOT_APPLICABLE = "Not applicable";

  private static final String SUBMITTED = "£100.00";
  private static final String CALCULATED = "£200.00";
  private static final String ASSESSED = "£300.00";

  @MockitoBean private AssessmentService assessmentService;
  @MockitoBean private UserRetrievalService userRetrievalService;

  @BeforeEach
  public void setup() {
    super.setup();
    when(featureFlagsConfig.getIsFullClaimDetailsEnabled()).thenReturn(true);
    mapping = costsUrl;
  }

  @Test
  void testShowsCrimeCosts() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setHasAssessment(true);
    claim.setLastAssessment(AssessmentInfo.builder().lastAssessmentOutcome(PAID_IN_FULL).build());
    when(assessmentService.getLatestAssessmentByClaim(claim)).thenReturn(claim);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Requested", "Calculated");
    assertSummaryListRowContainsValues(
        clientDetails.get(1), "Fixed fee", NOT_APPLICABLE, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(2), "Net profit costs", SUBMITTED, NOT_APPLICABLE, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(3), "Net disbursements", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(4), "Net travel costs", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(5), "Net waiting costs", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(clientDetails.get(6), "VAT indicator", "Yes", "No", "Yes");
    assertSummaryListRowContainsValues(
        clientDetails.get(7), "Disbursements VAT", SUBMITTED, CALCULATED, ASSESSED);
  }

  @Test
  void testShowsMediationCosts() {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setHasAssessment(true);
    claim.setLastAssessment(AssessmentInfo.builder().lastAssessmentOutcome(PAID_IN_FULL).build());
    when(assessmentService.getLatestAssessmentByClaim(claim)).thenReturn(claim);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Requested", "Calculated");
    assertSummaryListRowContainsValues(
        clientDetails.get(1), "Fixed fee", NOT_APPLICABLE, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(clientDetails.get(2), "VAT indicator", "Yes", "No", "Yes");
    assertSummaryListRowContainsValues(
        clientDetails.get(3), "Net disbursements", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(4), "Disbursements VAT", SUBMITTED, CALCULATED, ASSESSED);
  }

  @Test
  void testShowsCivilCosts() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setHasAssessment(true);
    claim.setLastAssessment(AssessmentInfo.builder().lastAssessmentOutcome(PAID_IN_FULL).build());
    when(assessmentService.getLatestAssessmentByClaim(claim)).thenReturn(claim);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Requested", "Calculated");
    assertSummaryListRowContainsValues(
        clientDetails.get(1), "Fixed fee", NOT_APPLICABLE, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(2), "Net profit costs", SUBMITTED, NOT_APPLICABLE, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(3), "Net disbursements", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(4), "Net cost of counsel", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(5), "Disbursements VAT", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(6), "Travel and waiting costs", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(clientDetails.get(7), "VAT indicator", "Yes", "No", "Yes");
    assertSummaryListRowContainsValues(
        clientDetails.get(8), "Adjourned hearing fee", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(9),
        "Detention, travel and waiting (DTW) costs",
        SUBMITTED,
        CALCULATED,
        ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(10), "Judicial review or form filling", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(11), "Substantive hearing", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(12), "Home Office Interview", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(13),
        "Case management review hearing (CMRH)-oral",
        SUBMITTED,
        CALCULATED,
        ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(14),
        "Case management review hearing (CMRH)-telephone",
        SUBMITTED,
        CALCULATED,
        ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(15), "London rate", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(16),
        "National Immigration Asylum Team Disbursement prior authority number",
        SUBMITTED,
        CALCULATED,
        ASSESSED);
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Claim details");
    assertPageHasHeading(doc, "Claim details");
    assertPageDoesNotHaveBackLink(doc);

    assertPageHasNoActiveServiceNavigationItems(doc);
    assertPageHasInactiveSubNavigationItem(doc, "Overview", overviewUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Client", clientUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Case", caseUrl);
    assertPageHasActiveSubNavigationItem(doc, "Costs", costsUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Claim history", historyUrl);

    assertH2Exists(doc, "Costs");
  }
}
