package uk.gov.justice.laa.amend.claim.pages.amendments;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ViewClientPage extends BaseAmendmentPage {

  private final Locator changeLink;
  private final Locator continueButton;

  public ViewClientPage(Page page) {
    super(page);
    this.changeLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Change"));
    this.continueButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
  }

  public void clickChangeLink() {
    changeLink.click();
  }

  public void clickContinueButton() {
    continueButton.click();
  }
}
