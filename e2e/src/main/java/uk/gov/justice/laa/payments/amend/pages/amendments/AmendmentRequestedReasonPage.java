package uk.gov.justice.laa.payments.amend.pages.amendments;

import static com.microsoft.playwright.options.AriaRole.BUTTON;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Getter;
import uk.gov.justice.laa.payments.amend.pages.LaaPage;

@Getter
public class AmendmentRequestedReasonPage extends LaaPage {

  private final Locator providerErrorRadio;
  private final Locator continueButton;

  public AmendmentRequestedReasonPage(Page page) {
    super(page, "Why was the amendment requested?");

    providerErrorRadio = page.getByLabel("Provider Error");
    continueButton = page.getByRole(BUTTON, new Page.GetByRoleOptions().setName("Continue"));
  }
}
