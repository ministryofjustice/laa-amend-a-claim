package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class AssessmentOutcomePage {

    private final Page page;

    private final Locator heading;
    private final Locator continueButton;

    private final Locator assessedInFullRadio;
    private final Locator reducedStillEscapedRadio;
    private final Locator reducedToFixedFeeRadio;
    private final Locator nilledRadio;

    private final Locator vatYesRadio;
    private final Locator vatNoRadio;

    public AssessmentOutcomePage(Page page) {
        this.page = page;

        this.heading = page.getByRole(AriaRole.HEADING, 
                new Page.GetByRoleOptions().setName("Assessment outcome"));

        this.assessedInFullRadio =
                page.getByLabel("Assessed in full", new Page.GetByLabelOptions().setExact(true));
        this.reducedStillEscapedRadio =
                page.getByLabel("Reduced (still escaped)", new Page.GetByLabelOptions().setExact(true));
        this.reducedToFixedFeeRadio =
                page.getByLabel("Reduced to fixed fee (assessed)", new Page.GetByLabelOptions().setExact(true));
        this.nilledRadio =
                page.getByLabel("Nilled", new Page.GetByLabelOptions().setExact(true));

        this.vatYesRadio =
                page.getByLabel("Yes", new Page.GetByLabelOptions().setExact(true));
        this.vatNoRadio =
                page.getByLabel("No", new Page.GetByLabelOptions().setExact(true));

        this.continueButton =
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
    }

    public void waitForPage() {
        heading.waitFor();
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