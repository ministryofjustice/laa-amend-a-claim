package uk.gov.justice.laa.amend.claim.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public abstract class LaaCostPage extends LaaInputPage {

    protected final Locator valueInput;

    public LaaCostPage(Page page, String heading) {
        super(page, heading);

        this.valueInput = page.locator("input#value");
    }

    public void setAssessedValue(String amount) {
        valueInput.fill(amount);
    }

    public void assertMustBeNumberWithUpTo2DpError() {
        waitForPageErrors();

        assertThat(errorSummary).containsText("must be a number with up to 2 decimal places");
        assertThat(inlineErrors).containsText("must be a number with up to 2 decimal places");
    }
}
