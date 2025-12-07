package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ReviewAndAmendPage {
    private final Page page;

    private final Locator heading;
    private final Locator claimCostsTable;
    private final Locator allowedTotalsTable;
    private final Locator submitAdjustmentsButton;
    private final Locator discardChangesButton;

    public ReviewAndAmendPage(Page page) {
        this.page = page;
        this.heading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Review and amend"));
        this.claimCostsTable = page.locator("table.govuk-table").first();
        this.allowedTotalsTable = page.locator("table.govuk-table").nth(1);
        this.submitAdjustmentsButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Submit adjustments"));
        this.discardChangesButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Discard changes"));
    }

    public void waitForPage() {
        heading.waitFor();
    }

    public String getHeadingText() {
        return heading.textContent().trim();
    }

<<<<<<< HEAD
=======
    // ---------- helpers for the “Change” links in Claim costs table ----------
>>>>>>> ef7f4cd (E2E Journey)

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

    public void clickChangeProfitCosts()       { clickChangeInRow("Profit costs"); }
    public void clickChangeDisbursements()     { clickChangeInRow("Disbursements"); }
    public void clickChangeDisbursementsVat()  { clickChangeInRow("Disbursement VAT"); }
    public void clickChangeTravelCosts()       { clickChangeInRow("Travel costs"); }
    public void clickChangeWaitingCosts()      { clickChangeInRow("Waiting costs"); }
    public void clickChangeVat()               { clickChangeInRow("VAT"); }

<<<<<<< HEAD
=======
    // ---------- “Add” links in Total allowed values table ----------
>>>>>>> ef7f4cd (E2E Journey)

    public void clickAddAllowedTotalVat() {
        allowedTotalsTable.locator("a#allowed-total-vat").click();
    }

    public void clickAddAllowedTotalInclVat() {
        allowedTotalsTable.locator("a#allowed-total-incl-vat").click();
    }

<<<<<<< HEAD
=======
    // ---------- Buttons ----------
>>>>>>> ef7f4cd (E2E Journey)

    public void submitAdjustments() {
        submitAdjustmentsButton.click();
    }

    public void discardChanges() {
        discardChangesButton.click();
    }
<<<<<<< HEAD

    public void clickDiscardChanges() {
    page.locator("a#discard").click();
}
=======
>>>>>>> ef7f4cd (E2E Journey)
}