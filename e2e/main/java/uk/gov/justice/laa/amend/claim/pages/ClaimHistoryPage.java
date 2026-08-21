package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ClaimHistoryPage extends LaaPage {

  private final Locator overviewLink;

  public ClaimHistoryPage(Page page) {
    super(page, "Claim history");

    this.overviewLink =
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Overview"));
  }

  public void clickOverviewItem() {
    overviewLink.click();
  }
}
