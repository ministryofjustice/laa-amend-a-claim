package uk.gov.justice.laa.amend.claim.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public abstract class LaaTotalsPage extends LaaInputPage {

    protected Locator totalVatInput;
    protected Locator totalInclVatInput;

    protected String totalVatError;
    protected String totalInclVatError;

    public LaaTotalsPage(Page page, String heading) {
        super(page, heading);
    }

    public void setTotalVat(String amount) {
        totalVatInput.fill(amount);
    }

    public void setTotalInclVat(String amount) {
        totalInclVatInput.fill(amount);
    }

    public void assertRequiredErrorsShown() {
        assertErrorsShown(totalVatError, totalInclVatError);
    }

    public void assertNumericErrorsShown() {
        assertErrorsShown("The total VAT must be", "The total value must");
    }

    private void assertErrorsShown(String totalVatError, String totalInclVatError) {
        waitForPageErrors();

        assertThat(errorSummary).containsText(totalVatError);
        assertThat(errorSummary).containsText(totalInclVatError);

        assertThat(inlineErrors
                        .filter(new Locator.FilterOptions().setHasText(totalVatError))
                        .first())
                .isVisible();
        assertThat(inlineErrors
                        .filter(new Locator.FilterOptions().setHasText(totalInclVatError))
                        .first())
                .isVisible();
    }
}
