package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.concurrent.ConcurrentHashMap;

public class AssessmentCompletePage {
    private final Page page;

    private final Locator heading;
    private final Locator bodyText;
    private final Locator goToSearchButton;
    private final Locator viewAssessedClaimButton;

    public AssessmentCompletePage(Page page) {
        this.page = page;
        this.heading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Assessment complete"));
        this.bodyText = page.locator(".govuk-panel__body");
        this.goToSearchButton = page.locator("#go-to-search");
        this.viewAssessedClaimButton = page.locator("#view-assessed-claim");
    }

    public void waitForPage() {
        heading.waitFor();
    }

    public String getHeadingText() {
        return heading.textContent().trim();
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

    public String getAssessmentId() {
        String url = page.url();
        int lastSlashIndex = url.lastIndexOf("/");
        return url.substring(lastSlashIndex + 1);
    }

    public void storeAssessmentId(ConcurrentHashMap<String, String> store) {
        store.put("assessmentId", getAssessmentId());
    }
}