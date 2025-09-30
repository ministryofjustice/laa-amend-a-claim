package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IndexViewTest extends ViewTestBase {

  IndexViewTest() {
    super("index");
  }

  @Test
  void testHomePage() {
    Document doc = renderDocument();
    assertPageHasTitle(doc, "Amend a claim");
    assertPageHasHeading(doc, "Amend a claim");
  }
}
