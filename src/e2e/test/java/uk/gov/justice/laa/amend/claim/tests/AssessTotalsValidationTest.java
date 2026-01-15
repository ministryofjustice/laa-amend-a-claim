package uk.gov.justice.laa.amend.claim.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.pages.AssessAllowedTotalsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessTotalClaimValuePage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssessTotalsValidationTest extends BaseTest {

    private static final String PROVIDER_ACCOUNT = "2N199K";
    private static final String UFN = "121019/001";
    private static final String MONTH = "06";
    private static final String YEAR = "2025";

    private void navigateToReviewAndAmend() {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);
        search.searchForClaim(PROVIDER_ACCOUNT, MONTH, YEAR, UFN, "");
        search.clickViewForUfn(UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();
        details.clickAddUpdateAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.selectAssessmentOutcome("reduced-to-fixed-fee-assessed");
        outcome.clickContinue();

        assertTrue(page.url().contains("/review"));
    }

    @Test
    @DisplayName("Assess total claim value: submit blank shows required errors")
    void assessedTotalsBlankShowsRequiredErrors() {
        navigateToReviewAndAmend();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickAddAssessedTotalVat();

        AssessTotalClaimValuePage totals = new AssessTotalClaimValuePage(page);
        totals.waitForPage();
        totals.saveChanges();

        totals.assertRequiredErrorsShown();
    }

    @Test
    @DisplayName("Assess total claim value: alphabetic input shows numeric errors")
    void assessedTotalsAlphaShowsNumericErrors() {
        navigateToReviewAndAmend();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickAddAssessedTotalVat();

        AssessTotalClaimValuePage totals = new AssessTotalClaimValuePage(page);
        totals.waitForPage();
        totals.setAssessedTotalVat("dasad");
        totals.setAssessedTotalInclVat("dasad");
        totals.saveChanges();

        totals.assertNumericErrorsShown();
    }

    @Test
    @DisplayName("Assess total allowed value: submit blank shows required errors")
    void allowedTotalsBlankShowsRequiredErrors() {
        navigateToReviewAndAmend();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickAddAllowedTotalVat();

        AssessAllowedTotalsPage allowed = new AssessAllowedTotalsPage(page);
        allowed.waitForPage();
        allowed.saveChanges();

        allowed.assertRequiredErrorsShown();
    }

    @Test
    @DisplayName("Assess total allowed value: alphabetic input shows numeric errors")
    void allowedTotalsAlphaShowsNumericErrors() {
        navigateToReviewAndAmend();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.clickAddAllowedTotalVat();

        AssessAllowedTotalsPage allowed = new AssessAllowedTotalsPage(page);
        allowed.waitForPage();
        allowed.setAllowedTotalVat("dasad");
        allowed.setAllowedTotalInclVat("dasad");
        allowed.saveChanges();

        allowed.assertNumericErrorsShown();
    }

    @Test
    @DisplayName("Assess total allowed value: allowed totals are correct")
    void allowedTotalsAreCorrect() {
        navigateToReviewAndAmend();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();

        review.assertAllowedTotalsAreCorrect("£0.00", "£132.00");
    }
}