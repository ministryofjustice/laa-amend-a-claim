package uk.gov.justice.laa.amend.claim.pages.amendments;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ViewCasePage extends BaseAmendmentPage {

  private final Locator changeCaseTypeLink;
  private final Locator changeCaseDetailsLink;

  public ViewCasePage(Page page) {
    super(page);
    this.changeCaseTypeLink = page.locator("#amend-case-type-link");
    this.changeCaseDetailsLink = page.locator("#amend-case-details-link");
  }

  public void clickChangeCaseTypeLink() {
    changeCaseTypeLink.click();
  }

  public void clickChangeCaseDetailsLink() {
    changeCaseDetailsLink.click();
  }
}
