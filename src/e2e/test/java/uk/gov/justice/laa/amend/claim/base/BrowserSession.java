package uk.gov.justice.laa.amend.claim.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.zaproxy.clientapi.core.ClientApi;
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
    private static ClientApi zap;
    private static boolean initialized = false;

    public static synchronized void initializeIfNeeded() {
        if (!initialized) {
            playwright = Playwright.create();
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(EnvConfig.headless());
            if (EnvConfig.zapEnabled()) {
                launchOptions.setProxy("http://localhost:8090");
                setupZap();
            }
            browser = playwright.chromium().launch(launchOptions);
            context = browser.newContext();

            if (EnvConfig.silasAuthenticationEnabled()) {
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
            } else {
                System.out.println("Running in local profile - skipping login");
                Page page = context.newPage();
                page.navigate(EnvConfig.baseUrl());
                page.close();
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

    public static ClientApi getZap() {
        return zap;
    }

    private static synchronized void setupZap() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(BrowserSession::generateZapReport));
            zap = new ClientApi("localhost", 8090);
            String[] urlsToExclude = {
                "http://clients2\\.google\\.com.*",
                "https://aadcdn\\.msauth\\.net.*",
                "https://dev\\.your\\-legal\\-aid\\-services\\.service\\.justice\\.gov\\.uk.*",
                "https://fonts\\.googleapis\\.com.*",
                "https://fonts\\.gstatic\\.com.*",
                "https://www\\.googletagmanager\\.com.*",
                "https://login\\.live\\.com.*",
                "https://www\\.nationalarchives\\.gov\\.uk.*"
            };
            for (String regex : urlsToExclude) {
                zap.ascan.excludeFromScan(regex);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static synchronized void generateZapReport() {
        try {
            Path path = Paths.get("build/zap-reports/zap.html");
            Files.createDirectories(path.getParent());
            Files.write(path, zap.core.htmlreport());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static synchronized void cleanup() {
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
