package uk.gov.justice.laa.payments.amend.pages.amendments;

import com.microsoft.playwright.Page;
import uk.gov.justice.laa.payments.amend.pages.LaaPage;

public class ConfirmationPage extends LaaPage {
  public ConfirmationPage(Page page) {
    super(page, "Amendments complete");
  }
}
