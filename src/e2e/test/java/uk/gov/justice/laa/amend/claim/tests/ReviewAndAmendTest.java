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
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.AssessTravelCostsPage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

public class ReviewAndAmendTest extends BaseTest {

    // ---------------- Crime data ----------------
    private final String CRIME_PROVIDER_ACCOUNT = "123456";
    private final String CRIME_UFN = generateUfn();
    private final String CRIME_MONTH = "04";
    private final String CRIME_YEAR = "2025";
    private final String CRIME_SUBMISSION_ID = UUID.randomUUID().toString();
    private final String CRIME_CLAIM_ID = UUID.randomUUID().toString();
    private final String CRIME_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
    private final String CRIME_CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

    // ---------------- Civil data ----------------
    private final String CIVIL_PROVIDER_ACCOUNT = "234567";
    private final String CIVIL_UFN = generateUfn();
    private final String CIVIL_MONTH = "06";
    private final String CIVIL_YEAR = "2025";
    private final String CIVIL_SUBMISSION_ID = UUID.randomUUID().toString();
    private final String CIVIL_CLAIM_ID = UUID.randomUUID().toString();
    private final String CIVIL_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
    private final String CIVIL_CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

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
                .id(CRIME_SUBMISSION_ID)
                .bulkSubmissionId(BULK_SUBMISSION_ID)
                .officeAccountNumber(CRIME_PROVIDER_ACCOUNT)
                .submissionPeriod("APR-2025")
                .areaOfLaw("CRIME_LOWER")
                .userId(USER_ID)
                .build(),

            SubmissionInsert
                .builder()
                .id(CIVIL_SUBMISSION_ID)
                .bulkSubmissionId(BULK_SUBMISSION_ID)
                .officeAccountNumber(CIVIL_PROVIDER_ACCOUNT)
                .submissionPeriod("JUN-2025")
                .areaOfLaw("LEGAL_HELP")
                .userId(USER_ID)
                .build(),

            ClaimInsert
                .builder()
                .id(CRIME_CLAIM_ID)
                .submissionId(CRIME_SUBMISSION_ID)
                .uniqueFileNumber(CRIME_UFN)
                .userId(USER_ID)
                .build(),

            ClaimInsert
                .builder()
                .id(CIVIL_CLAIM_ID)
                .submissionId(CIVIL_SUBMISSION_ID)
                .uniqueFileNumber(CIVIL_UFN)
                .userId(USER_ID)
                .build(),

            ClaimSummaryFeeInsert
                .builder()
                .id(CRIME_CLAIM_SUMMARY_FEE_ID)
                .claimId(CRIME_CLAIM_ID)
                .userId(USER_ID)
                .build(),

            ClaimSummaryFeeInsert
                .builder()
                .id(CIVIL_CLAIM_SUMMARY_FEE_ID)
                .claimId(CIVIL_CLAIM_ID)
                .userId(USER_ID)
                .build(),

            CalculatedFeeDetailInsert
                .builder()
                .id(CRIME_CALCULATED_FEE_DETAIL_ID)
                .claimSummaryFeeId(CRIME_CLAIM_SUMMARY_FEE_ID)
                .claimId(CRIME_CLAIM_ID)
                .escaped(true)
                .userId(USER_ID)
                .build(),

            CalculatedFeeDetailInsert
                .builder()
                .id(CIVIL_CALCULATED_FEE_DETAIL_ID)
                .claimSummaryFeeId(CIVIL_CLAIM_SUMMARY_FEE_ID)
                .claimId(CIVIL_CLAIM_ID)
                .escaped(true)
                .userId(USER_ID)
                .build()
        );
    }

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
        details.clickAddUpdateAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);

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

        review.submitAdjustments();

        assertTrue(page.url().contains("/review"));
        review.assertSubmitTotalsRequiredErrors();
    }

    @Test
    @DisplayName("Review & amend back link navigates to assessment outcome page (not browser history)")
    void backLinkNavigatesToAssessmentOutcome() {
        navigateToReviewAndAmend(
            CRIME_PROVIDER_ACCOUNT,
            CRIME_MONTH,
            CRIME_YEAR,
            CRIME_UFN
        );

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);

        // Navigate to a change page to add entries to browser history
        review.clickChangeTravelCosts();
        AssessTravelCostsPage travelCosts = new AssessTravelCostsPage(page);

        // Cancel to return to review page
        travelCosts.cancel();

        // Click back link which should go to assessment-outcome, not travel-costs
        review.clickBackLink();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);

        assertTrue(page.url().contains("/assessment-outcome"));
    }
}