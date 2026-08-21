package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.DiscardController;

@WebMvcTest(DiscardController.class)
class DiscardViewTest extends ViewTestBase {

  DiscardViewTest() {
    this.mapping = String.format("/submissions/%s/claims/%s/discard", submissionId, claimId);
  }

  @Test
  void testPage() {
    Document doc = renderDocument();

    assertPageHasTitle(doc, "Confirm you want to discard this assessment");

    assertPageHasHeading(doc, "Confirm you want to discard this assessment");

    assertPageHasBackLink(doc);

    assertPageHasContent(doc, "Any changes made will not be saved.");

    assertPageHasPrimaryButton(doc, "Discard assessment");

    assertPageHasLink(
        doc,
        "return-to-claim",
        "Return to claim",
        String.format("/submissions/%s/claims/%s/review", submissionId, claimId));
  }
}
