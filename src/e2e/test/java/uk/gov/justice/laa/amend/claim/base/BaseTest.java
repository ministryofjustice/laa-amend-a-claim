
package base;

import uk.gov.justice.laa.amend.claim.drivers.DriverFactory;
import uk.gov.justice.laa.amend.claim.pages.LoginPage;
import uk.gov.justice.laa.amend.claim.utils.EnvConfig;
import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.ByteArrayInputStream;

public abstract class BaseTest {
    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    public void setUp() {
        context = DriverFactory.createContext();
        page = context.newPage();

        String baseUrl = EnvConfig.baseUrl();
//        if (baseUrl.contains("localhost") || baseUrl.contains("127.0.0.1")) {
//            System.out.println("[INFO] Local environment detected. Skipping login steps.");
//            page.navigate(baseUrl);
//        } else {
            System.out.println("[INFO] Non-local environment detected. Running login steps.");
            new LoginPage(page).navigate().login();

    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        try {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Allure.addAttachment(testInfo.getDisplayName() + " - Screenshot", "image/png",
                    new ByteArrayInputStream(screenshot), "png");
        } catch (Throwable t) {
            System.out.println("[WARN] Could not capture screenshot: " + t.getMessage());
        }
        if (context != null) context.close();
        DriverFactory.close();
    }
}
