package uk.gov.justice.laa.payments.amend.views.amendments;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.payments.amend.controllers.amendments.AmendmentsConfirmationController;
import uk.gov.justice.laa.payments.amend.models.AmendmentConfirmation;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.service.ClaimHistoryService;
import uk.gov.justice.laa.payments.amend.service.ClaimService;

@WebMvcTest(AmendmentsConfirmationController.class)
class AmendmentsConfirmationViewTest extends AmendmentsBaseTest {
  private static final String NOT_APPLICABLE = "Not applicable";
  private static final String SUBMITTED = "£100.00";
  private static final String CALCULATED = "£200.00";

  @MockitoBean private ClaimService claimService;

  @MockitoBean private ClaimHistoryService claimHistoryService;

  AmendmentsConfirmationViewTest() {
    this.mapping = confirmationUrl;
  }

  @BeforeEach
  void setupConfirmation() {
    claim.setAmended(true);
    when(claimService.getClaimDetails(any(), any())).thenReturn(claim);
    when(claimHistoryService.getAmendmentConfirmation(claim))
        .thenReturn(new AmendmentConfirmation(false, java.util.Set.of()));
  }

  @Test
  void testPage() {
    session.setAttribute("searchUrl", "/?officeCode=0P322F&page=1");
    Document doc = renderDocument();

    assertPageHasTitle(doc, "Amendments complete");
    assertPageHasHeading(doc, "Amendments complete");
    assertPageHasPanel(doc);
    assertPageHasContent(doc, "The claim details have been updated with your amendments.");
    assertPageHasLink(doc, "view-amended-claim", "View amended claim", overviewUrl);
    assertPageHasLink(
        doc, "back-to-search", "Back to search results", "/?officeCode=0P322F&page=1");
  }

  @Test
  void showsUpdatedClaimTotalMessageWhenCalculatedCostsChanged() {
    when(claimHistoryService.getAmendmentConfirmation(claim))
        .thenReturn(new AmendmentConfirmation(true, new HashSet<>()));

    Document doc = renderDocument();

    assertPageHasContent(doc, "The updated claim total is £200.00");
  }

  @Test
  void rendersAllFspChangedCostFieldsForCrime() {
    setClaimForConfirmation(MockClaimsFunctions.createMockCrimeClaim());
    var changedFields =
        new HashSet<>(
            Set.of(
                "fee.fixedFeeAmount",
                "fee.netProfitCostsAmount",
                "fee.disbursementAmount",
                "fee.netTravelCostsAmount",
                "fee.netWaitingCostsAmount",
                "fee.vatIndicator",
                "fee.disbursementVatAmount"));
    when(claimHistoryService.getAmendmentConfirmation(claim))
        .thenReturn(new AmendmentConfirmation(true, changedFields));

    var doc = renderDocument();
    assertPageHasContent(doc, "The updated claim total is £200.00");

    var costs = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(costs.getFirst(), "Item", "Reported", "Calculated");
    assertSummaryListRowContainsValues(costs.get(1), "Fixed fee", NOT_APPLICABLE, CALCULATED);
    assertSummaryListRowContainsValues(costs.get(2), "Net profit costs", SUBMITTED, NOT_APPLICABLE);
    assertSummaryListRowContainsValues(costs.get(3), "Net disbursements", SUBMITTED, CALCULATED);
    assertSummaryListRowContainsValues(costs.get(4), "Net travel costs", SUBMITTED, CALCULATED);
    assertSummaryListRowContainsValues(costs.get(5), "Net waiting costs", SUBMITTED, CALCULATED);
    assertSummaryListRowContainsValues(costs.get(6), "VAT indicator", "Yes", "No");
    assertSummaryListRowContainsValues(costs.get(7), "Disbursements VAT", SUBMITTED, CALCULATED);
    assertRowsHaveAmendedTags(
        doc,
        "List of costs",
        "Fixed fee",
        "Net profit costs",
        "Net disbursements",
        "Net travel costs",
        "Net waiting costs",
        "VAT indicator",
        "Disbursements VAT");
  }

  @Test
  void rendersAllFspChangedCostFieldsForMediation() {
    setClaimForConfirmation(MockClaimsFunctions.createMockMediationClaim());
    var changedFields =
        new HashSet<>(
            Set.of(
                "fee.fixedFeeAmount",
                "fee.vatIndicator",
                "fee.disbursementAmount",
                "fee.disbursementVatAmount"));
    when(claimHistoryService.getAmendmentConfirmation(claim))
        .thenReturn(new AmendmentConfirmation(true, changedFields));

    var doc = renderDocument();
    assertPageHasContent(doc, "The updated claim total is £200.00");

    var costs = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(costs.getFirst(), "Item", "Reported", "Calculated");
    assertSummaryListRowContainsValues(costs.get(1), "Fixed fee", NOT_APPLICABLE, CALCULATED);
    assertSummaryListRowContainsValues(costs.get(2), "VAT indicator", "Yes", "No");
    assertSummaryListRowContainsValues(costs.get(3), "Net disbursements", SUBMITTED, CALCULATED);
    assertSummaryListRowContainsValues(costs.get(4), "Disbursements VAT", SUBMITTED, CALCULATED);
    assertRowsHaveAmendedTags(
        doc,
        "List of costs",
        "Fixed fee",
        "VAT indicator",
        "Net disbursements",
        "Disbursements VAT");
  }

  @Test
  void rendersAllFspChangedCostFieldsForCivil() {
    setClaimForConfirmation(MockClaimsFunctions.createMockCivilClaim());
    var changedFields =
        new HashSet<>(
            Set.of(
                "fee.fixedFeeAmount",
                "fee.netProfitCostsAmount",
                "fee.disbursementAmount",
                "fee.netCostOfCounselAmount",
                "fee.disbursementVatAmount",
                "fee.travelAndWaitingCostsAmount",
                "fee.vatIndicator",
                "fee.boltOnAdjournedHearingFee",
                "fee.detentionTravelAndWaitingCostsAmount",
                "fee.jrFormFillingAmount",
                "fee.boltOnSubstantiveHearingFee",
                "fee.boltOnHomeOfficeInterviewFee",
                "fee.boltOnCmrhOralFee",
                "fee.boltOnCmrhTelephoneFee"));
    when(claimHistoryService.getAmendmentConfirmation(claim))
        .thenReturn(new AmendmentConfirmation(true, changedFields));

    var doc = renderDocument();
    assertPageHasContent(doc, "The updated claim total is £200.00");

    var costs = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(costs.getFirst(), "Item", "Reported", "Calculated");
    assertSummaryListRowContainsValues(costs.get(1), "Fixed fee", NOT_APPLICABLE, CALCULATED);
    assertSummaryListRowContainsValues(costs.get(2), "Net profit costs", SUBMITTED, NOT_APPLICABLE);
    assertSummaryListRowContainsValues(costs.get(3), "Net disbursements", SUBMITTED, CALCULATED);
    assertSummaryListRowContainsValues(costs.get(4), "Net cost of counsel", SUBMITTED, CALCULATED);
    assertSummaryListRowContainsValues(costs.get(5), "Disbursements VAT", SUBMITTED, CALCULATED);
    assertSummaryListRowContainsValues(
        costs.get(6), "Travel and waiting costs", SUBMITTED, CALCULATED);
    assertSummaryListRowContainsValues(costs.get(7), "VAT indicator", "Yes", "No");
    assertSummaryListRowContainsValues(costs.get(8), "Adjourned hearing fee", "100", CALCULATED);
    assertSummaryListRowContainsValues(
        costs.get(9), "Detention, travel and waiting (DTW) costs", SUBMITTED, CALCULATED);
    assertSummaryListRowContainsValues(
        costs.get(10), "Judicial review or form filling", SUBMITTED, CALCULATED);
    assertSummaryListRowContainsValues(costs.get(11), "Substantive hearing", "Yes", CALCULATED);
    assertSummaryListRowContainsValues(costs.get(12), "Home Office Interview", "100", CALCULATED);
    assertSummaryListRowContainsValues(
        costs.get(13), "Case management review hearing (CMRH)-oral", "100", CALCULATED);
    assertSummaryListRowContainsValues(
        costs.get(14), "Case management review hearing (CMRH)-telephone", "100", CALCULATED);
    assertSummaryListRowContainsValues(costs.get(15), "London rate", "Yes", NOT_APPLICABLE);
    assertSummaryListRowContainsValues(
        costs.get(16),
        "National Immigration Asylum Team Disbursement prior authority number",
        "PRIOR_AUTHORITY_REF",
        NOT_APPLICABLE);
    assertRowsHaveAmendedTags(
        doc,
        "List of costs",
        "Fixed fee",
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
        "Case management review hearing (CMRH)-telephone");
  }

  private void setClaimForConfirmation(ClaimDetails claimDetails) {
    claimDetails.setSubmissionId(submissionId);
    claimDetails.setClaimId(claimId);
    claimDetails.setAmended(true);
    claim = claimDetails;
    when(claimService.getClaimDetails(any(), any())).thenReturn(claimDetails);
  }

  private void assertRowsHaveAmendedTags(Document doc, String cardTitle, String... rowLabels) {
    for (String rowLabel : rowLabels) {
      assertSummaryListRowHasAmendedTag(getSummaryListRowInCard(doc, cardTitle, rowLabel));
    }
  }
}
