package uk.gov.justice.laa.amend.claim.tests;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.pages.*;
import uk.gov.justice.laa.amend.claim.utils.EnvConfig;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssessCostsValidationTest extends BaseTest {

        // ---------------- Crime data ----------------
    private static final String CRIME_PROVIDER_ACCOUNT = "2R223X";
    private static final String CRIME_UFN = "031222/002";
    private static final String CRIME_MONTH = "04";
    private static final String CRIME_YEAR = "2025";
    // ---------------- Civil data ----------------
    private static final String CIVIL_PROVIDER_ACCOUNT = "2N199K";
    private static final String CIVIL_UFN = "121019/001";
    private static final String CIVIL_MONTH = "06";
    private static final String CIVIL_YEAR = "2025";

    private void navigateToReviewAndAmend(String provider, String month, String year, String ufn) {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);
        search.searchForClaim(provider, month, year, ufn, "");
        search.clickViewForUfn(ufn);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();
        details.clickAddAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.selectAssessmentOutcome("assessed in full");
        outcome.clickContinue();

        assertTrue(page.url().contains("/review"));
    }

    private void assertNumberWith2DpErrorShown() {
        assertThat(page.locator(".govuk-error-summary")).isVisible();
        assertThat(page.locator(".govuk-error-message")).isVisible();
        assertThat(page.locator(".govuk-error-summary")).containsText("must be a number with up to 2 decimal places");
        assertThat(page.locator(".govuk-error-message")).containsText("must be a number with up to 2 decimal places");
    }


    @Test
    @DisplayName("Crime: Profit costs - letters cause number validation error")
    void crimeProfitCostsLettersShowsError() {
        navigateToReviewAndAmend(CRIME_PROVIDER_ACCOUNT, CRIME_MONTH, CRIME_YEAR, CRIME_UFN);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickChangeProfitCosts();

        AssessProfitCostsPage profit = new AssessProfitCostsPage(page);
        profit.waitForPage();
        profit.setAssessedValue("dasad");
        profit.saveChanges();

        assertNumberWith2DpErrorShown();
    }

    @Test
    @DisplayName("Crime: Disbursements - letters cause number validation error")
    void crimeDisbursementsLettersShowsError() {
        navigateToReviewAndAmend(CRIME_PROVIDER_ACCOUNT, CRIME_MONTH, CRIME_YEAR, CRIME_UFN);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickChangeDisbursements();

        AssessDisbursementsPage disb = new AssessDisbursementsPage(page);
        disb.waitForPage();
        disb.setAssessedValue("dasad");
        disb.saveChanges();

        assertNumberWith2DpErrorShown();
    }

    @Test
    @DisplayName("Crime: Disbursement VAT - letters cause number validation error")
    void crimeDisbursementVatLettersShowsError() {
        navigateToReviewAndAmend(CRIME_PROVIDER_ACCOUNT, CRIME_MONTH, CRIME_YEAR, CRIME_UFN);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickChangeDisbursementsVat();

        AssessDisbursementsVatPage disbVat = new AssessDisbursementsVatPage(page);
        disbVat.waitForPage();
        disbVat.setAssessedValue("dasad");
        disbVat.saveChanges();

        assertNumberWith2DpErrorShown();
    }

    @Test
    @DisplayName("Crime: Travel costs - letters cause number validation error")
    void crimeTravelCostsLettersShowsError() {
        navigateToReviewAndAmend(CRIME_PROVIDER_ACCOUNT, CRIME_MONTH, CRIME_YEAR, CRIME_UFN);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickChangeTravelCosts();

        AssessTravelCostsPage travel = new AssessTravelCostsPage(page);
        travel.waitForPage();
        travel.setAssessedValue("dasad");
        travel.saveChanges();

        assertNumberWith2DpErrorShown();
    }

    @Test
    @DisplayName("Crime: Waiting costs - letters cause number validation error")
    void crimeWaitingCostsLettersShowsError() {
        navigateToReviewAndAmend(CRIME_PROVIDER_ACCOUNT, CRIME_MONTH, CRIME_YEAR, CRIME_UFN);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickChangeWaitingCosts();

        AssessWaitingCostsPage waiting = new AssessWaitingCostsPage(page);
        waiting.waitForPage();
        waiting.setAssessedValue("dasad");
        waiting.saveChanges();

        assertNumberWith2DpErrorShown();
    }


    @Test
    @DisplayName("Civil: Detention travel and waiting costs - letters cause number validation error")
    void civilDetentionTravelWaitingLettersShowsError() {
        navigateToReviewAndAmend(CIVIL_PROVIDER_ACCOUNT, CIVIL_MONTH, CIVIL_YEAR, CIVIL_UFN);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickChangeDetentionTravelAndWaitingCosts();

        AssessDetentionTravelAndWaitingCostsPage detention = new AssessDetentionTravelAndWaitingCostsPage(page);
        detention.waitForPage();
        detention.setAssessedValue("dasad");
        detention.saveChanges();

        assertNumberWith2DpErrorShown();
    }

    @Test
    @DisplayName("Civil: JR and form filling - letters cause number validation error")
    void civilJrFormFillingLettersShowsError() {
        navigateToReviewAndAmend(CIVIL_PROVIDER_ACCOUNT, CIVIL_MONTH, CIVIL_YEAR, CIVIL_UFN);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickChangeJrAndFormFilling();

        AssessJrFormFillingCostsPage jr = new AssessJrFormFillingCostsPage(page);
        jr.waitForPage();
        jr.setAssessedValue("dasad");
        jr.saveChanges();

        assertNumberWith2DpErrorShown();
    }

    @Test
    @DisplayName("Civil: Counsel costs - letters cause number validation error")
    void civilCounselCostsLettersShowsError() {
        navigateToReviewAndAmend(CIVIL_PROVIDER_ACCOUNT, CIVIL_MONTH, CIVIL_YEAR, CIVIL_UFN);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickChangeCounselCosts();

        AssessCounselCostsPage counsel = new AssessCounselCostsPage(page);
        counsel.waitForPage();
        counsel.setAssessedValue("dasad");
        counsel.saveChanges();

        assertNumberWith2DpErrorShown();
    }
}