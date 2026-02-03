package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static uk.gov.justice.laa.amend.claim.pages.PageHelper.cardByTitle;
import static uk.gov.justice.laa.amend.claim.pages.PageHelper.tableByCard;

public class ReviewAndAmendPage {
    private final Page page;

    private final Locator heading;
    private final Locator backLink;

    private final Locator assessmentOutcomeCard;
    private final Locator claimCostsCard;
    private final Locator totalClaimValueCard;
    private final Locator totalAllowedValueCard;

    private final Locator assessmentTable;
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

        this.backLink = page.locator(".govuk-back-link");

        this.assessmentOutcomeCard = cardByTitle("Assessment", page);
        this.claimCostsCard = cardByTitle("Claim costs", page);
        this.totalClaimValueCard = cardByTitle("Total claim value", page);
        this.totalAllowedValueCard = cardByTitle("Total allowed value", page);

        this.assessmentTable = tableByCard(assessmentOutcomeCard);
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

    public void waitForPage() {
        heading.waitFor();
        assessmentOutcomeCard.waitFor();
        claimCostsCard.waitFor();
        if (totalClaimValueCard.isVisible()) {
            totalClaimValueCard.waitFor();
        }
        totalAllowedValueCard.waitFor();
    }

    public String getHeadingText() {
        return heading.textContent().trim();
    }



    private Locator rowByItemName(String itemName, Locator locator) {
        return locator.locator("tbody tr").filter(
                new Locator.FilterOptions().setHasText(itemName)
        ).first();
    }

    private void clickChangeInRow(String itemName, Locator locator) {
        rowByItemName(itemName, locator)
            .locator("a.govuk-link:has-text('Change')")
            .click();
    }

    private void clickAddLink(String itemName, String id, Locator locator) {
        Locator link = rowByItemName(itemName, locator).getByTestId(id);
        clickAddLink(link);
    }

    private void clickAddLink(Locator link) {
        assertThat(link).containsText("Add");
        link.click();
    }

    public void clickAssessmentOutcome()      { clickChangeInRow("Assessment outcome", assessmentTable); }
    public void clickLiableForVat()      { clickChangeInRow("Is this claim liable for VAT?", assessmentTable); }
    public void clickChangeProfitCosts()      { clickChangeInRow("Profit costs", claimCostsTable); }
    public void clickAddProfitCosts()         { clickAddLink("Profit costs", "claim-field-profitCost", claimCostsTable); }
    public void clickChangeDisbursements()    { clickChangeInRow("Disbursements", claimCostsTable); }
    public void clickChangeDisbursementsVat() { clickChangeInRow("Disbursement VAT", claimCostsTable); }
    public void clickChangeTravelCosts()      { clickChangeInRow("Travel costs", claimCostsTable); }
    public void clickChangeWaitingCosts()     { clickChangeInRow("Waiting costs", claimCostsTable); }
    public void clickChangeVat()              { clickChangeInRow("VAT", claimCostsTable); }

    public void clickChangeDetentionTravelAndWaitingCosts() {
        clickChangeInRow("Detention travel and waiting costs", claimCostsTable);
    }

    public void clickChangeJrAndFormFilling() {
        clickChangeInRow("JR and form filling", claimCostsTable);
    }

    public void clickChangeCounselCosts() {
        clickChangeInRow("Counsel costs", claimCostsTable);
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
        waitForPage();

        assertTableHasHeaders(claimCostsTable, "Item", "Calculated", "Requested", "Assessed");
        assertTableHasHeaders(totalClaimValueTable, "Item", "Calculated", "Requested", "Assessed");
        assertTableHasHeaders(totalAllowedValueTable, "Item", "Calculated", "Requested", "Allowed");

        assertAssessmentHasItems("Assessment outcome", "Is this claim liable for VAT?");
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
        waitForPage();

        assertTableHasHeaders(claimCostsTable, "Item", "Calculated", "Requested", "Assessed");
        assertTableHasHeaders(totalClaimValueTable, "Item", "Calculated", "Requested", "Assessed");
        assertTableHasHeaders(totalAllowedValueTable, "Item", "Calculated", "Requested", "Allowed");

        assertAssessmentHasItems("Assessment outcome", "Is this claim liable for VAT?");
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

    private void assertAssessmentHasItems(String... itemNames) {
        assertThat(assessmentTable).isVisible();

        Locator rows = assessmentTable.locator("tbody tr");
        assertThat(rows.first()).isVisible(); // strict-safe

        for (String item : itemNames) {
            assertThat(assessmentTable).containsText(item);
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