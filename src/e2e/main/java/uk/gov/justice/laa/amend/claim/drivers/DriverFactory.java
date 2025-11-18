
package uk.gov.justice.laa.amend.claim.drivers;

import uk.gov.justice.laa.amend.claim.utils.EnvConfig;
import com.microsoft.playwright.*;

public class DriverFactory {
    private static Playwright playwright;
    private static Browser browser;

    private static void ensureStarted() {
        if (playwright == null) {
            playwright = Playwright.create();
        }
    }

    public static BrowserContext createContext() {
        ensureStarted();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(EnvConfig.headless());
        String type = EnvConfig.browser().toLowerCase();
        switch (type) {
            case "firefox":
                browser = playwright.firefox().launch(options);
                break;
            case "webkit":
                browser = playwright.webkit().launch(options);
                break;
            default:
                browser = playwright.chromium().launch(options);
        }
        return browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 800));
    }

    public static void close() {
        try {
            if (browser != null) browser.close();
        } finally {
            if (playwright != null) playwright.close();
        }
    }
}
