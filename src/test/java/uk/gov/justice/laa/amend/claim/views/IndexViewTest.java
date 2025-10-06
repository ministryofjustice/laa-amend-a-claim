package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.HomePageController;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;

import java.util.Map;

@ActiveProfiles("local")
@WebMvcTest(HomePageController.class)
@Import(LocalSecurityConfig.class)
class IndexViewTest extends ViewTestBase {

    IndexViewTest() {
        super("/");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Search for a claim");

        assertPageHasHeading(doc, "Search for a claim");

        assertPageHasHint(doc, "search-hint", "Enter a provider account number and at least one other field to search.");

        assertPageHasTextInput(doc, "provider-account-number", "Provider account number");

        assertPageHasDateInput(doc, "Submission date");

        assertPageHasHint(doc, "submission-date-hint", "For example, 3 2007");

        assertPageHasTextInput(doc, "submission-date-month", "Month");

        assertPageHasTextInput(doc, "submission-date-year", "Year");

        assertPageHasTextInput(doc, "reference-number", "UFN or CRN");

        assertPageHasActiveServiceNavigationItem(doc, "Search");
    }

    @Test
    void testPageWithPagination() throws Exception {
        Map<String, Object> variables = Map.of(
            "viewModel", new SearchResultViewModel()
        );
        Document doc = renderDocument(variables);

        assertPageHasH2(doc, "3 search results");

        assertPageHasTable(doc);

        assertPageHasPagination(doc);
    }
}
