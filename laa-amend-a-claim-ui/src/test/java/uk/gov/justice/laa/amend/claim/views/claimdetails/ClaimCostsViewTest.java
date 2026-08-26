package uk.gov.justice.laa.amend.claim.views.claimdetails;

import static uk.gov.justice.laa.amend.claim.models.enums.OutcomeType.PAID_IN_FULL;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.claimdetails.ClaimCostsController;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@WebMvcTest(ClaimCostsController.class)
class ClaimCostsViewTest extends ClaimDetailsBaseTest {

  private static final String NOT_APPLICABLE = "Not applicable";

  private static final String SUBMITTED = "£100.00";
  private static final String CALCULATED = "£200.00";
  private static final String ASSESSED = "£300.00";

  @BeforeEach
  public void setup() {
    super.setup();
    mapping = costsUrl;
  }

  @Test
  void testShowsCrimeCosts() {
    claim = createCrimeClaim();
    mockClaimHistorySummary();

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Reported", "Calculated");
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
    assertPageHasNoAmendedTags(doc);
  }

  @Test
  void testShowsAmendedTagsForCrimeCosts() {
    claim = createCrimeClaim();
    mockClaimHistorySummary(
        "claimSummaryFee.netProfitCostsAmount",
        "claimSummaryFee.netDisbursementAmount",
        "claimSummaryFee.travelWaitingCostsAmount",
        "claimSummaryFee.netWaitingCostsAmount",
        "claimSummaryFee.isVatApplicable",
        "claimSummaryFee.disbursementsVatAmount");

    var doc = renderDocument();
    assertRowsHaveAmendedTags(
        doc,
        "List of costs",
        "Net profit costs",
        "Net disbursements",
        "Net travel costs",
        "Net waiting costs",
        "VAT indicator",
        "Disbursements VAT");
  }

  @Test
  void testShowsMediationCosts() {
    claim = createMediationClaim();
    mockClaimHistorySummary();

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Reported", "Calculated");
    assertSummaryListRowContainsValues(
        clientDetails.get(1), "Fixed fee", NOT_APPLICABLE, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(clientDetails.get(2), "VAT indicator", "Yes", "No", "Yes");
    assertSummaryListRowContainsValues(
        clientDetails.get(3), "Net disbursements", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(4), "Disbursements VAT", SUBMITTED, CALCULATED, ASSESSED);
    assertPageHasNoAmendedTags(doc);
  }

  @Test
  void testShowsAmendedTagsForMediationCosts() {
    claim = createMediationClaim();
    mockClaimHistorySummary(
        "claimSummaryFee.netDisbursementAmount",
        "claimSummaryFee.disbursementsVatAmount",
        "claimSummaryFee.isVatApplicable");

    var doc = renderDocument();
    assertRowsHaveAmendedTags(
        doc, "List of costs", "VAT indicator", "Net disbursements", "Disbursements VAT");
  }

  @Test
  void testShowsCivilCosts() {
    claim = createCivilClaim();
    mockClaimHistorySummary();

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Reported", "Calculated");
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
        clientDetails.get(8), "Adjourned hearing fee", "100", CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(9),
        "Detention, travel and waiting (DTW) costs",
        SUBMITTED,
        CALCULATED,
        ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(10), "Judicial review or form filling", SUBMITTED, CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(11), "Substantive hearing", "Yes", CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(12), "Home Office Interview", "100", CALCULATED, ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(13),
        "Case management review hearing (CMRH)-oral",
        "100",
        CALCULATED,
        ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(14),
        "Case management review hearing (CMRH)-telephone",
        "100",
        CALCULATED,
        ASSESSED);
    assertSummaryListRowContainsValues(
        clientDetails.get(15), "London rate", "Yes", NOT_APPLICABLE, NOT_APPLICABLE);
    assertSummaryListRowContainsValues(
        clientDetails.get(16),
        "National Immigration Asylum Team Disbursement prior authority number",
        "PRIOR_AUTHORITY_REF",
        NOT_APPLICABLE,
        NOT_APPLICABLE);
    assertPageHasNoAmendedTags(doc);
  }

  @Test
  void testShowsAmendedTagsForCivilCosts() {
    claim = createCivilClaim();
    mockClaimHistorySummary(
        "claimSummaryFee.netProfitCostsAmount",
        "claimSummaryFee.netDisbursementAmount",
        "claimSummaryFee.netCounselCostsAmount",
        "claimSummaryFee.disbursementsVatAmount",
        "claimSummaryFee.travelWaitingCostsAmount",
        "claimSummaryFee.isVatApplicable",
        "claimSummaryFee.adjournedHearingFeeAmount",
        "claimSummaryFee.detentionTravelWaitingCostsAmount",
        "claimSummaryFee.jrFormFillingAmount",
        "claimSummaryFee.isSubstantiveHearing",
        "claimSummaryFee.hoInterview",
        "claimSummaryFee.cmrhOralCount",
        "claimSummaryFee.cmrhTelephoneCount",
        "claimSummaryFee.isLondonRate",
        "claimSummaryFee.priorAuthorityReference");

    var doc = renderDocument();
    assertRowsHaveAmendedTags(
        doc,
        "List of costs",
        "Net profit costs",
        "Net disbursements",
        "Net cost of counsel",
        "Disbursements VAT",
        "Travel and waiting costs",
        "VAT indicator",
        "Adjourned hearing fee",
        "Detention, travel and waiting (DTW) costs",
        "Judicial review or form filling",
        "Substantive hearing",
        "Home Office Interview",
        "Case management review hearing (CMRH)-oral",
        "Case management review hearing (CMRH)-telephone",
        "London rate",
        "National Immigration Asylum Team Disbursement prior authority number");
  }

  private CrimeClaimDetails createCrimeClaim() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setHasAssessment(true);
    claim.setLastAssessment(AssessmentInfo.builder().lastAssessmentOutcome(PAID_IN_FULL).build());
    return claim;
  }

  private MediationClaimDetails createMediationClaim() {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setHasAssessment(true);
    claim.setLastAssessment(AssessmentInfo.builder().lastAssessmentOutcome(PAID_IN_FULL).build());
    return claim;
  }

  private CivilClaimDetails createCivilClaim() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setHasAssessment(true);
    claim.setLastAssessment(AssessmentInfo.builder().lastAssessmentOutcome(PAID_IN_FULL).build());
    return claim;
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
