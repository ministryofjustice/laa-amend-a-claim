package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ClaimClientPage extends LaaPage {

  private final Locator claimHistoryLink;

  public ClaimClientPage(Page page) {
    super(page, "Client");
    this.claimHistoryLink =
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Claim history"));
  }

  public void clickClaimHistoryItem() {
    claimHistoryLink.click();
  }
}
