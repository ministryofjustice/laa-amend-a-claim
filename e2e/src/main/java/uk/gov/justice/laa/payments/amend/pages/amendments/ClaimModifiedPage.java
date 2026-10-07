package uk.gov.justice.laa.payments.amend.pages.amendments;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import uk.gov.justice.laa.payments.amend.pages.LaaPage;

public class ClaimModifiedPage extends LaaPage {

  private final Locator searchResultsLink;

  public ClaimModifiedPage(Page page) {
    super(page, "Amendments cannot be submitted");
    this.searchResultsLink = page.locator("#back-to-search");
  }

  public void assertClaimModifiedMessage() {
    assertThat(page.getByText("This claim has been modified by another user.")).isVisible();
  }

  public void clickSearchResults() {
    searchResultsLink.click();
  }
}
