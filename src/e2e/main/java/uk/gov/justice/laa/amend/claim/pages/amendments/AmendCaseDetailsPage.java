package uk.gov.justice.laa.amend.claim.pages.amendments;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import uk.gov.justice.laa.amend.claim.pages.LaaPage;

public class AmendCaseDetailsPage extends LaaPage {

  private final Locator continueButton;

  public AmendCaseDetailsPage(Page page) {
    super(page, "Amend claim details");
    this.continueButton =
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
  }

  public void fillInput(String inputKey, String value) {
    var caseDetailInput = page.locator(String.format("input#inputs%s", inputKey));
    assertThat(caseDetailInput).isVisible();
    caseDetailInput.fill(value);
  }

  public void fillTypeaheadInput(String inputKey, String value) {
    var caseDetailInput = page.locator(String.format("input#%s", inputKey));
    assertThat(caseDetailInput).isVisible();
    caseDetailInput.clear();
    caseDetailInput.fill(value);
    caseDetailInput.press("Enter");
  }

  public void clickContinueButton() {
    continueButton.click();
  }
}
