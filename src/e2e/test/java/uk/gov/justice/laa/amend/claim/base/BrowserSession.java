package uk.gov.justice.laa.amend.claim.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitForSelectorState;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.pages.LoginPage;

/**
 * The {@code BrowserSession} class provides a managed browser session for
 * automated UI tests using the Playwright library. It handles browser
 * initialization, login, and cleanup within a reusable browser context.
 */
public class BrowserSession {
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static boolean initialized = false;

    private static boolean isSilasAuthenticationDisabled() {
        return EnvConfig.silasAuthenticationEnabled().equals("false");
    }

    public static synchronized void initializeIfNeeded() {
        if (!initialized) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(EnvConfig.headless()));
            context = browser.newContext();

            if (isSilasAuthenticationDisabled()) {
                System.out.println("Running in local profile - skipping login");
                Page page = context.newPage();
                page.navigate(EnvConfig.baseUrl());
                page.close();
            } else {
                // Single login for all tests
                Page loginPage = context.newPage();
                LoginPage login = new LoginPage(loginPage);
                login.navigate();
                try {
                    login.login();
                    loginPage.waitForSelector(
                            "h1:has-text('Search for a claim')",
                            new Page.WaitForSelectorOptions()
                                    .setTimeout(60_000)
                                    .setState(WaitForSelectorState.VISIBLE));
                } finally {
                    loginPage.close();
                }
            }

            initialized = true;

            // Register shutdown hook to ensure cleanup
            Runtime.getRuntime().addShutdownHook(new Thread(BrowserSession::cleanup));
        }
    }

    public static BrowserContext getContext() {
        initializeIfNeeded();
        return context;
    }

    public static synchronized void cleanup() {
        if (initialized) {
            try {
                // Sign out
                Page signoutPage = context.newPage();
                signoutPage.navigate(EnvConfig.baseUrl());
                signoutPage
                        .locator(".moj-header__navigation-link:has-text('Sign out')")
                        .click();
                signoutPage.waitForLoadState();

            } finally {
                if (context != null) {
                    context.close();
                }
                if (browser != null) {
                    browser.close();
                }
                if (playwright != null) {
                    playwright.close();
                }
                initialized = false;
            }
        }
    }
}
