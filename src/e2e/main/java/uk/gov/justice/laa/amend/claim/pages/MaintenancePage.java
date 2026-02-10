package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class MaintenancePage extends LaaPage {

    private final Locator bodyText;

    public MaintenancePage(Page page) {
        super(page, "Service maintenance");
        this.bodyText = page.locator(".govuk-body");
    }

    public String getBodyText() {
        return bodyText.textContent().trim();
    }
}
