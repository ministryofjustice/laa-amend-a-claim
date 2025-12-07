package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ClaimDetailsPage {
    private final Page page;

    private final Locator heading;
    private final Locator addAssessmentOutcomeButton;

    public ClaimDetailsPage(Page page) {
        this.page = page;

        this.heading = page.getByRole(
                AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Claim details")
        );

        this.addAssessmentOutcomeButton = page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Add assessment outcome")
        );
    }

    public void waitForPage() { heading.waitFor(); }

    public String getHeadingText() { return heading.textContent().trim(); }

    public void clickAddAssessmentOutcome() {
        addAssessmentOutcomeButton.click();
    }
}