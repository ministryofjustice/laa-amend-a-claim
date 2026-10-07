package uk.gov.justice.laa.payments.amend.pages.amendments;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import uk.gov.justice.laa.payments.amend.pages.LaaPage;

public class CannotBeSubmittedPage extends LaaPage {

  private final Locator errorRows;
  private final Locator amendClaimButton;
  private final Locator cancelAmendmentsLink;

  public CannotBeSubmittedPage(Page page) {
    super(page, "Your amendments cannot be submitted");
    this.errorRows = page.locator("#amendment-errors tbody tr");
    this.amendClaimButton = page.locator("#amend-claim");
    this.cancelAmendmentsLink = page.locator("#cancel-amendments");
  }

  public void assertErrorRow(String item, String issue) {
    var row = errorRows.filter(new Locator.FilterOptions().setHasText(issue));
    assertThat(row.locator("th")).hasText(item);
  }

  public void clickAmendClaim() {
    amendClaimButton.click();
  }

  public void clickCancelAmendments() {
    cancelAmendmentsLink.click();
  }
}
