package uk.gov.justice.laa.amend.claim.pages.amendments;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import uk.gov.justice.laa.amend.claim.pages.LaaPage;

public class CheckPage extends LaaPage {

  private final Locator changeClientOneLink;
  private final Locator changeClientTwoLink;
  private final Locator submitButton;
  private final Locator cancelLink;

  public CheckPage(Page page) {
    super(page, "Check your amendments");
    this.changeClientOneLink = page.locator("#change-client1");
    this.changeClientTwoLink = page.locator("#change-client2");
    this.submitButton =
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit amendments"));
    this.cancelLink =
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cancel amendments"));
  }

  public void clickChangeClientOneLink() {
    changeClientOneLink.click();
  }

  public void clickChangeClientTwoLink() {
      changeClientTwoLink.click();
  }

  public void clickSubmitButton() {
    submitButton.click();
  }

  public void clickCancelLink() {
    cancelLink.click();
  }
}
