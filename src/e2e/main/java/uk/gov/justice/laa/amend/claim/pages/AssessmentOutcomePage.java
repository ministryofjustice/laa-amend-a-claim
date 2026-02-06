package uk.gov.justice.laa.amend.claim.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class AssessmentOutcomePage extends LaaInputPage {

    private final Locator assessedInFullRadio;
    private final Locator reducedStillEscapedRadio;
    private final Locator reducedToFixedFeeRadio;
    private final Locator nilledRadio;

    private final Locator vatGroup;
    private final Locator vatLegend;
    private final Locator vatYesRadio;
    private final Locator vatNoRadio;

    private final Locator errorSummaryTitle;
    private final Locator errorSummaryLink;

    public AssessmentOutcomePage(Page page) {
        super(page, "Assessment Outcome");

        this.assessedInFullRadio = page.getByLabel("Assessed in full", new Page.GetByLabelOptions().setExact(true));
        this.reducedStillEscapedRadio =
                page.getByLabel("Reduced (still escaped)", new Page.GetByLabelOptions().setExact(true));
        this.reducedToFixedFeeRadio =
                page.getByLabel("Reduced to fixed fee (assessed)", new Page.GetByLabelOptions().setExact(true));
        this.nilledRadio = page.getByLabel("Nilled", new Page.GetByLabelOptions().setExact(true));

        this.vatGroup =
                page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName("Is this claim liable for VAT?"));
        this.vatLegend = page.getByText("Is this claim liable for VAT?");
        this.vatYesRadio = vatGroup.getByLabel("Yes", new Locator.GetByLabelOptions().setExact(true));
        this.vatNoRadio = vatGroup.getByLabel("No", new Locator.GetByLabelOptions().setExact(true));

        this.saveButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));

        this.errorSummaryTitle = page.locator(".govuk-error-summary__title");
        this.errorSummaryLink = page.locator(".govuk-error-summary__list a");
    }

    public void assertPageLoaded() {
        assertThat(assessedInFullRadio).isVisible();
        assertThat(reducedStillEscapedRadio).isVisible();
        assertThat(reducedToFixedFeeRadio).isVisible();
        assertThat(nilledRadio).isVisible();

        assertThat(vatLegend).isVisible();
        assertThat(vatYesRadio).isVisible();
        assertThat(vatNoRadio).isVisible();

        assertThat(saveButton).isVisible();
    }

    public void assertAssessmentOutcomeRequiredError() {
        waitForPageErrors();

        assertThat(errorSummaryTitle).containsText("There is a problem");
        assertThat(errorSummaryLink).containsText("Select the assessment outcome");
        assertThat(inlineErrors).containsText("Select the assessment outcome");
    }

    public void selectAssessmentOutcome(String outcome) {
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

    public void completeAssessment(String outcome, boolean vat) {
        selectAssessmentOutcome(outcome);
        selectVatLiable(vat);
        saveChanges();
    }
}
