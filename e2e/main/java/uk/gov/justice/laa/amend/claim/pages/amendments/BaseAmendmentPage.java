package uk.gov.justice.laa.amend.claim.pages.amendments;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import uk.gov.justice.laa.amend.claim.pages.LaaPage;

public class BaseAmendmentPage extends LaaPage {

  private final Locator clientTabLink;
  private final Locator caseTabLink;
  private final Locator costsTabLink;

  public BaseAmendmentPage(Page page) {
    super(page, "Amend claim details");
    clientTabLink =
        page.locator(".moj-sub-navigation__link")
            .filter(new Locator.FilterOptions().setHasText("Client"));
    caseTabLink =
        page.locator(".moj-sub-navigation__link")
            .filter(new Locator.FilterOptions().setHasText("Case"));
    costsTabLink =
        page.locator(".moj-sub-navigation__link")
            .filter(new Locator.FilterOptions().setHasText("Costs"));
  }

  public void clickClientTab() {
    this.clientTabLink.click();
  }

  public void clickCaseTab() {
    this.caseTabLink.click();
  }

  public void clickCostsTab() {
    this.costsTabLink.click();
  }
}
