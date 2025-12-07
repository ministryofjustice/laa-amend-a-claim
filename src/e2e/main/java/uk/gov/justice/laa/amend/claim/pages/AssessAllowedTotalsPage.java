package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class AssessAllowedTotalsPage {
    private final Page page;

    private final Locator heading;
    private final Locator allowedTotalVatInput;
    private final Locator allowedTotalInclVatInput;
    private final Locator saveButton;
    private final Locator cancelButton;

    public AssessAllowedTotalsPage(Page page) {
        this.page = page;
        this.heading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Assess total allowed value"));
        this.allowedTotalVatInput = page.locator("input#allowed-total-vat");
        this.allowedTotalInclVatInput = page.locator("input#allowed-total-incl-vat");
        this.saveButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Save changes"));
        this.cancelButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Cancel"));
    }

    public void waitForPage() { heading.waitFor(); }

    public void setAllowedTotalVat(String amount) {
        allowedTotalVatInput.fill(amount);
    }

    public void setAllowedTotalInclVat(String amount) {
        allowedTotalInclVatInput.fill(amount);
    }

    public void saveChanges() { saveButton.click(); }

    public void cancel() { cancelButton.click(); }
}