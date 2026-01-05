package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class DiscardAssessmentPage {
    private final Page page;

    private final Locator heading;
    private final Locator discardButton;
    private final Locator returnToClaimLink;

    public DiscardAssessmentPage(Page page) {
        this.page = page;

        this.heading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Confirm you want to discard this assessment"));

        this.discardButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Discard assessment"));

        this.returnToClaimLink = page.locator("#return-to-claim");
    }

    public void waitForPage() { heading.waitFor(); }

    public String getHeadingText() { return heading.textContent().trim(); }

    public boolean isDiscardAssessmentButtonVisible() {
        return discardButton.isVisible();
    }

    public boolean isReturnToClaimLinkVisible() {
        return returnToClaimLink.isVisible();
    }

    public void clickDiscardAssessment() { discardButton.click(); }

    public void clickReturnToClaim() { returnToClaimLink.click(); }
}