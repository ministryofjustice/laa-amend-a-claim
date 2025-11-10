
package com.example.framework.pages;

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

    public SearchPage(Page page) {
        this.page = page;
        this.heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Search for a claim"));
        this.providerAccountNumberInput = page.locator("#provider-account-number");
        this.submissionMonthInput = page.locator("#submission-date-month");
        this.submissionYearInput = page.locator("#submission-date-year");
        this.ufnInput = page.locator("#unique-file-number");
        this.crnInput = page.locator("#case-reference-number");
        this.searchButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search"));
        this.clearAllLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Clear all"));
    }

    public void waitForPage() { heading.waitFor(); }

    public SearchPage navigateTo(String baseUrl) {
        page.navigate(baseUrl);
        waitForPage();
        return this;
    }

    public void enterProviderAccountNumber(String number) { providerAccountNumberInput.fill(number); }
    public void enterSubmissionDate(String month, String year) {
        if (month != null && !month.isEmpty()) submissionMonthInput.fill(month);
        if (year != null && !year.isEmpty()) submissionYearInput.fill(year);
    }
    public void enterUFN(String ufn) { if (ufn != null && !ufn.isEmpty()) ufnInput.fill(ufn); }
    public void enterCRN(String crn) { if (crn != null && !crn.isEmpty()) crnInput.fill(crn); }
    public void clickSearch() { searchButton.click(); }
    public void clickClearAll() { clearAllLink.click(); }

    public void searchForClaim(String providerAccount, String month, String year, String ufn, String crn) {
        waitForPage();
        enterProviderAccountNumber(providerAccount);
        enterSubmissionDate(month, year);
        enterUFN(ufn);
        enterCRN(crn);
        clickSearch();
    }

    public String getHeadingText() { return heading.textContent().trim(); }
}
