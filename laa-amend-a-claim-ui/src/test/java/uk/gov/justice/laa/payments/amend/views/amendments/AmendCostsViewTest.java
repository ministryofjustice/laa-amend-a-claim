package uk.gov.justice.laa.payments.amend.views.amendments;

import static uk.gov.justice.laa.payments.amend.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.AMENDMENTS_KEY;

import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.payments.amend.controllers.amendments.AmendCostsController;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.payments.amend.models.BoltOnClaimField;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcosts.ClaimCostsViewFactory;

@WebMvcTest(AmendCostsController.class)
class AmendCostsViewTest extends AmendmentsBaseTest {

  AmendCostsViewTest() {
    this.mapping =
        "/submissions/%s/claims/%s/amendments/amend-costs".formatted(submissionId, claimId);
  }

  @Test
  void testShowsBigDecimalCostInputs() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);

    var forms = createCostsForms(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var costs = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(costs.getFirst(), "Item", "Reported", "Amended");
    assertSummaryListRowContainsValues(costs.get(1), "Fixed fee", "Not applicable");
    Assertions.assertTrue(costs.get(1).get(2).select("input, select").isEmpty());
    assertBigDecimalInputRow(costs.get(2), "Net profit costs", "£100.00", "PROFIT_COST", "100.00");
    assertBooleanSelectRow(costs.get(6), "VAT indicator", "Yes", "VAT", true);
  }

  @Test
  void testShowsAmendedHeadingForMediationCostInputs() {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);

    var forms = createCostsForms(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();

    var costs = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(costs.getFirst(), "Item", "Reported", "Amended");
    assertSummaryListRowContainsValues(costs.get(1), "Fixed fee", "Not applicable");
    Assertions.assertTrue(costs.get(1).get(2).select("input, select").isEmpty());
    assertBooleanSelectRow(costs.get(2), "VAT indicator", "Yes", "VAT", true);
    assertBigDecimalInputRow(
        costs.get(3), "Net disbursements", "£100.00", "DISBURSEMENTS", "100.00");
  }

  @Test
  void testNotProvidedBoltOnIsStillEditable() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setSubstantiveHearing(BoltOnClaimField.builder().key(SUBSTANTIVE_HEARING).build());
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);

    var forms = createCostsForms(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var costs = getSummaryListInCard(doc, "List of costs");
    assertSummaryListRowContainsValues(costs.getFirst(), "Item", "Reported", "Amended");
    assertBigDecimalInputRow(costs.get(2), "Net profit costs", "£100.00", "PROFIT_COST", "100.00");
    assertBooleanSelectRow(costs.get(7), "VAT indicator", "Yes", "VAT", true);
    assertSummaryListRowContainsValues(costs.get(11), "Substantive hearing", "Not applicable");
    Assertions.assertFalse(
        costs.get(11).get(2).select("select#SUBSTANTIVE_HEARING").isEmpty(),
        "Not-provided bolt-on should still be editable");
    assertBooleanSelectRow(costs.get(15), "London rate", "Yes", "IS_LONDON_RATE", true);
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
    assertPageHasBackLink(doc);

    assertH2Exists(doc, "Costs");
    assertPageHasPrimaryButton(doc, "Continue");
    assertPageHasLink(doc, "cancel", "Cancel", costsUrl);
  }

  private void assertBigDecimalInputRow(
      List<Element> row, String label, String currentValue, String inputId, String inputValue) {
    assertCellContainsText(row.getFirst(), label);
    assertCellContainsText(row.get(1), currentValue);

    Element inputWrapper = selectFirst(row.get(2), ".govuk-input__wrapper");
    Assertions.assertEquals("£", selectFirst(inputWrapper, ".govuk-input__prefix").text());

    Element input = selectFirst(inputWrapper, "input.govuk-input--width-10");
    Assertions.assertEquals(inputId, input.attr("id"), "BigDecimal input id");
    Assertions.assertEquals(inputValue, input.attr("value"), "BigDecimal input value");
  }
}
