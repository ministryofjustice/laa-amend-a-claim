package uk.gov.justice.laa.amend.claim.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public abstract class LaaErrorSummaryPage extends LaaPage {

    protected Locator saveButton;
    protected Locator cancelButton;

    protected Locator errorSummary;

    public LaaErrorSummaryPage(Page page, String heading) {
        super(page, heading);

        this.saveButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save changes"));

        this.cancelButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));

        this.errorSummary = page.locator(".govuk-error-summary");
    }

    public void saveChanges() {
        saveButton.click();
    }

    public void cancel() {
        cancelButton.click();
    }

    public void waitForPageErrors() {
        assertThat(errorSummary).isVisible();
        generateErrorSummaryAxeReport();
    }
}
