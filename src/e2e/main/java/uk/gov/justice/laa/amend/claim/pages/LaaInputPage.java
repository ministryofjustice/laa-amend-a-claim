package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LaaInputPage extends LaaPage {

    protected final Locator saveButton;
    protected final Locator cancelButton;

    protected final Locator errorSummary;
    protected final Locator inlineErrors;

    public LaaInputPage(Page page, String heading) {
        super(page, heading);

        this.saveButton = page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Save changes")
        );

        this.cancelButton = page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Cancel")
        );

        this.errorSummary = page.locator(".govuk-error-summary");

        this.inlineErrors = page.locator(".govuk-error-message");
    }

    public void saveChanges() {
        saveButton.click();
    }

    public void cancel() {
        cancelButton.click();
    }
}
