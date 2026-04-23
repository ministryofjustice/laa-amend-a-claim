package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.controllers.ChangeAllowedTotalsController;

@WebMvcTest(ChangeAllowedTotalsController.class)
class ChangeAllowedTotalViewTest extends ViewTestBase {

  ChangeAllowedTotalViewTest() {
    this.mapping = String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId);
  }

  @Test
  void testPage() {
    Document doc = renderDocument();

    assertPageHasTitle(doc, "Assess total allowed value");

    assertPageHasHeading(doc, "Assess total allowed value");

    assertPageHasHint(
        doc, "allowed-total-vat-hint", "Enter the amount of allowed VAT for the claim");
    assertPageHasHint(
        doc, "allowed-total-incl-vat-hint", "Enter the total allowed value of the claim");

    assertPageHasLabel(doc, "allowed-total-vat", "Allowed total VAT");
    assertPageHasLabel(doc, "allowed-total-incl-vat", "Allowed total including VAT");
  }

  @Test
  void testPageWithErrors() {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("value", "-1");

    Document doc = renderDocumentWithErrors(params);

    assertPageHasErrorSummary(doc, "allowed-total-vat", "allowed-total-incl-vat");
  }
}
