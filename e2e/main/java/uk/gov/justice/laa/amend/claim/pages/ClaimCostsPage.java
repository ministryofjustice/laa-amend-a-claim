package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ClaimCostsPage extends LaaPage {

  private final Locator claimHistory;

  public ClaimCostsPage(Page page) {
    super(page, "Costs");
    this.claimHistory =
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Claim history"));
  }

  public void clickClaimHistoryItem() {
    claimHistory.click();
  }
}
