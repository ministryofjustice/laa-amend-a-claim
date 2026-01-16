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

import java.util.List;
import java.util.UUID;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssessCostsValidationTest extends BaseTest {

    private final String BULK_SUBMISSION_ID = UUID.randomUUID().toString();

    // ---------------- Crime data ----------------
    private final String CRIME_PROVIDER_ACCOUNT = "123456";
    private final String CRIME_UFN = "031222/002";
    private final String CRIME_MONTH = "04";
    private final String CRIME_YEAR = "2025";
    private final String CRIME_SUBMISSION_ID = UUID.randomUUID().toString();
    private final String CRIME_CLAIM_ID = UUID.randomUUID().toString();
    private final String CRIME_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
    private final String CRIME_CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

    // ---------------- Civil data ----------------
    private final String CIVIL_PROVIDER_ACCOUNT = "234567";
    private final String CIVIL_UFN = "121019/001";
    private final String CIVIL_MONTH = "06";
    private final String CIVIL_YEAR = "2025";
    private final String CIVIL_SUBMISSION_ID = UUID.randomUUID().toString();
    private final String CIVIL_CLAIM_ID = UUID.randomUUID().toString();
    private final String CIVIL_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
    private final String CIVIL_CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

    private final String USER_ID = UUID.randomUUID().toString();

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
                .userId(USER_ID)
                .build(),

            CalculatedFeeDetailInsert
                .builder()
                .id(CIVIL_CALCULATED_FEE_DETAIL_ID)
                .claimSummaryFeeId(CIVIL_CLAIM_SUMMARY_FEE_ID)
                .claimId(CIVIL_CLAIM_ID)
                .userId(USER_ID)
                .build()
        );
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