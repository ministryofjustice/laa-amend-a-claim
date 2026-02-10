package uk.gov.justice.laa.amend.claim.tests;

import com.microsoft.playwright.options.Cookie;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

import java.util.List;

import static io.restassured.http.Cookie.DOMAIN;

public class MaintenancePageTest extends BaseTest {

    @Override
    protected List<Insert> inserts() {
        return List.of();
    }

    @Test
    public void maintenancePageTest() throws InterruptedException {

        Cookie cookie = new Cookie("bypass", "password");

        cookie.setPath("/");
        cookie.setDomain(DOMAIN);

        List<Cookie> cookies = List.of(cookie);

        browserContext.addCookies(cookies);

        SearchPage search = new SearchPage(page).navigateTo(EnvConfig.baseUrl());

        Thread.sleep(100000000);



    }





}
