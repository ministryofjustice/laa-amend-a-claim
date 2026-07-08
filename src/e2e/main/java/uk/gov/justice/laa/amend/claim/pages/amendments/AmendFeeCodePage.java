package uk.gov.justice.laa.amend.claim.pages.amendments;

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
    this.feeCodeInput = page.locator("#fee-code-input");
  }

  public void fillFeeCodeInput(String value) {
    feeCodeInput.clear();
    feeCodeInput.fill(value);
    feeCodeInput.press("Enter");
  }

  public void clickContinueButton() {
    continueButton.click();
  }
}
