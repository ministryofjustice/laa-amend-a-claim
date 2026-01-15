package uk.gov.justice.laa.amend.claim.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReviewAndAmendTest extends BaseTest {

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
    private void navigateToReviewAndAmend(
            String providerAccount,
            String month,
            String year,
            String ufn
    ) {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);
        search.searchForClaim(providerAccount, month, year, ufn, "");
        search.clickViewForUfn(ufn);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();
        details.clickAddUpdateAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();

        // Minimal inputs to proceed
        outcome.selectAssessmentOutcome("assessed in full");

        // VAT is defaulted on your HTML (often "No" checked), and not needed for these tests.
        // If your app requires VAT explicitly, uncomment one line:
        // outcome.selectVatLiable(false);

        outcome.clickContinue();
    }

    @Test
    @DisplayName("Review & amend (Crime) loads correctly – headers + claim cost items")
    void crimeReviewAndAmendLoads() {
        navigateToReviewAndAmend(
                CRIME_PROVIDER_ACCOUNT,
                CRIME_MONTH,
                CRIME_YEAR,
                CRIME_UFN
        );

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();

        assertTrue(page.url().contains("/review"));
        review.assertCrimePageLoadedHeadersAndItems();
    }

    @Test
    @DisplayName("Review & amend (Civil) loads correctly – headers + claim cost items")
    void civilReviewAndAmendLoads() {
        navigateToReviewAndAmend(
                CIVIL_PROVIDER_ACCOUNT,
                CIVIL_MONTH,
                CIVIL_YEAR,
                CIVIL_UFN
        );

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();

        assertTrue(page.url().contains("/review"));
        review.assertCivilPageLoadedHeadersAndItems();
    }

    @Test
    @DisplayName("Review & amend (Crime) submit without totals shows GOV.UK error summary")
    void crimeSubmitWithoutTotalsShowsErrors() {
        navigateToReviewAndAmend(
                CRIME_PROVIDER_ACCOUNT,
                CRIME_MONTH,
                CRIME_YEAR,
                CRIME_UFN
        );

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();

        review.submitAdjustments();

        assertTrue(page.url().contains("/review"));
        review.assertSubmitTotalsRequiredErrors();
    }
}