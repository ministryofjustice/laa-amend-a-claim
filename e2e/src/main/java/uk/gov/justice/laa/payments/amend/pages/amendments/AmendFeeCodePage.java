package uk.gov.justice.laa.payments.amend.pages.amendments;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import lombok.Getter;
import uk.gov.justice.laa.payments.amend.pages.LaaPage;

public class AmendFeeCodePage extends LaaPage {

  private final Locator continueButton;
  @Getter
  private final Locator feeCodeInput;
  @Getter
  private final Locator feeCodeMenu;

  public AmendFeeCodePage(Page page) {
    super(page, "Amend fee code");
    this.continueButton =
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
    this.feeCodeInput = page.locator("#fee-code-input.autocomplete__input");
    this.feeCodeMenu = page.locator("#fee-code-input__listbox");
  }

  public boolean isAllInputTextSelected() {
    return (boolean)
        feeCodeInput.evaluate(
            "input => input.selectionStart === 0"
                + " && input.selectionEnd === input.value.length");
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
