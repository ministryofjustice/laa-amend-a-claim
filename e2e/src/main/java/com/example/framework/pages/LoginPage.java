
package com.example.framework.pages;

import com.example.framework.utils.EnvConfig;
import com.microsoft.playwright.Page;

public class LoginPage {
    private final Page page;
    private final String userField = "#user-name";
    private final String passField = "#password";
    private final String loginButton = "#login-button";

    public LoginPage(Page page) { this.page = page; }

    public LoginPage navigate() { page.navigate(EnvConfig.baseUrl()); return this; }

    public void login() {
        page.fill(userField, EnvConfig.username());
        page.fill(passField, EnvConfig.password());
        page.click(loginButton);
        page.waitForSelector(".title:has-text('Products')");
    }
}
