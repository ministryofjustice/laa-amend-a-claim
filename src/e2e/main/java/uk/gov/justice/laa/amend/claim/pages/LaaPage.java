package uk.gov.justice.laa.amend.claim.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.playwright.Reporter;
import com.deque.html.axecore.results.AxeResults;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;

public abstract class LaaPage {

    protected final Page page;
    protected final Locator heading;
    protected final AxeBuilder axeBuilder;
    private final String directory;

    public LaaPage(Page page, String heading) {
        this.page = page;

        this.heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(heading));

        this.axeBuilder = new AxeBuilder(page).exclude(".govuk-phase-banner").exclude(".govuk-back-link");

        this.directory = EnvConfig.axeReportsDirectory();

        new File(directory).mkdirs();

        waitForPage();
    }

    private void waitForPage() {
        heading.waitFor();
        assertThat(heading).isVisible();
        generateAxeReport(200);
    }

    protected void generateErrorSummaryAxeReport() {
        generateAxeReport(400);
    }

    private void generateAxeReport(int status) {
        if (EnvConfig.axeEnabled()) {
            try {
                String fileName = heading.textContent().trim().replace(" ", "_");
                String path = String.format("%s/%s_%d.json", directory, fileName, status);
                if (Files.notExists(Paths.get(path))) {
                    AxeResults axeResults = axeBuilder.analyze();
                    Reporter reporter = new Reporter();
                    reporter.JSONStringify(axeResults, path);
                    assertTrue(axeResults.violationFree());
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
