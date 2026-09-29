package uk.gov.justice.laa.payments.amend.pages.amendments;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.util.Locale;
import uk.gov.justice.laa.payments.amend.pages.LaaPage;

public class AmendFeeCodePage extends LaaPage {

  private final Locator continueButton;
  private final Locator feeCodeInput;
  private final Locator feeCodeMenu;

  public AmendFeeCodePage(Page page) {
    super(page, "Amend fee code");
    this.continueButton =
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
    this.feeCodeInput =
        page.locator("#fee-code-input.autocomplete__input.autocomplete__input--default");
    this.feeCodeMenu = page.locator("#fee-code-input__listbox");
  }

  public void assertTypeaheadBehaviour(
      String existingValue, String query, String expectedMatchingOption) {
    assertThat(feeCodeInput).hasValue(existingValue);
    assertThat(page.locator(".autocomplete__dropdown-arrow-down")).hasCount(0);
    assertThat(feeCodeMenu).isHidden();

    feeCodeInput.click();

    assertThat(feeCodeMenu).isHidden();
    assertEquals("text", feeCodeInput.evaluate("input => getComputedStyle(input).cursor"));
    assertEquals(
        true,
        feeCodeInput.evaluate(
            "input => input.selectionStart === 0"
                + " && input.selectionEnd === input.value.length"));

    feeCodeInput.pressSequentially(query);

    assertThat(feeCodeInput).hasValue(query);
    assertThat(feeCodeMenu).isVisible();
    assertThat(feeCodeMenu).containsText(expectedMatchingOption);
    assertTrue(
        feeCodeMenu.locator(".autocomplete__option").allTextContents().stream()
            .allMatch(
                option ->
                    option.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))));
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
