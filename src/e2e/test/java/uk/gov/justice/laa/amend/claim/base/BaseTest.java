package uk.gov.justice.laa.amend.claim.base;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.persistence.DatabaseQueryExecutor;

/**
 * The {@code BaseTest} class provides a foundation for UI tests using the
 * Playwright library. It handles common setup and cleanup tasks, such as
 * initializing a new browser page and navigating to the application's base URL
 * before each test, and ensuring the page is closed after the test is executed.
 *
 * Subclasses of {@code BaseTest} inherit this functionality to streamline test
 * development, focusing only on the test logic rather than boilerplate setup
 * and teardown.
 *
 * This class is abstract and not meant to be instantiated directly. It is
 * designed to be extended by specific test classes that implement particular
 * test scenarios.
 *
 * Features:
 * - Automatically initializes a browser page and navigates to the base URL before each test.
 * - Safely closes the browser page after each test execution.
 * - Leverages the {@code BrowserSession} and {@code EnvConfig} classes for
 *   session management and configuration.
 *
 * Structure:
 * - {@code setup()}: A method annotated with {@code @BeforeEach} to set up the testing
 *   environment. It creates a new browser page and navigates to the configured
 *   base URL.
 * - {@code tearDown()}: A method annotated with {@code @AfterEach} to ensure that
 *   resources are properly released by closing the browser page after the test
 *   completes.
 */
public abstract class BaseTest {

    protected DatabaseQueryExecutor dqe;
    protected Page page;

    public final String BULK_SUBMISSION_ID = UUID.randomUUID().toString();
    public final String USER_ID = EnvConfig.userId();

    protected BrowserContext browserContext;
    protected abstract List<Insert> inserts();

    @BeforeEach
    public void setup() {
        try {
            dqe = new DatabaseQueryExecutor();
            dqe.clean();
            dqe.seed(inserts());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed database", e);
        }

        browserContext = BrowserSession.getContext();
        page = browserContext.newPage();
        page.navigate(EnvConfig.baseUrl());
    }

    @AfterEach
    public void tearDown() {
        if (page != null) {
            try {
                page.close();
            } catch (Exception ignored) {
            }
        }
    }
}
