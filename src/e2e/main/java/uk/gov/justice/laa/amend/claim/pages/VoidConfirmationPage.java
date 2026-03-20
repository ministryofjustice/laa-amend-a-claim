package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class VoidConfirmationPage extends LaaPage {

    private final Locator voidClaimButton;

    public VoidConfirmationPage(Page page) {
        super(page, "Confirm you want to void this claim");
        this.voidClaimButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Void claim"));
    }

    public void clickVoidClaimButton() {
        voidClaimButton.click();
    }
}
