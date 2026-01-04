package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ReviewAndAmendPage {
    private final Page page;

    private final Locator heading;

    private final Locator claimCostsCard;
    private final Locator totalClaimValueCard;
    private final Locator totalAllowedValueCard;

    private final Locator claimCostsTable;
    private final Locator totalClaimValueTable;
    private final Locator totalAllowedValueTable;

    private final Locator submitAdjustmentsButton;
    private final Locator discardChangesLink;

    private final Locator errorSummary;

    public ReviewAndAmendPage(Page page) {
        this.page = page;

        this.heading = page.getByRole(
                AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Review and amend")
        );

        this.claimCostsCard = cardByTitle("Claim costs");
        this.totalClaimValueCard = cardByTitle("Total claim value");
        this.totalAllowedValueCard = cardByTitle("Total allowed value");

        this.claimCostsTable = claimCostsCard.locator("table.govuk-table");
        this.totalClaimValueTable = totalClaimValueCard.locator("table.govuk-table");
        this.totalAllowedValueTable = totalAllowedValueCard.locator("table.govuk-table");

        this.submitAdjustmentsButton = page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Submit adjustments")
        );

        this.discardChangesLink = page.locator("#discard");

        this.errorSummary = page.locator(".govuk-error-summary");
    }

    public void waitForPage() {
        heading.waitFor();
        claimCostsCard.waitFor();
        totalClaimValueCard.waitFor();
        totalAllowedValueCard.waitFor();
    }

    public String getHeadingText() {
        return heading.textContent().trim();
    }

    private Locator cardByTitle(String title) {
        return page.locator(".govuk-summary-card")
                .filter(new Locator.FilterOptions().setHas(
                        page.locator("h2.govuk-summary-card__title")
                                .filter(new Locator.FilterOptions().setHasText(title))
                ))
                .first();
    }


    private Locator rowByItemName(String itemName) {
        return claimCostsTable.locator("tbody tr").filter(
                new Locator.FilterOptions().setHasText(itemName)
        ).first();
    }

    private void clickChangeInRow(String itemName) {
        rowByItemName(itemName)
                .locator("a.govuk-link:has-text('Change')")
                .click();
    }

    public void clickChangeProfitCosts()      { clickChangeInRow("Profit costs"); }
    public void clickChangeDisbursements()    { clickChangeInRow("Disbursements"); }
    public void clickChangeDisbursementsVat() { clickChangeInRow("Disbursement VAT"); }
    public void clickChangeTravelCosts()      { clickChangeInRow("Travel costs"); }
    public void clickChangeWaitingCosts()     { clickChangeInRow("Waiting costs"); }
    public void clickChangeVat()              { clickChangeInRow("VAT"); }

    public void clickChangeDetentionTravelAndWaitingCosts() {
        clickChangeInRow("Detention travel and waiting costs");
    }

    public void clickChangeJrAndFormFilling() {
        clickChangeInRow("JR and form filling");
    }

    public void clickChangeCounselCosts() {
        clickChangeInRow("Counsel costs");
    }


    public void clickAddAssessedTotalVat() {
        totalClaimValueTable.locator("#assessed-total-vat").click();
    }

    public void clickAddAssessedTotalInclVat() {
        totalClaimValueTable.locator("#assessed-total-incl-vat").click();
    }


    public void clickAddAllowedTotalVat() {
        totalAllowedValueTable.locator("#allowed-total-vat").click();
    }

    public void clickAddAllowedTotalInclVat() {
        totalAllowedValueTable.locator("#allowed-total-incl-vat").click();
    }


    public void submitAdjustments() {
        submitAdjustmentsButton.click();
    }

    public void discardChanges() {
        discardChangesLink.click();
    }


    public void assertCrimePageLoadedHeadersAndItems() {
        waitForPage();

        assertTableHasHeaders(claimCostsTable, "Item", "Calculated", "Requested", "Assessed");
        assertTableHasHeaders(totalClaimValueTable, "Item", "Calculated", "Requested", "Assessed");
        assertTableHasHeaders(totalAllowedValueTable, "Item", "Calculated", "Requested", "Allowed");

        assertClaimCostsHasItems(
                "Fixed fee",
                "Profit costs",
                "Disbursements",
                "Disbursement VAT",
                "Travel costs",
                "Waiting costs",
                "VAT",
                "Total"
        );

        assertThat(submitAdjustmentsButton).isVisible();
        assertThat(discardChangesLink).isVisible();
    }

    public void assertCivilPageLoadedHeadersAndItems() {
        waitForPage();

        assertTableHasHeaders(claimCostsTable, "Item", "Calculated", "Requested", "Assessed");
        assertTableHasHeaders(totalClaimValueTable, "Item", "Calculated", "Requested", "Assessed");
        assertTableHasHeaders(totalAllowedValueTable, "Item", "Calculated", "Requested", "Allowed");

        assertClaimCostsHasItems(
                "Fixed fee",
                "Profit costs",
                "Disbursements",
                "Disbursement VAT",
                "Detention travel and waiting costs",
                "JR and form filling",
                "Counsel costs",
                "VAT",
                "Total"
        );

        assertThat(submitAdjustmentsButton).isVisible();
        assertThat(discardChangesLink).isVisible();
    }

    public void assertSubmitTotalsRequiredErrors() {
        assertThat(errorSummary).isVisible();

        assertThat(errorSummary).containsText("The submission must include an assessed total VAT");
        assertThat(errorSummary).containsText("The submission must include an assessed total including VAT");
        assertThat(errorSummary).containsText("The submission must include an allowed total VAT");
        assertThat(errorSummary).containsText("The submission must include an allowed total including VAT");
    }

    private void assertTableHasHeaders(Locator table, String... headers) {
        assertThat(table).isVisible();
        for (String header : headers) {
            assertThat(table.getByRole(
                    AriaRole.COLUMNHEADER,
                    new Locator.GetByRoleOptions().setName(header)
            )).isVisible();
        }
    }

    private void assertClaimCostsHasItems(String... itemNames) {
        assertThat(claimCostsTable).isVisible();

        Locator rows = claimCostsTable.locator("tbody tr");
        assertThat(rows.first()).isVisible(); // strict-safe

        for (String item : itemNames) {
            assertThat(claimCostsTable).containsText(item);
        }
    }
}