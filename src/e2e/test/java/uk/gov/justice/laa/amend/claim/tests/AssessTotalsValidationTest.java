package uk.gov.justice.laa.amend.claim.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.models.BulkSubmissionInsert;
import uk.gov.justice.laa.amend.claim.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.models.SubmissionInsert;
import uk.gov.justice.laa.amend.claim.pages.AssessAllowedTotalsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessTotalClaimValuePage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

public class AssessTotalsValidationTest extends BaseTest {

    private final String PROVIDER_ACCOUNT = "234567";
    private final String UFN = generateUfn();
    private final String MONTH = "06";
    private final String YEAR = "2025";
    private final String SUBMISSION_ID = UUID.randomUUID().toString();
    private final String CLAIM_ID = UUID.randomUUID().toString();
    private final String CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
    private final String CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

    @Override
    protected List<Insert> inserts() {
        return List.of(
            BulkSubmissionInsert
                .builder()
                .id(BULK_SUBMISSION_ID)
                .userId(USER_ID)
                .build(),

            SubmissionInsert
                .builder()
                .id(SUBMISSION_ID)
                .bulkSubmissionId(BULK_SUBMISSION_ID)
                .officeAccountNumber(PROVIDER_ACCOUNT)
                .submissionPeriod("JUN-2025")
                .areaOfLaw("LEGAL_HELP")
                .userId(USER_ID)
                .build(),

            ClaimInsert
                .builder()
                .id(CLAIM_ID)
                .submissionId(SUBMISSION_ID)
                .uniqueFileNumber(UFN)
                .userId(USER_ID)
                .build(),

            ClaimSummaryFeeInsert
                .builder()
                .id(CLAIM_SUMMARY_FEE_ID)
                .claimId(CLAIM_ID)
                .userId(USER_ID)
                .build(),

            CalculatedFeeDetailInsert
                .builder()
                .id(CALCULATED_FEE_DETAIL_ID)
                .claimSummaryFeeId(CLAIM_SUMMARY_FEE_ID)
                .claimId(CLAIM_ID)
                .escaped(true)
                .userId(USER_ID)
                .build()
        );
    }

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

        review.assertAllowedTotalsAreCorrect("£127.87", "£767.22");
    }
}