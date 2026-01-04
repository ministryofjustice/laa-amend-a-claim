package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AssessAllowedTotalsPage {

    private final Page page;

    private final Locator heading;
    private final Locator allowedTotalVatInput;
    private final Locator allowedTotalInclVatInput;
    private final Locator saveButton;
    private final Locator cancelLink;

    private final Locator errorSummary;
    private final Locator inlineErrors;

    public AssessAllowedTotalsPage(Page page) {
        this.page = page;

        this.heading = page.getByRole(
                AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Assess total allowed value")
        );

        this.allowedTotalVatInput = page.locator("#allowed-total-vat");
        this.allowedTotalInclVatInput = page.locator("#allowed-total-incl-vat");

        this.saveButton = page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Save changes")
        );

        this.cancelLink = page.getByRole(
                AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Cancel")
        );

        this.errorSummary = page.locator(".govuk-error-summary");
        this.inlineErrors = page.locator(".govuk-error-message");
    }

    public void waitForPage() { heading.waitFor(); }

    public void setAllowedTotalVat(String amount) { allowedTotalVatInput.fill(amount); }

    public void setAllowedTotalInclVat(String amount) { allowedTotalInclVatInput.fill(amount); }

    public void saveChanges() { saveButton.click(); }

    public void cancel() { cancelLink.click(); }

    public void assertRequiredErrorsShown() {
        assertThat(errorSummary).isVisible();

        assertThat(errorSummary).containsText("Enter the total allowed VAT");
        assertThat(errorSummary).containsText("Enter the total allowed value of the claim");

        assertThat(inlineErrors.first()).isVisible();

        assertThat(
                inlineErrors.filter(new Locator.FilterOptions().setHasText("Enter the total allowed VAT")).first()
        ).isVisible();

        assertThat(
                inlineErrors.filter(new Locator.FilterOptions().setHasText("Enter the total allowed value of the claim")).first()
        ).isVisible();
    }

    public void assertNumericErrorsShown() {
        assertThat(errorSummary).isVisible();
        assertThat(inlineErrors.first()).isVisible();

        assertThat(errorSummary).containsText("The total VAT must be");
        assertThat(errorSummary).containsText("The total value must");

        assertThat(
                inlineErrors.filter(new Locator.FilterOptions().setHasText("The total VAT must be")).first()
        ).isVisible();

        assertThat(
                inlineErrors.filter(new Locator.FilterOptions().setHasText("The total value must")).first()
        ).isVisible();
    }
}