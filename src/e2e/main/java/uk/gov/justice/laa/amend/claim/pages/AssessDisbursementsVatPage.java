package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class AssessDisbursementsVatPage {
    private final Page page;

    private final Locator heading;
    private final Locator valueInput;
    private final Locator saveButton;
    private final Locator cancelButton;

    public AssessDisbursementsVatPage(Page page) {
        this.page = page;
        this.heading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Assess disbursements VAT"));
        this.valueInput = page.locator("input#value");
        this.saveButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Save changes"));
        this.cancelButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Cancel"));
    }

    public void waitForPage() { heading.waitFor(); }

    public void setAssessedValue(String amount) { valueInput.fill(amount); }

    public void saveChanges() { saveButton.click(); }

    public void cancel() { cancelButton.click(); }
}