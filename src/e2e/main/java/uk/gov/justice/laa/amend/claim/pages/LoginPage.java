package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Page;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.utils.OneTimePasswordUtils;

public class LoginPage {
    private final Page page;

    private final String userField = "input[name='loginfmt'], input[type='email']";
    private final String nextButton =
            "input[type='submit'], button[type='submit'], button:has-text('Next'), button:has-text('Continue'),"
                    + " button:has-text('Sign in')";
    private final String passwdField = "input[name='passwd']";
    private final String signInButton = "input[type='submit'], button:has-text('Sign in')";
    private final String otcInput = "input[name='otc']";
    private final String verifyButton = "input[id='idSubmit_SAOTCC_Continue']";

    public LoginPage(Page page) {
        this.page = page;
    }

    public LoginPage navigate() {
        String url = EnvConfig.baseUrl();
        System.out.println("[INFO] Navigating to: " + url);
        page.navigate(url, new Page.NavigateOptions().setTimeout(120_000));
        return this;
    }

    public void login() {
        System.out.println("[INFO] Starting login flow...");

        page.waitForSelector(userField, new Page.WaitForSelectorOptions().setTimeout(120_000));
        page.fill(userField, EnvConfig.username());
        page.click(nextButton);

        page.waitForSelector(passwdField, new Page.WaitForSelectorOptions().setTimeout(120_000));
        page.fill(passwdField, EnvConfig.password());
        page.click(signInButton);

        String mfaSecret = EnvConfig.mfaSecret();
        if (mfaSecret != null && !mfaSecret.isBlank()) {
            handleMfa(mfaSecret);
        }

        page.waitForSelector("h1:has-text('Search for a claim')", new Page.WaitForSelectorOptions().setTimeout(60_000));

        System.out.println("[INFO] Login successful!");
    }

    private void handleMfa(String secret) {
        System.out.println("[INFO] Performing MFA step...");
        page.waitForSelector(otcInput, new Page.WaitForSelectorOptions().setTimeout(120_000));

        // MFA secret is securely loaded from the environment via EnvConfig
        String code = OneTimePasswordUtils.generateTOTP(secret);
        System.out.println("[INFO] Generated MFA code securely.");

        page.fill(otcInput, code);
        page.click(verifyButton);

        page.waitForSelector("h1:has-text('Search for a claim')", new Page.WaitForSelectorOptions().setTimeout(60_000));
    }
}
