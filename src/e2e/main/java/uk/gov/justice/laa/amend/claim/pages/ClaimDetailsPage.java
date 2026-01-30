package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.cardByTitle;
import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.rowByLabel;

public class ClaimDetailsPage extends LaaPage {

    private final Locator addAssessmentOutcomeButton;
    private final Locator updateAssessmentOutcomeButton;
    private final Locator addUpdateAssessmentOutcomeButton;

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

    public void assertAssessedTotals(Map<String, String[]> expectedTotals) {
        Locator totalAssessedValuesCard = cardByTitle("Total claim value", page);
        for (Map.Entry<String, String[]> entry : expectedTotals.entrySet()) {

            Locator row = rowByLabel(totalAssessedValuesCard, entry.getKey());
            String[] values = entry.getValue();

            // Check calculated value
            assertCellValue(row, 1, values[0]);
            // Check submitted value
            assertCellValue(row, 2, values[1]);
            // Check assessed value
            assertCellValue(row, 3, values[2]);
        }
    }

    public void assertAllowedTotals(Map<String, String[]> expectedTotals) {
        Locator totalAllowedValuesCard = cardByTitle("Total allowed value", page);
        for (Map.Entry<String, String[]> entry : expectedTotals.entrySet()) {
            Locator row = rowByLabel(totalAllowedValuesCard, entry.getKey());
            String[] values = entry.getValue();

            // Check calculated value
            assertCellValue(row, 1, values[0]);
            // Check submitted value
            assertCellValue(row, 2, values[1]);
            // Check allowed value
            assertCellValue(row, 3, values[2]);
        }
    }

    private void assertCellValue(Locator rowSelector, int columnIndex, String expectedValue) {
        String actualValue = rowSelector.locator("td").nth(columnIndex).textContent().trim();
        Assertions.assertEquals(expectedValue, actualValue,
            String.format("Value mismatch in column %d", columnIndex));
    }

    public void assertAddAssessmentOutcomeButtonIsPresent() {
        addAssessmentOutcomeButton.waitFor();
    }

    public void assertUpdateAssessmentOutcomeButtonIsPresent() {
        updateAssessmentOutcomeButton.waitFor();
    }
}