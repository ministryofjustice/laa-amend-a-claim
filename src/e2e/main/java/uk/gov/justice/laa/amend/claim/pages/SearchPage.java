package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;

public class SearchPage {
    private final Page page;

    private final Locator heading;
    private final Locator providerAccountNumberInput;
    private final Locator submissionMonthInput;
    private final Locator submissionYearInput;
    private final Locator ufnInput;
    private final Locator crnInput;
    private final Locator searchButton;
    private final Locator clearAllLink;

    private final Locator resultsHeading;
    private final Locator resultsTable;
    private final Locator resultRows;

<<<<<<< HEAD
    private final Locator successBanner;
    private final Locator successBannerHeading;

=======
>>>>>>> ef7f4cd (E2E Journey)
    public SearchPage(Page page) {
        this.page = page;

        this.heading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Search for a claim"));

<<<<<<< HEAD

        this.heading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Search for a claim"));

=======
>>>>>>> ef7f4cd (E2E Journey)
        this.providerAccountNumberInput = page.locator("#provider-account-number");
        this.submissionMonthInput = page.locator("#submission-date-month");
        this.submissionYearInput = page.locator("#submission-date-year");
        this.ufnInput = page.locator("#unique-file-number");
        this.crnInput = page.locator("#case-reference-number");
<<<<<<< HEAD

        this.searchButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Search"));

=======
        this.searchButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Search"));
>>>>>>> ef7f4cd (E2E Journey)
        this.clearAllLink = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Clear all"));

        this.resultsHeading = page.locator("h2.govuk-heading-m:has-text('search results')");
        this.resultsTable = page.locator("table.govuk-table");
        this.resultRows = resultsTable.locator("tbody tr.govuk-table__row");
<<<<<<< HEAD

        this.successBanner = page.locator(".govuk-notification-banner--success");
        this.successBannerHeading = successBanner.locator("h3.govuk-notification-banner__heading");
=======
>>>>>>> ef7f4cd (E2E Journey)
    }

    public void waitForPage() {
        heading.waitFor();
    }

    public SearchPage navigateTo(String baseUrl) {
        page.navigate(baseUrl);
        waitForPage();
        return this;
    }

    public void enterProviderAccountNumber(String number) {
        providerAccountNumberInput.fill(number);
    }

<<<<<<< HEAD
    public void enterProviderAccountNumber(String number) {
        providerAccountNumberInput.fill(number);
    }

=======
>>>>>>> ef7f4cd (E2E Journey)
    public void enterSubmissionDate(String month, String year) {
        if (month != null && !month.isEmpty()) submissionMonthInput.fill(month);
        if (year != null && !year.isEmpty()) submissionYearInput.fill(year);
    }
<<<<<<< HEAD

    public void enterUFN(String ufn) {
        if (ufn != null && !ufn.isEmpty()) ufnInput.fill(ufn);
    }

    public void enterCRN(String crn) {
        if (crn != null && !crn.isEmpty()) crnInput.fill(crn);
    }


    public void enterUFN(String ufn) {
        if (ufn != null && !ufn.isEmpty()) ufnInput.fill(ufn);
    }

    public void enterCRN(String crn) {
        if (crn != null && !crn.isEmpty()) crnInput.fill(crn);
    }

    public void clickSearch() { searchButton.click(); }

    public void clickClearAll() {
        clearAllLink.click();
    }

=======

    public void enterUFN(String ufn) {
        if (ufn != null && !ufn.isEmpty()) ufnInput.fill(ufn);
    }

    public void enterCRN(String crn) {
        if (crn != null && !crn.isEmpty()) crnInput.fill(crn);
    }

    public void clickSearch() { searchButton.click(); }

    public void clickClearAll() { clearAllLink.click(); }

    // ---- COMBINED SEARCH + WAIT FOR RESULTS ----
>>>>>>> ef7f4cd (E2E Journey)
    public void searchForClaim(String providerAccount, String month, String year,
                               String ufn, String crn) {
        waitForPage();
        enterProviderAccountNumber(providerAccount);
        enterSubmissionDate(month, year);
        enterUFN(ufn);
        enterCRN(crn);
        clickSearch();
        waitForResults();
<<<<<<< HEAD
        waitForResults();
=======
>>>>>>> ef7f4cd (E2E Journey)
    }

    public String getHeadingText() {
        return heading.textContent().trim();
    }

    public void waitForResults() {
        resultsHeading.waitFor();
        resultsTable.waitFor();
    }

<<<<<<< HEAD
=======
    /** Returns true if ≥1 result row is present */
>>>>>>> ef7f4cd (E2E Journey)
    public boolean hasResults() {
        waitForResults();
        return resultRows.count() > 0;
    }

<<<<<<< HEAD
=======
    /** Clicks "View" on the first result row */
>>>>>>> ef7f4cd (E2E Journey)
    public void clickViewOnFirstResult() {
        waitForResults();
        Locator row = resultRows.first();
        row.locator("a.govuk-link:has-text('View')").click();
    }

<<<<<<< HEAD
    public void clickViewForUfn(String ufn) {
        waitForResults();
        Locator row = resultRows.filter(
                new Locator.FilterOptions().setHasText(ufn)
        ).first();
        row.locator("a.govuk-link:has-text('View')").click();
    }

    public void clickViewForCrn(String crn) {
        waitForResults();
        Locator row = resultRows.filter(
                new Locator.FilterOptions().setHasText(crn)
        ).first();
        row.locator("a.govuk-link:has-text('View')").click();
    }

    public boolean isSuccessBannerVisible() {
        return successBanner.isVisible();
    }

    public String getSuccessBannerHeading() {
        if (!successBanner.isVisible()) return "";
        return successBannerHeading.textContent().trim();
    }
=======
    /** Clicks "View" on the row matching a specific UFN */
    public void clickViewForUfn(String ufn) {
        waitForResults();
        Locator row = resultRows.filter(new Locator.FilterOptions().setHasText(ufn)).first();
        row.locator("a.govuk-link:has-text('View')").click();
    }

    /** Clicks "View" on the row matching a CRN */
    public void clickViewForCrn(String crn) {
        waitForResults();
        Locator row = resultRows.filter(new Locator.FilterOptions().setHasText(crn)).first();
        row.locator("a.govuk-link:has-text('View')").click();
    }
>>>>>>> ef7f4cd (E2E Journey)
}