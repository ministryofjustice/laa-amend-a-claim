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
import uk.gov.justice.laa.amend.claim.models.ClaimDetailsFixture;
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

import static uk.gov.justice.laa.amend.claim.base.E2ETestHelper.getFromMap;
import static uk.gov.justice.laa.amend.claim.base.E2ETestHelper.loadFixture;
import static uk.gov.justice.laa.amend.claim.base.E2ETestHelper.normalizeMoneyForInput;

@Epic("ClaimDetails")
@Feature("Assessment Totals")
public class AssessedClaimDetailsTest extends BaseTest {

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
                .feeCode("INVC")
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

    @Test
    @DisplayName("E2E: Assessed ClaimDetails - Reduced (still escaped) - Show claim Assessed/Allowed totals")
    void showClaimTotals() {
        ClaimDetailsFixture claimDetailsFixture = loadFixture("fixtures/claim-details/crime-reduced-002.json");
        SearchPage search = new SearchPage(page).navigateTo(EnvConfig.baseUrl());

        search.searchForClaim(
            PROVIDER_ACCOUNT,
            "",
            "",
            UFN,
            ""
        );

        search.clickViewForUfn(claimDetailsFixture.getUfn());

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();


        details.clickAddUpdateAssessmentOutcome();


        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.selectAssessmentOutcome(claimDetailsFixture.getOutcome());
        outcome.selectVatLiable(true);
        outcome.clickContinue();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        Assertions.assertEquals("Review and amend", review.getHeadingText());


        review.clickChangeProfitCosts();
        AssessProfitCostsPage profit = new AssessProfitCostsPage(page);
        profit.waitForPage();
        profit.setAssessedValue("999.99");
        profit.saveChanges();

        // -------- Total claim value --------

        String assessedVatRaw = getFromMap(claimDetailsFixture.getAssessedTotals(), "Assessed total VAT", 2);
        String assessedInclVatRaw = getFromMap(claimDetailsFixture.getAssessedTotals(), "Assessed total incl VAT", 2);

        // Normalize for input fields (strip currency, commas, handle "Not applicable")
        String assessedVat = normalizeMoneyForInput(assessedVatRaw);       // e.g., "£239.35" -> "239.35"
        String assessedInclVat = normalizeMoneyForInput(assessedInclVatRaw); // "Not applicable" -> null

        review.waitForPage();
        review.clickAddAssessedTotalVat();
        AssessAssessedTotalsPage assessedTotals = new AssessAssessedTotalsPage(page);
        assessedTotals.waitForPage();
        assessedTotals.setAssessedTotalVat(assessedVat);
        assessedTotals.setAssessedTotalInclVat(assessedInclVat);
        assessedTotals.saveChanges();

        // -------- Total allowed value --------

        String allowedVatRaw = getFromMap(claimDetailsFixture.getAllowedTotals(), "Allowed total VAT", 2);
        String allowedInclVatRaw = getFromMap(claimDetailsFixture.getAllowedTotals(), "Allowed total incl VAT", 2);

        // Normalize for input fields (strip currency, commas, handle "Not applicable")
        String allowedVat = normalizeMoneyForInput(allowedVatRaw);       // e.g., "£239.35" -> "239.35"
        String allowedInclVat = normalizeMoneyForInput(allowedInclVatRaw); // "Not applicable" -> null

        review.waitForPage();
        review.clickAddAllowedTotalVat();
        AssessAllowedTotalsPage allowedTotals = new AssessAllowedTotalsPage(page);
        allowedTotals.waitForPage();
        allowedTotals.setAllowedTotalVat(allowedVat);
        allowedTotals.setAllowedTotalInclVat(allowedInclVat);
        allowedTotals.saveChanges();

        // -------- Submit --------

        review.waitForPage();
        review.submitAdjustments();

        AssessmentCompletePage complete = new AssessmentCompletePage(page);
        complete.waitForPage();
        complete.storeAssessmentId(store);

        Assertions.assertEquals("Assessment complete", complete.getHeadingText());
        Assertions.assertTrue(complete.getBodyText().contains("Your changes have been submitted"));
        Assertions.assertTrue(complete.goToSearchExists());
        Assertions.assertTrue(complete.viewAssessedClaimExists());

        complete.clickViewAssessedClaim();

        ClaimDetailsPage claimDetails = new ClaimDetailsPage(page);
        claimDetails.waitForPage();
        claimDetails.assertUpdateAssessmentOutcomeButtonIsPresent();
        claimDetails.assertAllowedTotals(claimDetailsFixture.getAllowedTotals());
        claimDetails.assertAssessedTotals(claimDetailsFixture.getAssessedTotals());
    }

}
