package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ClaimCasePage extends LaaPage {

  private final Locator costs;

  public ClaimCasePage(Page page) {
    super(page, "Case");
    this.costs = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Costs"));
  }

  public void clickCostsItem() {
    costs.click();
  }
}
