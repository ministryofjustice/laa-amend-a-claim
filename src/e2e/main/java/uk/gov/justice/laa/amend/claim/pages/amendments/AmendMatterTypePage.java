package uk.gov.justice.laa.amend.claim.pages.amendments;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import uk.gov.justice.laa.amend.claim.pages.LaaPage;

public class AmendMatterTypePage extends LaaPage {

  private final Locator continueButton;
  private final Locator matterTypeCode;
  private final Locator matterTypeCodeOne;
  private final Locator matterTypeCodeTwo;

  public AmendMatterTypePage(Page page) {
    super(page, "Amend matter type");
    this.continueButton =
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
    this.matterTypeCode = page.locator("#matter-type-input");
    this.matterTypeCodeOne = page.locator("#matter-type-one-input");
    this.matterTypeCodeTwo = page.locator("#matter-type-two-input");
  }

  public void fillMatterTypeCode(String value) {
    assertThat(matterTypeCode).isVisible();
    matterTypeCode.fill(value);
  }

  public void fillMatterTypeCodeOne(String value) {
    assertThat(matterTypeCodeOne).isVisible();
    matterTypeCodeOne.fill(value);
  }

  public void fillMatterTypeCodeTwo(String value) {
    assertThat(matterTypeCodeTwo).isVisible();
    matterTypeCodeTwo.fill(value);
  }

  public void clickContinueButton() {
    continueButton.click();
  }
}
