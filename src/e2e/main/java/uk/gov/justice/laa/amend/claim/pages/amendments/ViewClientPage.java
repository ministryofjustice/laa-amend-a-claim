package uk.gov.justice.laa.amend.claim.pages.amendments;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Getter;

@Getter
public class ViewClientPage extends BaseAmendmentPage {

  private final Locator changeClientOneLink;
  private final Locator changeClientTwoLink;

  public ViewClientPage(Page page) {
    super(page);
    this.changeClientOneLink = page.locator("#amend-client-1");
    this.changeClientTwoLink = page.locator("#amend-client-2");
  }
}
