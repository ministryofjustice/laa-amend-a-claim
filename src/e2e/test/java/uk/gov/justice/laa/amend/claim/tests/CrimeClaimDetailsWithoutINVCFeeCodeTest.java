package uk.gov.justice.laa.amend.claim.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
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
import uk.gov.justice.laa.amend.claim.pages.AssessAssessedTotalsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessProfitCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentCompletePage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

import java.util.List;
import java.util.UUID;

@Epic("ClaimDetails")
@Feature("Crime claim without INVC code")
public class CrimeClaimDetailsWithoutINVCFeeCodeTest extends BaseTest {

    private final String PROVIDER_ACCOUNT = "123456";
    private final String UFN = "031222/003";
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
                .submissionPeriod("APR-2025")
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
                .feeCode("CAPA")
                .escaped(true)
                .userId(USER_ID)
                .build()
        );
    }

    @Test
    @DisplayName("E2E: Assessed ClaimDetails - Reduced (still escaped) - Show claim Assessed/Allowed totals")
    void reduced() {
        SearchPage search = new SearchPage(page).navigateTo(EnvConfig.baseUrl());

        search.searchForClaim(
            PROVIDER_ACCOUNT,
            "",
            "",
            UFN,
            ""
        );

        search.clickViewForUfn(UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();


        details.clickAddUpdateAssessmentOutcome();


        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.selectAssessmentOutcome("reduced-still-escaped");
        outcome.selectVatLiable(true);
        outcome.clickContinue();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        Assertions.assertEquals("Review and amend", review.getHeadingText());


        review.clickAddProfitCosts();
        AssessProfitCostsPage profit = new AssessProfitCostsPage(page);
        profit.waitForPage();
        profit.setAssessedValue("999.99");
        profit.saveChanges();

        // -------- Total allowed value --------

        review.waitForPage();
        review.clickAddAllowedTotalVat();
        AssessAllowedTotalsPage allowedTotals = new AssessAllowedTotalsPage(page);
        allowedTotals.waitForPage();
        allowedTotals.setAllowedTotalVat("321");
        allowedTotals.setAllowedTotalInclVat("321");
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

        complete.clickViewAssessedClaim();

        ClaimDetailsPage claimDetails = new ClaimDetailsPage(page);
        claimDetails.waitForPage();
        claimDetails.assertUpdateAssessmentOutcomeButtonIsPresent();

        claimDetails.assertAllowedTotals("Allowed total VAT", "£127.87", "Not applicable", "£321.00");
        claimDetails.assertAllowedTotals("Allowed total incl VAT", "£767.22", "Not applicable", "£321.00");

        claimDetails.assertAssessedTotals("Assessed total VAT", "Not applicable", "Not applicable", "£321.00");
        claimDetails.assertAssessedTotals("Assessed total incl VAT", "Not applicable", "Not applicable", "£321.00");
    }

    @Test
    @DisplayName("E2E: Assessed ClaimDetails - Reduced (fixed fee) - Show claim Assessed/Allowed totals")
    void reducedFixedFee() {
        SearchPage search = new SearchPage(page).navigateTo(EnvConfig.baseUrl());

        search.searchForClaim(
            PROVIDER_ACCOUNT,
            "",
            "",
            UFN,
            ""
        );

        search.clickViewForUfn(UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();


        details.clickAddUpdateAssessmentOutcome();


        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.selectAssessmentOutcome("reduced-to-fixed-fee-assessed");
        outcome.selectVatLiable(true);
        outcome.clickContinue();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        Assertions.assertEquals("Review and amend", review.getHeadingText());

        review.clickAddProfitCosts();
        AssessProfitCostsPage profit = new AssessProfitCostsPage(page);
        profit.waitForPage();
        profit.setAssessedValue("999.99");
        profit.saveChanges();

        // -------- Total claim value --------

        review.waitForPage();
        review.clickAddAssessedTotalVat();
        AssessAssessedTotalsPage assessedTotals = new AssessAssessedTotalsPage(page);
        assessedTotals.waitForPage();
        assessedTotals.setAssessedTotalVat("321");
        assessedTotals.setAssessedTotalInclVat("321");
        assessedTotals.saveChanges();

        // -------- Total allowed value --------

        review.waitForPage();
        review.clickAddAllowedTotalVat();
        AssessAllowedTotalsPage allowedTotals = new AssessAllowedTotalsPage(page);
        allowedTotals.waitForPage();
        allowedTotals.setAllowedTotalVat("321");
        allowedTotals.setAllowedTotalInclVat("321");
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

        complete.clickViewAssessedClaim();

        ClaimDetailsPage claimDetails = new ClaimDetailsPage(page);
        claimDetails.waitForPage();
        claimDetails.assertUpdateAssessmentOutcomeButtonIsPresent();

        claimDetails.assertAllowedTotals("Allowed total VAT", "£127.87", "Not applicable", "£321.00");
        claimDetails.assertAllowedTotals("Allowed total incl VAT", "£767.22", "Not applicable", "£321.00");

        claimDetails.assertAssessedTotals("Assessed total VAT", "Not applicable", "Not applicable", "£321.00");
        claimDetails.assertAssessedTotals("Assessed total incl VAT", "Not applicable", "Not applicable", "£321.00");
    }

    @Test
    @DisplayName("E2E: Assessed ClaimDetails - Nilled - Show claim Assessed/Allowed totals")
    void nilled() {
        SearchPage search = new SearchPage(page).navigateTo(EnvConfig.baseUrl());

        search.searchForClaim(
            PROVIDER_ACCOUNT,
            "",
            "",
            UFN,
            ""
        );

        search.clickViewForUfn(UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();


        details.clickAddUpdateAssessmentOutcome();


        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.selectAssessmentOutcome("nilled");
        outcome.selectVatLiable(true);
        outcome.clickContinue();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        Assertions.assertEquals("Review and amend", review.getHeadingText());

        // -------- Submit --------

        review.waitForPage();
        review.submitAdjustments();

        AssessmentCompletePage complete = new AssessmentCompletePage(page);
        complete.waitForPage();

        Assertions.assertEquals("Assessment complete", complete.getHeadingText());
        Assertions.assertTrue(complete.getBodyText().contains("Your changes have been submitted"));
        Assertions.assertTrue(complete.goToSearchExists());
        Assertions.assertTrue(complete.viewAssessedClaimExists());

        complete.clickViewAssessedClaim();

        ClaimDetailsPage claimDetails = new ClaimDetailsPage(page);
        claimDetails.waitForPage();
        claimDetails.assertUpdateAssessmentOutcomeButtonIsPresent();

        claimDetails.assertAllowedTotals("Allowed total VAT", "£127.87", "Not applicable", "£0.00");
        claimDetails.assertAllowedTotals("Allowed total incl VAT", "£767.22", "Not applicable", "£0.00");

        claimDetails.assertAssessedTotals("Assessed total VAT", "Not applicable", "Not applicable", "£0.00");
        claimDetails.assertAssessedTotals("Assessed total incl VAT", "Not applicable", "Not applicable", "£0.00");
    }

    @Test
    @DisplayName("E2E: Assessed ClaimDetails - Assessed in full - Show claim Assessed/Allowed totals")
    void assessedInFull() {
        SearchPage search = new SearchPage(page).navigateTo(EnvConfig.baseUrl());

        search.searchForClaim(
            PROVIDER_ACCOUNT,
            "",
            "",
            UFN,
            ""
        );

        search.clickViewForUfn(UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();


        details.clickAddUpdateAssessmentOutcome();


        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.selectAssessmentOutcome("paid-in-full");
        outcome.selectVatLiable(true);
        outcome.clickContinue();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        Assertions.assertEquals("Review and amend", review.getHeadingText());

        // -------- Total allowed value --------

        review.waitForPage();
        review.clickAddAllowedTotalVat();
        AssessAllowedTotalsPage allowedTotals = new AssessAllowedTotalsPage(page);
        allowedTotals.waitForPage();
        allowedTotals.setAllowedTotalVat("321");
        allowedTotals.setAllowedTotalInclVat("321");
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

        complete.clickViewAssessedClaim();

        ClaimDetailsPage claimDetails = new ClaimDetailsPage(page);
        claimDetails.waitForPage();
        claimDetails.assertUpdateAssessmentOutcomeButtonIsPresent();

        claimDetails.assertAllowedTotals("Allowed total VAT", "£127.87", "Not applicable", "£321.00");
        claimDetails.assertAllowedTotals("Allowed total incl VAT", "£767.22", "Not applicable", "£321.00");

        claimDetails.assertAssessedTotals("Assessed total VAT", "Not applicable", "Not applicable", "£321.00");
        claimDetails.assertAssessedTotals("Assessed total incl VAT", "Not applicable", "Not applicable", "£321.00");
    }
}
