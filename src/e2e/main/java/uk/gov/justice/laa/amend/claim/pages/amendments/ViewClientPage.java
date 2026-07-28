package uk.gov.justice.laa.amend.claim.pages.amendments;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ViewClientPage extends BaseAmendmentPage {

  private final Locator changeClientOneLine;
  private final Locator changeClientTwoLink;

  public ViewClientPage(Page page) {
    super(page);
    this.changeClientOneLine = page.locator("#amend-client-1");
    this.changeClientTwoLink = page.locator("#amend-client-2");
  }

  public void clickChangeClientOneLink() {
    changeClientOneLine.click();
  }

  public void clickChangeClientTwoLink() {
    changeClientTwoLink.click();
  }
}
