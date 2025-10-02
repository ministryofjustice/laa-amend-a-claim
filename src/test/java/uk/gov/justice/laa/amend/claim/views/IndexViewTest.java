package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@SpringBootTest
class IndexViewTest extends ViewTestBase {

  IndexViewTest() {
    super("index");
  }

  @Test
  void testPage() {
    Document doc = renderDocument();

    assertPageHasTitle(doc, "Search for a claim");

    assertPageHasHeading(doc, "Search for a claim");

    assertPageHasHint(doc, "search-hint", "Enter a provider account and at least one field to search.");

    assertPageHasTextInput(doc, "provider-account-number", "Provider account number");

    assertPageHasDateInput(doc, "Submission date");

    assertPageHasHint(doc, "submission-date-hint", "For example, 3 2007");

    assertPageHasTextInput(doc, "submission-date-month", "Month");

    assertPageHasTextInput(doc, "submission-date-year", "Year");

    assertPageHasTextInput(doc, "reference-number", "UFN or CRN");

    assertPageHasActiveServiceNavigationItem(doc, "Search");
  }
}
