package uk.gov.justice.laa.amend.claim.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.cardByTitle;
import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.tableByCard;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.util.List;

public class ReviewAndAmendPage extends LaaErrorSummaryPage {

    private final Locator backLink;

    private final Locator assessmentOutcomeCard;
    private final Locator claimCostsCard;
    private final Locator totalClaimValueCard;
    private final Locator totalAllowedValueCard;

    private final Locator assessmentTable;
    private final Locator claimCostsTable;
    private final Locator totalClaimValueTable;
    private final Locator totalAllowedValueTable;

    public ReviewAndAmendPage(Page page) {
        super(page, "Review and amend");

        this.backLink = page.locator(".govuk-back-link");

        this.assessmentOutcomeCard = cardByTitle("Assessment", page);
        this.claimCostsCard = cardByTitle("Claim costs", page);
        this.totalClaimValueCard = cardByTitle("Total claim value", page);
        this.totalAllowedValueCard = cardByTitle("Total allowed value", page);

        this.assessmentTable = tableByCard(assessmentOutcomeCard);
        this.claimCostsTable = tableByCard(claimCostsCard);
        this.totalClaimValueTable = tableByCard(totalClaimValueCard);
        this.totalAllowedValueTable = tableByCard(totalAllowedValueCard);

        this.saveButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit adjustments"));

        this.cancelButton = page.locator("#discard");
    }

    private Locator rowByItemName(String itemName, Locator locator) {
        return locator.locator("tbody tr")
                .filter(new Locator.FilterOptions().setHasText(itemName))
                .first();
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

    public void clickAssessmentOutcome() {
        clickChangeInRow("Assessment outcome", assessmentTable);
    }

    public void clickContingencyAssessment() {
        clickChangeInRow("Was this claim assessed as part of the contingency process?", assessmentTable);
    }

    public void clickChangeProfitCosts() {
        clickChangeInRow("Profit costs", claimCostsTable);
    }

    public void clickAddProfitCosts() {
        clickAddLink("Profit costs", "claim-field-profitCost", claimCostsTable);
    }

    public void clickChangeDisbursements() {
        clickChangeInRow("Disbursements", claimCostsTable);
    }

    public void clickChangeDisbursementsVat() {
        clickChangeInRow("Disbursement VAT", claimCostsTable);
    }

    public void clickChangeTravelCosts() {
        clickChangeInRow("Travel costs", claimCostsTable);
    }

    public void clickChangeWaitingCosts() {
        clickChangeInRow("Waiting costs", claimCostsTable);
    }

    public void clickChangeVat() {
        clickChangeInRow("VAT", claimCostsTable);
    }

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

        assertAssessmentHasItems("Assessment outcome", "Was this claim assessed as part of the contingency process?");
        assertClaimCostsHasItems(
                "Fixed fee", "Profit costs", "Disbursements", "Disbursement VAT", "Travel costs", "Waiting costs");
        assertClaimCostsNotHasItems("Total");
        assertThat(saveButton).isVisible();
        assertThat(cancelButton).isVisible();
    }

    public void assertCivilPageLoadedHeadersAndItems() {
        assertTableHasHeaders(claimCostsTable, "Item", "Calculated", "Requested", "Assessed");
        assertTableHasHeaders(totalClaimValueTable, "Item", "Calculated", "Requested", "Assessed");
        assertTableHasHeaders(totalAllowedValueTable, "Item", "Calculated", "Requested", "Allowed");

        assertAssessmentHasItems("Assessment outcome", "Was this claim assessed as part of the contingency process?");
        assertClaimCostsHasItems(
                "Fixed fee",
                "Profit costs",
                "Disbursements",
                "Disbursement VAT",
                "Detention travel and waiting costs",
                "JR and form filling",
                "Counsel costs");
        assertClaimCostsNotHasItems("Total");

        assertThat(saveButton).isVisible();
        assertThat(cancelButton).isVisible();
    }

    public void assertSubmitTotalsRequiredErrors() {
        waitForPageErrors();
        assertErrorMessage("The submission must include an assessed total VAT");
        assertErrorMessage("The submission must include an assessed total including VAT");
        assertErrorMessage("The submission must include an allowed total VAT");
        assertErrorMessage("The submission must include an allowed total including VAT");
    }

    private void assertErrorMessage(String message) {
        assertThat(errorSummary).containsText(message);
    }

    public void assertProfitCostRequiredErrors() {
        waitForPageErrors();
        assertErrorMessage("The submission must include profit costs");
    }

    private void assertTableHasHeaders(Locator table, String... headers) {
        assertThat(table).isVisible();
        for (String header : headers) {
            assertThat(table.getByRole(AriaRole.COLUMNHEADER, new Locator.GetByRoleOptions().setName(header)))
                    .isVisible();
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
