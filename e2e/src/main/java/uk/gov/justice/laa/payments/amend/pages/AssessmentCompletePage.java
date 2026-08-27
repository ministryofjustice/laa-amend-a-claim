package uk.gov.justice.laa.payments.amend.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Getter;

@Getter
public class AssessmentCompletePage extends LaaPage {

  private final Locator bodyText;
  private final Locator goToSearchButton;
  private final Locator viewAssessedClaimButton;

  public AssessmentCompletePage(Page page) {
    super(page, "Assessment complete");
    this.bodyText = page.locator(".govuk-panel__body");
    this.goToSearchButton = page.locator("#go-to-search");
    this.viewAssessedClaimButton = page.locator("#view-assessed-claim");
  }

  public void clickViewAssessedClaim() {
    viewAssessedClaimButton.click();
  }
}
