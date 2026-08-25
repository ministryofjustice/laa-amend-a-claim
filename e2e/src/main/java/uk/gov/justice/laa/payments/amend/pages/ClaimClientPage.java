package uk.gov.justice.laa.payments.amend.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ClaimClientPage extends LaaPage {

  private final Locator caseLink;

  public ClaimClientPage(Page page) {
    super(page, "Client");
    this.caseLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Case"));
  }

  public void clickCaseItem() {
    caseLink.click();
  }
}
