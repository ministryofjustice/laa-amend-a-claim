package uk.gov.justice.laa.amend.claim.pages;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.playwright.Reporter;
import com.deque.html.axecore.results.AxeResults;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.io.IOException;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class LaaPage {

    protected Page page;
    protected Locator heading;
    protected AxeBuilder axeBuilder;

    public LaaPage(Page page, String heading) {
        this.page = page;

        this.heading = page.getByRole(
            AriaRole.HEADING,
            new Page.GetByRoleOptions().setName(heading)
        );

        this.axeBuilder = new AxeBuilder(page)
            .exclude(".govuk-phase-banner")
            .exclude(".govuk-back-link");

        waitForPage();
    }

    private void waitForPage() {
        heading.waitFor();
        assertThat(heading).isVisible();
        try {
            AxeResults axeResults = axeBuilder.analyze();
            axeResults.getViolations().forEach(System.out::println);
            assertTrue(axeResults.violationFree());
            Reporter reporter = new Reporter();
            reporter.JSONStringify(axeResults, "AxeResults.json");
        } catch (RuntimeException | IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
