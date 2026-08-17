package uk.gov.justice.laa.amend.claim.pages.amendments;

import static com.microsoft.playwright.options.AriaRole.BUTTON;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.pages.LaaPage;

@Getter
public class AmendmentRequestByPage extends LaaPage {

  private final Locator providerRadio;
  private final Locator continueButton;

  public AmendmentRequestByPage(Page page) {
    super(page, "Who requested the amendment?");

    providerRadio = page.getByLabel("Provider");
    continueButton = page.getByRole(BUTTON, new Page.GetByRoleOptions().setName("Continue"));
  }
}
