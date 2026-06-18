package uk.gov.justice.laa.amend.claim.pages.amendments;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import uk.gov.justice.laa.amend.claim.pages.LaaPage;

public class AmendClient1Page extends LaaPage {

  private final Locator continueButton;

  public AmendClient1Page(Page page) {
    super(page, "Amend claim details");
    this.continueButton =
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
  }

  public void fillInput(String inputKey, String value) {
    var surnameInput = page.locator(String.format("input#inputs%s", inputKey));
    assertThat(surnameInput).isVisible();
    surnameInput.fill(value);
  }

  public void clickContinueButton() {
    continueButton.click();
  }
}
