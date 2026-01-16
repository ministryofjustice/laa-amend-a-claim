package uk.gov.justice.laa.amend.claim.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.pages.AssessCounselCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessDetentionTravelAndWaitingCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessDisbursementsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessDisbursementsVatPage;
import uk.gov.justice.laa.amend.claim.pages.AssessJrFormFillingCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessProfitCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessWaitingCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssessCostsValidationTest extends BaseTest {

        // ---------------- Crime data ----------------
    private String CRIME_PROVIDER_ACCOUNT;
    private String CRIME_UFN;
    private String CRIME_MONTH;
    private String CRIME_YEAR;
    // ---------------- Civil data ----------------
    private String CIVIL_PROVIDER_ACCOUNT;
    private String CIVIL_UFN;
    private String CIVIL_MONTH;
    private String CIVIL_YEAR;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();

        this.CRIME_PROVIDER_ACCOUNT = "123456";
        this.CRIME_UFN = dqe.getUfn();
        this.CRIME_MONTH = "04";
        this.CRIME_YEAR = "2025";

        this.CIVIL_PROVIDER_ACCOUNT = "234567";
        this.CIVIL_UFN = dqe.getUfn();
        this.CIVIL_MONTH = "06";
        this.CIVIL_YEAR = "2025";
    }

    private void navigateToReviewAndAmend(String provider, String month, String year, String ufn) {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);
        search.searchForClaim(provider, month, year, ufn, "");
        search.clickViewForUfn(ufn);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();
        details.clickAddUpdateAssessmentOutcome();

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