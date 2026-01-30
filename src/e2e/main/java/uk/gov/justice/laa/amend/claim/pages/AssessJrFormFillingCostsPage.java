package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AssessJrFormFillingCostsPage extends LaaPage {

    private final Locator valueInput;
    private final Locator saveButton;
    private final Locator cancelButton;

    private final Locator errorSummary;
    private final Locator inlineError;

    public AssessJrFormFillingCostsPage(Page page) {
        super(page, "Assess JR and form filling");

        this.valueInput = page.locator("input#value");

        this.saveButton = page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Save changes")
        );

        this.cancelButton = page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Cancel")
        );

        this.errorSummary = page.locator(".govuk-error-summary");
        this.inlineError = page.locator(".govuk-error-message");
    }

    public void setAssessedValue(String amount) { valueInput.fill(amount); }

    public void saveChanges() { saveButton.click(); }

    public void cancel() { cancelButton.click(); }

    public void assertMustBeNumberWithUpTo2DpError() {
        assertThat(errorSummary).isVisible();
        assertThat(inlineError).isVisible();
        assertThat(errorSummary).containsText("must be a number with up to 2 decimal places");
        assertThat(inlineError).containsText("must be a number with up to 2 decimal places");
    }
}