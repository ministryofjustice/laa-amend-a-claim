package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public abstract class LaaInputPage extends LaaErrorSummaryPage {

    protected Locator inlineErrors;

    public LaaInputPage(Page page, String heading) {
        super(page, heading);

        this.inlineErrors = page.locator(".govuk-error-message");
    }
}
