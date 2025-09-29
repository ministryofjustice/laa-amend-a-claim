package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotAuthorisedViewTest extends ViewTestBase {

  NotAuthorisedViewTest() {
    super("not-authorised");
  }

  @Test
  void testHomePage() {
    Document doc = renderDocument();
    assertPageHasHeading(doc, "You are not authorised");
  }
}
