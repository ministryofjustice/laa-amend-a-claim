package uk.gov.justice.laa.amend.claim.views;

import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.controllers.ChangeMonetaryValueController;

@WebMvcTest(ChangeMonetaryValueController.class)
class ChangeMonetaryValueViewTest extends ViewTestBase {

  ChangeMonetaryValueViewTest() {
    this.mapping = String.format("/submissions/%s/claims/%s/profit-costs", submissionId, claimId);
  }

  @Test
  void testPage() {
    Document doc = renderDocument();

    assertPageHasTitle(doc, "Assess profit costs");

    assertPageHasHeading(doc, "Assess profit costs");

    List<List<Element>> summaryList = getFirstSummaryList(doc);
    Assertions.assertEquals(2, summaryList.size());
    assertSummaryListRowContainsValues(summaryList.getFirst(), "Calculated", "Not applicable");
    assertSummaryListRowContainsValues(summaryList.get(1), "Requested", "£100.00");

    assertPageHasLabel(doc, "value", "Assessed");

    assertPageHasHint(
        doc,
        "value-hint",
        "Enter the assessed value for the providers' profit costs, excluding VAT.");
  }

  @Test
  void testPageWithErrors() {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("value", "-1");

    Document doc = renderDocumentWithErrors(params);

    assertPageHasErrorSummary(doc, "value");
  }
}
