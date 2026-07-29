package uk.gov.justice.laa.amend.claim.pages.amendments;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ViewCasePage extends BaseAmendmentPage {

  private final Locator changeCaseTypeLink;
  private final Locator changeCaseDetailsLink;
  private final Locator continueButton;

  public ViewCasePage(Page page) {
    super(page);
    this.changeCaseTypeLink = page.locator("#amend-case-type-link");
    this.changeCaseDetailsLink = page.locator("#amend-case-details-link");
    this.continueButton =
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
  }

  public void clickChangeCaseTypeLink() {
    changeCaseTypeLink.click();
  }

  public void clickChangeCaseDetailsLink() {
    changeCaseDetailsLink.click();
  }

  public void clickContinue() {
    continueButton.click();
  }
}
