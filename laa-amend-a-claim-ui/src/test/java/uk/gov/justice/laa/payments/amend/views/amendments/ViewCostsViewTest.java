package uk.gov.justice.laa.payments.amend.views.amendments;

import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.AMENDMENTS_KEY;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.payments.amend.controllers.amendments.CostsController;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcosts.ClaimCostsViewFactory;

@WebMvcTest(CostsController.class)
class ViewCostsViewTest extends AmendmentsBaseTest {

  ViewCostsViewTest() {
    this.mapping = costsUrl;
  }

  @Test
  void testShowsUnamendedCosts() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);

    var forms = createCostsForms(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var costs = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(costs.getFirst(), "Item", "Reported", "Calculated");
    assertSummaryListRowContainsValues(costs.get(1), "Fixed fee", "Not applicable", "£200.00");
    assertSummaryListRowContainsValues(
        costs.get(2), "Net profit costs", "£100.00", "Not applicable");
    assertPageHasLink(doc, "amend-costs-link", "Change", amendCostsUrl());
    assertPageHasLink(doc, "back-to-claim-details", "Back to claim details", overviewUrl);
  }

  @Test
  void testShowsAmendedCosts() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);

    var forms = createCostsForms(claim);
    forms.getCostsForm().getCurrent().getInputs().put("PROFIT_COST", "150.25");
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var costs = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(
        costs.getFirst(), "Item", "Reported", "Calculated", "Amended");
    assertSummaryListRowContainsValues(
        costs.get(2), "Net profit costs", "£100.00", "Not applicable", "£150.25");
    assertPageHasLink(doc, "check", "Continue", checkUrl);
    assertPageHasLink(doc, "cancel", "Cancel", overviewUrl);
  }

  @Test
  void testReformattedBigDecimalDoesNotShowAmendedColumn() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);

    var forms = createCostsForms(claim);
    forms.getCostsForm().getCurrent().getInputs().put("PROFIT_COST", "100");
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();

    var costs = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(costs.getFirst(), "Item", "Reported", "Calculated");
    Assertions.assertEquals(3, costs.getFirst().size(), "Amended column should not be shown");
  }

  @Test
  void hidesChangeLinkWhenClaimAssessed() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    markAssessed(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createCostsForms(claim));

    var doc = renderDocument();

    Assertions.assertTrue(
        doc.select("#amend-costs-link").isEmpty(), "Change link should be hidden when assessed");
  }

  @Test
  void showsAssessedColumnWhenClaimAssessed() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    markAssessed(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createCostsForms(claim));

    var doc = renderDocument();

    var costs = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(
        costs.getFirst(), "Item", "Reported", "Calculated", "Assessed");
    assertSummaryListRowContainsValues(
        costs.get(1), "Fixed fee", "Not applicable", "£200.00", "£300.00");
    assertSummaryListRowContainsValues(
        costs.get(2), "Net profit costs", "£100.00", "Not applicable", "£300.00");
  }

  @Test
  void doesNotShowAssessedColumnWhenClaimNotAssessed() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);

    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createCostsForms(claim));

    var doc = renderDocument();

    var costs = getSummaryListInCard(doc, "List of costs");
    Assertions.assertEquals(3, costs.getFirst().size(), "Assessed column should not be shown");
  }

  @Test
  void showsAssessedBanner() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    markAssessed(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createCostsForms(claim));

    assertShowsAssessedBanner(renderDocument());
  }

  private static AmendmentForms createCostsForms(ClaimDetails claimDetails) {
    var costsForm = new AmendmentForm(ClaimCostsViewFactory.create(claimDetails).costRows());
    return AmendmentForms.builder()
        .client1(new AmendmentForm())
        .caseType(new AmendmentForm())
        .caseDetails(new AmendmentForm())
        .costs(costsForm)
        .build();
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Amend claim details");
    assertPageHasHeading(doc, "Amend claim details");
    assertPageDoesNotHaveBackLink(doc);

    assertH2Exists(doc, "Costs");
    assertPageHasActiveSubNavigationItem(doc, "Costs", costsUrl);
  }

  private String amendCostsUrl() {
    return "/submissions/%s/claims/%s/amendments/amend-costs".formatted(submissionId, claimId);
  }
}
