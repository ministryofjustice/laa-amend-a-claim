package uk.gov.justice.laa.amend.claim.views.amendments;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.amendments.AmendmentsConfirmationController;

@WebMvcTest(AmendmentsConfirmationController.class)
class AmendmentsConfirmationViewTest extends AmendmentsBaseTest {

  AmendmentsConfirmationViewTest() {
    this.mapping = confirmationUrl;
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
}
