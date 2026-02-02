package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.cardByTitle;
import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.summaryListRowByLabel;
import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.tableRowByLabel;

public class ClaimDetailsPage extends LaaPage {

    private final Locator addAssessmentOutcomeButton;
    private final Locator updateAssessmentOutcomeButton;
    private final Locator addUpdateAssessmentOutcomeButton;
    private final Locator infoAlert;
    private final Locator backToSearchButton;

    public ClaimDetailsPage(Page page) {
        super(page, "Claim details");

        this.addAssessmentOutcomeButton = page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Add assessment outcome")
        );

        this.updateAssessmentOutcomeButton = page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Update assessment outcome")
        );

        this.addUpdateAssessmentOutcomeButton = page.getByTestId("claim-details-assessment-button");

        this.infoAlert = page.locator(".moj-alert--information");

        this.backToSearchButton = page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Back to search")
        );
    }

    public void clickAddUpdateAssessmentOutcome() {
        if (addUpdateAssessmentOutcomeButton.isVisible()) {
            addUpdateAssessmentOutcomeButton.click();
        }
    }

    public void clickAddAssessmentOutcome() {
        addAssessmentOutcomeButton.click();
    }

    public void clickUpdateAssessmentOutcome() {
        addAssessmentOutcomeButton.click();
    }

    public void clickBackToSearchButton() {
        backToSearchButton.click();
    }

    public boolean isAddAssessmentOutcomeDisabled() {
        String ariaDisabled = addAssessmentOutcomeButton.getAttribute("aria-disabled");
        boolean aria = "true".equalsIgnoreCase(ariaDisabled);
        boolean disabledAttr = addAssessmentOutcomeButton.isDisabled();
        return aria || disabledAttr;
    }

    private Locator valuesCard() {
        return page.locator(".govuk-summary-card:has(h2.govuk-summary-card__title:has-text('Values'))").first();
    }

    public boolean hasValuesCard() {
        return valuesCard().count() > 0 && valuesCard().isVisible();
    }

    private Locator valuesRows() {
        return valuesCard().locator(".govuk-summary-list__row");
    }

    private Locator valuesRowByItem(String itemName) {
        if (!hasValuesCard()) return page.locator("non-existent-selector");

        Locator rows = valuesRows();
        int count = rows.count();

        for (int i = 0; i < count; i++) {
            Locator row = rows.nth(i);

            Locator dt = row.locator("dt.govuk-summary-list__key");
            if (dt.count() == 0) continue;

            String keyText = dt.first().textContent();
            if (keyText == null) continue;

            String normalized = keyText.trim();
            if (normalized.equals(itemName)) {
                return row;
            }
        }

        return page.locator("non-existent-selector");
    }

    public boolean valuesHasItem(String itemName) {
        return valuesRowByItem(itemName).count() > 0;
    }

    public String getCalculatedValue(String itemName) {
        Locator row = valuesRowByItem(itemName);
        if (row.count() == 0) {
            throw new AssertionError("Missing Values row for item: " + itemName);
        }

        Locator dds = row.locator("dd.govuk-summary-list__value");
        if (dds.count() < 2) {
            throw new AssertionError("Expected 2 values (Calculated + Requested) for item: " + itemName);
        }

        return dds.nth(0).textContent().trim();
    }

    public String getRequestedValue(String itemName) {
        Locator row = valuesRowByItem(itemName);
        if (row.count() == 0) {
            throw new AssertionError("Missing Values row for item: " + itemName);
        }

        Locator dds = row.locator("dd.govuk-summary-list__value");
        if (dds.count() < 2) {
            throw new AssertionError("Expected 2 values (Calculated + Requested) for item: " + itemName);
        }

        return dds.nth(1).textContent().trim();
    }

    public void assertAllValues(Map<String, String[]> expected) {
        if (!hasValuesCard()) {
            throw new AssertionError("Expected Values card to be visible, but it was not.");
        }

        for (Map.Entry<String, String[]> entry : expected.entrySet()) {
            String item = entry.getKey();
            String[] pair = entry.getValue();

            if (pair == null || pair.length != 2) {
                throw new AssertionError("Invalid expected pair for item '" + item + "'. Must be [calculated, requested].");
            }

            if (!valuesHasItem(item)) {
                throw new AssertionError("Missing Values row for item: " + item);
            }

            String expectedCalculated = pair[0];
            String expectedRequested = pair[1];

            String actualCalculated = getCalculatedValue(item);
            String actualRequested = getRequestedValue(item);

            if (!actualCalculated.equals(expectedCalculated)) {
                throw new AssertionError("Calculated mismatch for '" + item + "'. Expected: "
                        + expectedCalculated + " but was: " + actualCalculated);
            }

            if (!actualRequested.equals(expectedRequested)) {
                throw new AssertionError("Requested mismatch for '" + item + "'. Expected: "
                        + expectedRequested + " but was: " + actualRequested);
            }
        }
    }

    public String dumpValuesKeys() {
        if (!hasValuesCard()) return "Values card not present.";

        StringBuilder sb = new StringBuilder("Values keys found:\n");
        Locator rows = valuesRows();
        int count = rows.count();

        for (int i = 0; i < count; i++) {
            Locator dt = rows.nth(i).locator("dt.govuk-summary-list__key").first();
            if (dt.count() == 0) continue;
            String key = dt.textContent();
            if (key != null) sb.append("- ").append(key.trim()).append("\n");
        }

        return sb.toString();
    }

    public void assertCost(String label, String calculated, String submitted, String assessed) {
        assertSummaryListRow("Values", label, calculated, submitted, assessed);
    }

    public void assertAllowedTotals(String label, String calculated, String submitted, String allowed) {
        assertTableRow("Total allowed value", label, calculated, submitted, allowed);
    }

    public void assertAssessedTotals(String label, String calculated, String submitted, String assessed) {
        assertTableRow("Total claim value", label, calculated, submitted, assessed);
    }

    private void assertTableRow(String title, String label, String calculated, String submitted, String assessed) {
        Locator card = cardByTitle(title, page);
        Locator row = tableRowByLabel(card, label);

        // Check calculated value
        assertTableCellValue(row, 1, calculated);
        // Check submitted value
        assertTableCellValue(row, 2, submitted);
        // Check allowed value
        assertTableCellValue(row, 3, assessed);
    }

    private void assertSummaryListRow(String title, String label, String calculated, String submitted, String assessed) {
        Locator card = cardByTitle(title, page);
        Locator row = summaryListRowByLabel(card, label);
        // Check calculated value
        assertSummaryListValue(row, 0, calculated);
        // Check submitted value
        assertSummaryListValue(row, 1, submitted);
        // Check allowed value
        assertSummaryListValue(row, 2, assessed);
    }

    private void assertTableCellValue(Locator rowSelector, int columnIndex, String expectedValue) {
        String actualValue = rowSelector.locator("td").nth(columnIndex).textContent().trim();
        Assertions.assertEquals(expectedValue, actualValue,
            String.format("Value mismatch in column %d", columnIndex));
    }

    private void assertSummaryListValue(Locator rowSelector, int columnIndex, String expectedValue) {
        String actualValue = rowSelector.locator("dd").nth(columnIndex).textContent().trim();
        Assertions.assertEquals(expectedValue, actualValue, "Value mismatch in key");
    }

    public void assertAddAssessmentOutcomeButtonIsPresent() {
        addAssessmentOutcomeButton.waitFor();
    }

    public void assertUpdateAssessmentOutcomeButtonIsPresent() {
        updateAssessmentOutcomeButton.waitFor();
    }

    public void assertInfoAlertIsPresent() {
        Locator heading = infoAlert.locator(".moj-alert__heading");
        Assertions.assertEquals("This claim has been assessed", heading.textContent());
    }
}
