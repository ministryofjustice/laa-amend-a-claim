package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AssessmentOutcomePage {

    private final Page page;

    private final Locator heading;
    private final Locator continueButton;

    private final Locator assessedInFullRadio;
    private final Locator reducedStillEscapedRadio;
    private final Locator reducedToFixedFeeRadio;
    private final Locator nilledRadio;

    private final Locator vatGroup;
    private final Locator vatLegend;
    private final Locator vatYesRadio;
    private final Locator vatNoRadio;

    private final Locator errorSummary;
    private final Locator errorSummaryTitle;
    private final Locator errorSummaryLink;
    private final Locator inlineErrorMessage;

    public AssessmentOutcomePage(Page page) {
        this.page = page;

        this.heading = page.getByRole(
                AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Assessment outcome")
        );

        this.assessedInFullRadio =
                page.getByLabel("Assessed in full", new Page.GetByLabelOptions().setExact(true));
        this.reducedStillEscapedRadio =
                page.getByLabel("Reduced (still escaped)", new Page.GetByLabelOptions().setExact(true));
        this.reducedToFixedFeeRadio =
                page.getByLabel("Reduced to fixed fee (assessed)", new Page.GetByLabelOptions().setExact(true));
        this.nilledRadio =
                page.getByLabel("Nilled", new Page.GetByLabelOptions().setExact(true));

        this.vatGroup = page.getByRole(
                AriaRole.GROUP,
                new Page.GetByRoleOptions().setName("Is this claim liable for VAT?")
        );
        this.vatLegend = page.getByText("Is this claim liable for VAT?");
        this.vatYesRadio = vatGroup.getByLabel("Yes", new Locator.GetByLabelOptions().setExact(true));
        this.vatNoRadio  = vatGroup.getByLabel("No", new Locator.GetByLabelOptions().setExact(true));

        this.continueButton =
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));

        this.errorSummary = page.locator(".govuk-error-summary");
        this.errorSummaryTitle = page.locator(".govuk-error-summary__title");
        this.errorSummaryLink = page.locator(".govuk-error-summary__list a");
        this.inlineErrorMessage = page.locator(".govuk-error-message");
    }

    public void waitForPage() {
        heading.waitFor();
    }

    public void assertPageLoaded() {
        waitForPage();

        assertThat(heading).isVisible();

        assertThat(assessedInFullRadio).isVisible();
        assertThat(reducedStillEscapedRadio).isVisible();
        assertThat(reducedToFixedFeeRadio).isVisible();
        assertThat(nilledRadio).isVisible();

        assertThat(vatLegend).isVisible();
        assertThat(vatYesRadio).isVisible();
        assertThat(vatNoRadio).isVisible();

        assertThat(continueButton).isVisible();
    }

    public void assertAssessmentOutcomeRequiredError() {
        assertThat(errorSummary).isVisible();
        assertThat(errorSummaryTitle).containsText("There is a problem");
        assertThat(errorSummaryLink).containsText("Select the assessment outcome");
        assertThat(inlineErrorMessage).containsText("Select the assessment outcome");
    }


    public void selectAssessmentOutcome(String outcome) {
        waitForPage();
        switch (outcome.toLowerCase()) {
            case "assessed in full":
            case "paid-in-full":
                assessedInFullRadio.check();
                break;

            case "reduced (still escaped)":
            case "reduced-still-escaped":
                reducedStillEscapedRadio.check();
                break;

            case "reduced to fixed fee (assessed)":
            case "reduced-to-fixed-fee-assessed":
                reducedToFixedFeeRadio.check();
                break;

            case "nilled":
                nilledRadio.check();
                break;

            default:
                throw new IllegalArgumentException("Unknown assessment outcome: " + outcome);
        }
    }

    public void selectVatLiable(boolean isLiable) {
        if (isLiable) {
            vatYesRadio.check();
        } else {
            vatNoRadio.check();
        }
    }

    public void clickContinue() {
        continueButton.click();
    }

    public void completeAssessment(String outcome, boolean vat) {
        selectAssessmentOutcome(outcome);
        selectVatLiable(vat);
        clickContinue();
    }


    public String getHeadingText() {
        return heading.textContent().trim();
    }
}