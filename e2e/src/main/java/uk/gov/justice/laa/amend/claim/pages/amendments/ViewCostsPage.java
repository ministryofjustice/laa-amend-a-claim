package uk.gov.justice.laa.amend.claim.pages.amendments;

import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.assertSummaryListValue;
import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.cardByTitle;
import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.summaryListRowByLabel;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ViewCostsPage extends BaseAmendmentPage {

  private static final String LIST_OF_COSTS = "List of costs";
  private static final int AMENDED_COLUMN_INDEX = 2;

  private final Locator changeCostsLink;
  private final Locator continueButton;

  public ViewCostsPage(Page page) {
    super(page);
    this.changeCostsLink = page.locator("#amend-costs-link");
    this.continueButton = page.locator("#check");
  }

  public void clickChangeCostsLink() {
    changeCostsLink.click();
  }

  public void clickContinue() {
    continueButton.click();
  }

  public void assertAmendedCost(String label, String amendedValue) {
    Locator row = summaryListRowByLabel(cardByTitle(LIST_OF_COSTS, page), label);
    assertSummaryListValue(row, AMENDED_COLUMN_INDEX, amendedValue);
  }
}
