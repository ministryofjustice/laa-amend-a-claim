package uk.gov.justice.laa.payments.amend.pages.amendments;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import uk.gov.justice.laa.payments.amend.pages.LaaPage;

public class CheckPage extends LaaPage {

  private final Locator submitButton;

  public CheckPage(Page page) {
    super(page, "Check your amendments");
    this.submitButton =
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit amendments"));
  }

  public void clickSubmitButton() {
    submitButton.click();
  }
}
