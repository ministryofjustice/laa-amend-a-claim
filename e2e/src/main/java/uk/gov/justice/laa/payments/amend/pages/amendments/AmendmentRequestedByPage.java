package uk.gov.justice.laa.payments.amend.pages.amendments;

import static com.microsoft.playwright.options.AriaRole.BUTTON;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Getter;
import uk.gov.justice.laa.payments.amend.pages.LaaPage;

@Getter
public class AmendmentRequestedByPage extends LaaPage {

  private final Locator providerRadio;
  private final Locator continueButton;

  public AmendmentRequestedByPage(Page page) {
    super(page, "Who requested the amendment?");

    providerRadio = page.getByLabel("Provider");
    continueButton = page.getByRole(BUTTON, new Page.GetByRoleOptions().setName("Continue"));
  }
}
