package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IndexViewTest extends ViewTestBase {

  private final String view = "index";

  @Test
  void testHomePage() {
    Document doc = Jsoup.parse(templateEngine.process(view, context));
    assertPageHasHeading(doc, "Amend a claim");
  }
}
