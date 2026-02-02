package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.cardByTitle;
import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.tableByCard;

public class ReviewAndAmendPage extends LaaPage {

    private final Locator backLink;

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
        super(page, "Review and amend");

        this.backLink = page.locator(".govuk-back-link");

        this.claimCostsCard = cardByTitle("Claim costs", page);
        this.totalClaimValueCard = cardByTitle("Total claim value", page);
        this.totalAllowedValueCard = cardByTitle("Total allowed value", page);

        this.claimCostsTable = tableByCard(claimCostsCard);
        this.totalClaimValueTable = tableByCard(totalClaimValueCard);
        this.totalAllowedValueTable = tableByCard(totalAllowedValueCard);

        this.submitAdjustmentsButton = page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Submit adjustments")
        );

        this.discardChangesLink = page.locator("#discard");

        this.errorSummary = page.locator(".govuk-error-summary");
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

    private void clickAddLink(String itemName, String id) {
        Locator link = rowByItemName(itemName).getByTestId(id);
        clickAddLink(link);
    }

    private void clickAddLink(Locator link) {
        assertThat(link).containsText("Add");
        link.click();
    }

    public void clickChangeProfitCosts()      { clickChangeInRow("Profit costs"); }
    public void clickAddProfitCosts()         { clickAddLink("Profit costs", "claim-field-profitCost"); }
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
        Locator link = totalClaimValueTable.getByTestId("totals-assessedTotalVat");
        clickAddLink(link);
    }

    public void clickAddAllowedTotalVat() {
        Locator link = totalAllowedValueTable.getByTestId("totals-allowedTotalVat");
        clickAddLink(link);
    }

    public void submitAdjustments() {
        submitAdjustmentsButton.click();
    }

    public void discardChanges() {
        discardChangesLink.click();
    }

    public void clickBackLink() {
        backLink.click();
    }

    public boolean isAssessClaimValuesVisible() {
        return totalClaimValueCard.isVisible();
    }

    public void assertCrimePageLoadedHeadersAndItems() {
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
                "VAT"
        );
        assertClaimCostsNotHasItems("Total");
        assertThat(submitAdjustmentsButton).isVisible();
        assertThat(discardChangesLink).isVisible();
    }

    public void assertCivilPageLoadedHeadersAndItems() {
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
                "VAT"
        );
        assertClaimCostsNotHasItems("Total");

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

    private void assertClaimCostsNotHasItems(String... itemNames) {
        assertThat(claimCostsTable).isVisible();

        Locator rows = claimCostsTable.locator("tbody tr");
        assertThat(rows.first()).isVisible(); // strict-safe

        for (String item : itemNames) {
            assertThat(claimCostsTable).not().containsText(item);
        }
    }

    public void assertAllowedTotalsAreCorrect(String allowedTotalVat, String allowedTotalInclVat) {
        assertThat(totalAllowedValueTable).isVisible();

        List<Locator> rows = totalAllowedValueTable.locator("tbody tr").all();

        List<Locator> allowedTotalVatRow = rows.get(0).locator("td").all();
        assertThat(allowedTotalVatRow.get(1)).containsText(allowedTotalVat);

        List<Locator> allowedTotalInclVatRow = rows.get(1).locator("td").all();
        assertThat(allowedTotalInclVatRow.get(1)).containsText(allowedTotalInclVat);
    }
}