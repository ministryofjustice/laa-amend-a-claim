package uk.gov.justice.laa.payments.amend.pages.amendments;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ViewCasePage extends BaseAmendmentPage {

  private final Locator changeCaseTypeLink;
  private final Locator changeCaseDetailsLink;
  private final Locator continueButton;

  public ViewCasePage(Page page) {
    super(page);
    this.changeCaseTypeLink = page.locator("#amend-case-type-link");
    this.changeCaseDetailsLink = page.locator("#amend-case-details-link");
    this.continueButton = page.locator("#check");
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
