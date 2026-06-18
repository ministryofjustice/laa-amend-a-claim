package uk.gov.justice.laa.amend.claim.pages.amendments;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import uk.gov.justice.laa.amend.claim.pages.LaaPage;

public class ViewClientPage extends LaaPage {

  private final Locator changeLink;

  public ViewClientPage(Page page) {
    super(page, "Amend claim details");
    this.changeLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Change"));
  }

  public void clickChangeLink() {
    changeLink.click();
  }
}
