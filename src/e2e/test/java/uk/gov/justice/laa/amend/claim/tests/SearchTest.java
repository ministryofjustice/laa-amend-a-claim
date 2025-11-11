
package uk.gov.justice.laa.amend.claim.tests;

import base.BaseTest;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.utils.EnvConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SearchTest extends BaseTest {

    @Test
    @DisplayName("Search: submit form and see results table")
    void canSearchForClaim() {
        String baseUrl = EnvConfig.baseUrl();
        SearchPage searchPage = new SearchPage(page).navigateTo(baseUrl);

        Assertions.assertEquals("Search for a claim", searchPage.getHeadingText());

        searchPage.searchForClaim("123456", "10", "2025", "123", "CRN002");

        page.waitForSelector("h2.govuk-heading-m:has-text('search results')");
        boolean hasResults = page.locator("table.govuk-table").isVisible();
        Assertions.assertTrue(hasResults, "Expected results table to be visible after search.");
    }
}
