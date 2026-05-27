package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ClaimCasePage extends LaaPage {

  private final Locator claimHistory;

  public ClaimCasePage(Page page) {
    super(page, "Case");
    this.claimHistory =
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Claim history"));
  }

  public void clickClaimHistoryItem() {
    claimHistory.click();
  }
}
