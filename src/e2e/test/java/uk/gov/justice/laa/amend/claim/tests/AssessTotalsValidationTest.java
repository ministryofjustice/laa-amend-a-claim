package uk.gov.justice.laa.amend.claim.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

import java.util.List;
import java.util.UUID;
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
import uk.gov.justice.laa.amend.claim.pages.AssessAssessedTotalsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

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
                BulkSubmissionInsert.builder()
                        .id(BULK_SUBMISSION_ID)
                        .userId(USER_ID)
                        .build(),
                SubmissionInsert.builder()
                        .id(SUBMISSION_ID)
                        .bulkSubmissionId(BULK_SUBMISSION_ID)
                        .officeAccountNumber(PROVIDER_ACCOUNT)
                        .submissionPeriod("JUN-2025")
                        .areaOfLaw("LEGAL_HELP")
                        .userId(USER_ID)
                        .build(),
                ClaimInsert.builder()
                        .id(CLAIM_ID)
                        .submissionId(SUBMISSION_ID)
                        .uniqueFileNumber(UFN)
                        .userId(USER_ID)
                        .build(),
                ClaimSummaryFeeInsert.builder()
                        .id(CLAIM_SUMMARY_FEE_ID)
                        .claimId(CLAIM_ID)
                        .userId(USER_ID)
                        .build(),
                CalculatedFeeDetailInsert.builder()
                        .id(CALCULATED_FEE_DETAIL_ID)
                        .claimSummaryFeeId(CLAIM_SUMMARY_FEE_ID)
                        .claimId(CLAIM_ID)
                        .escaped(true)
                        .userId(USER_ID)
                        .build());
    }

    private void navigateToReviewAndAmend() {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);
        search.searchForClaim(PROVIDER_ACCOUNT, MONTH, YEAR, UFN, "");
        search.clickViewForUfn(UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.clickAddUpdateAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.selectAssessmentOutcome("reduced-to-fixed-fee-assessed");
        outcome.saveChanges();

        assertTrue(page.url().contains("/review"));
    }

    @Test
    @DisplayName("Assess total claim value: submit blank shows required errors")
    void assessedTotalsBlankShowsRequiredErrors() {
        navigateToReviewAndAmend();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.clickAddAssessedTotalVat();

        AssessAssessedTotalsPage totals = new AssessAssessedTotalsPage(page);
        totals.saveChanges();

        totals.assertRequiredErrorsShown();
    }

    @Test
    @DisplayName("Assess total claim value: alphabetic input shows numeric errors")
    void assessedTotalsAlphaShowsNumericErrors() {
        navigateToReviewAndAmend();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.clickAddAssessedTotalVat();

        AssessAssessedTotalsPage totals = new AssessAssessedTotalsPage(page);
        totals.setTotalVat("dasad");
        totals.setTotalInclVat("dasad");
        totals.saveChanges();

        totals.assertNumericErrorsShown();
    }

    @Test
    @DisplayName("Assess total allowed value: submit blank shows required errors")
    void allowedTotalsBlankShowsRequiredErrors() {
        navigateToReviewAndAmend();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.clickAddAllowedTotalVat();

        AssessAllowedTotalsPage allowed = new AssessAllowedTotalsPage(page);
        allowed.saveChanges();

        allowed.assertRequiredErrorsShown();
    }

    @Test
    @DisplayName("Assess total allowed value: alphabetic input shows numeric errors")
    void allowedTotalsAlphaShowsNumericErrors() {
        navigateToReviewAndAmend();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.clickAddAllowedTotalVat();

        AssessAllowedTotalsPage allowed = new AssessAllowedTotalsPage(page);
        allowed.setTotalVat("dasad");
        allowed.setTotalInclVat("dasad");
        allowed.saveChanges();

        allowed.assertNumericErrorsShown();
    }

    @Test
    @DisplayName("Assess total allowed value: allowed totals are correct")
    void allowedTotalsAreCorrect() {
        navigateToReviewAndAmend();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);

        review.assertAllowedTotalsAreCorrect("£127.87", "£767.22");
    }
}
