<<<<<<< HEAD
package uk.gov.justice.laa.amend.claim.base;


import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import uk.gov.justice.laa.amend.claim.pages.LoginPage;
import uk.gov.justice.laa.amend.claim.utils.EnvConfig;
=======
package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
>>>>>>> 43d9a35 (E2E Journey)

public abstract class BaseTest {

    protected static Playwright playwright;
    protected static Browser browser;
<<<<<<< HEAD
    protected static BrowserContext context;

    protected Page page;


    @BeforeAll
    static void launchBrowserAndLogin() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(EnvConfig.headless()));
        context = browser.newContext();
        Page loginPage = context.newPage();
        new LoginPage(loginPage).navigate().login();
        loginPage.close(); // Close the login page, but context keeps the session
    }

    @AfterAll
    static void closeBrowser() {
        if (context != null) context.close();
=======

    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(System.getProperty("headless", "true"))));
    }

    @AfterAll
    static void closeBrowser() {
>>>>>>> 43d9a35 (E2E Journey)
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
<<<<<<< HEAD
    void createPage() {
=======
    void createContext() {
        context = browser.newContext();
>>>>>>> 43d9a35 (E2E Journey)
        page = context.newPage();
    }

    @AfterEach
    void cleanUp() {
<<<<<<< HEAD
        if (page != null) {
            try {
                page.close();
=======
        if (context != null) {
            try {
                context.close();
>>>>>>> 43d9a35 (E2E Journey)
            } catch (Exception ignored) {}
        }
    }
}