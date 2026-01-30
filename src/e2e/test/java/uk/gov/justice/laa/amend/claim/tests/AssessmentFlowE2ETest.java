package uk.gov.justice.laa.amend.claim.tests;

import org.junit.jupiter.api.Assertions;
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
import uk.gov.justice.laa.amend.claim.pages.AssessDisbursementsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessDisbursementsVatPage;
import uk.gov.justice.laa.amend.claim.pages.AssessProfitCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessTotalClaimValuePage;
import uk.gov.justice.laa.amend.claim.pages.AssessTravelCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessWaitingCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentCompletePage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

import java.util.List;
import java.util.UUID;

import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

public class AssessmentFlowE2ETest extends BaseTest {

    private final String PROVIDER_ACCOUNT = "123456";
    private final String UFN = generateUfn();
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
                .submissionPeriod("MAR-2020")
                .areaOfLaw("CRIME_LOWER")
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
                .feeCode("INVC")
                .escaped(true)
                .userId(USER_ID)
                .build()
        );
    }

    @Test
    @DisplayName("E2E: Full Crime Assessment Flow – Search → View → Outcome → Amend All → Submit")
    void fullAssessmentFlow() {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

        search.searchForClaim(
            PROVIDER_ACCOUNT,
            "03",
            "2020",
            UFN,
            ""
        );

        search.clickViewForUfn(UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();
        details.clickAddUpdateAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.selectAssessmentOutcome("assessed in full");
        outcome.selectVatLiable(true);
        outcome.clickContinue();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        Assertions.assertEquals("Review and amend", review.getHeadingText());

        // -------- Claim costs --------

        review.clickChangeProfitCosts();
        AssessProfitCostsPage profit = new AssessProfitCostsPage(page);
        profit.waitForPage();
        profit.setAssessedValue("999.99");
        profit.saveChanges();

        review.waitForPage();
        review.clickChangeDisbursements();
        AssessDisbursementsPage disb = new AssessDisbursementsPage(page);
        disb.waitForPage();
        disb.setAssessedValue("111.11");
        disb.saveChanges();

        review.waitForPage();
        review.clickChangeDisbursementsVat();
        AssessDisbursementsVatPage disbVat = new AssessDisbursementsVatPage(page);
        disbVat.waitForPage();
        disbVat.setAssessedValue("22.22");
        disbVat.saveChanges();

        review.waitForPage();
        review.clickChangeTravelCosts();
        AssessTravelCostsPage travel = new AssessTravelCostsPage(page);
        travel.waitForPage();
        travel.setAssessedValue("10.00");
        travel.saveChanges();

        review.waitForPage();
        review.clickChangeWaitingCosts();
        AssessWaitingCostsPage waiting = new AssessWaitingCostsPage(page);
        waiting.waitForPage();
        waiting.setAssessedValue("12.34");
        waiting.saveChanges();

        // -------- Total claim value (assessed totals) --------

        review.waitForPage();
        review.clickAddAssessedTotalVat();
        AssessTotalClaimValuePage assessedTotals = new AssessTotalClaimValuePage(page);
        assessedTotals.waitForPage();
        assessedTotals.setAssessedTotalVat("5.00");
        assessedTotals.setAssessedTotalInclVat("1000.00");
        assessedTotals.saveChanges();

        // -------- Total allowed value --------

        review.waitForPage();
        review.clickAddAllowedTotalVat();
        AssessAllowedTotalsPage allowedTotals = new AssessAllowedTotalsPage(page);
        allowedTotals.waitForPage();
        allowedTotals.setAllowedTotalVat("6.00");
        allowedTotals.setAllowedTotalInclVat("1100.00");
        allowedTotals.saveChanges();

        // -------- Submit --------

        review.waitForPage();
        review.submitAdjustments();

        AssessmentCompletePage complete = new AssessmentCompletePage(page);
        complete.waitForPage();

        Assertions.assertEquals("Assessment complete", complete.getHeadingText());
        Assertions.assertTrue(complete.getBodyText().contains("Your changes have been submitted"));
        Assertions.assertTrue(complete.goToSearchExists());
        Assertions.assertTrue(complete.viewAssessedClaimExists());
    }
}