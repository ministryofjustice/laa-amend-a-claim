package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class AssessmentCompletePage extends LaaPage {

    private final Locator bodyText;
    private final Locator goToSearchButton;
    private final Locator viewAssessedClaimButton;

    public AssessmentCompletePage(Page page) {
        super(page, "Assessment Complete");
        this.bodyText = page.locator(".govuk-panel__body");
        this.goToSearchButton = page.locator("#go-to-search");
        this.viewAssessedClaimButton = page.locator("#view-assessed-claim");
    }

    public String getBodyText() {
        return bodyText.textContent().trim();
    }

    public boolean goToSearchExists() {
        return goToSearchButton.isVisible();
    }

    public boolean viewAssessedClaimExists() {
        return viewAssessedClaimButton.isVisible();
    }

    public void clickGoToSearch() {
        goToSearchButton.click();
    }

    public void clickViewAssessedClaim() {
        viewAssessedClaimButton.click();
    }
}