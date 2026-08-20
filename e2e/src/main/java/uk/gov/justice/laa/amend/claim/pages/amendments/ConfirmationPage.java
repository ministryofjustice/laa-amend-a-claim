package uk.gov.justice.laa.amend.claim.pages.amendments;

import com.microsoft.playwright.Page;
import uk.gov.justice.laa.amend.claim.pages.LaaPage;

public class ConfirmationPage extends LaaPage {
  public ConfirmationPage(Page page) {
    super(page, "Amendments complete");
  }
}
