package uk.gov.justice.laa.amend.claim.pages.amendments;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import uk.gov.justice.laa.amend.claim.pages.LaaPage;

public class AmendFeeCodePage extends LaaPage {

  private final Locator continueButton;
  private final Locator feeCodeInput;

  public AmendFeeCodePage(Page page) {
    super(page, "Fee code");

    this.continueButton =
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
    this.feeCodeInput =
        page.locator("#fee-code-input.autocomplete__input.autocomplete__input--default");
  }

  public void fillFeeCodeInput(String value) {
    assertThat(feeCodeInput).isVisible();
    feeCodeInput.clear();
    feeCodeInput.fill(value);
    feeCodeInput.press("Enter");
  }

  public void clickContinueButton() {
    continueButton.click();
  }
}
