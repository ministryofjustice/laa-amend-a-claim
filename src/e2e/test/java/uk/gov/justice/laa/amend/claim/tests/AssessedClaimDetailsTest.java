package uk.gov.justice.laa.amend.claim.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.models.AssessmentInsert;
import uk.gov.justice.laa.amend.claim.models.BulkSubmissionInsert;
import uk.gov.justice.laa.amend.claim.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.models.SubmissionInsert;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

import java.util.List;
import java.util.UUID;

@Epic("ClaimDetails")
@Feature("Assessed claim")
public class AssessedClaimDetailsTest extends BaseTest {

    private final String PROVIDER_ACCOUNT = "123456";
    private final String UFN = "031222/003";
    private final String SUBMISSION_ID = UUID.randomUUID().toString();
    private final String CLAIM_ID = UUID.randomUUID().toString();
    private final String CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
    private final String CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();
    private final String ASSESSMENT_ID = UUID.randomUUID().toString();

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
                .areaOfLaw("LEGAL_HELP")
                .userId(USER_ID)
                .build(),

            ClaimInsert
                .builder()
                .id(CLAIM_ID)
                .submissionId(SUBMISSION_ID)
                .uniqueFileNumber(UFN)
                .userId(USER_ID)
                .hasAssessment(true)
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
                .build(),

            AssessmentInsert
                .builder()
                .id(ASSESSMENT_ID)
                .claimSummaryFeeId(CLAIM_SUMMARY_FEE_ID)
                .claimId(CLAIM_ID)
                .userId(USER_ID)
                .build()
        );
    }

    @Test
    @DisplayName("E2E: Assessed ClaimDetails")
    void assessed() throws InterruptedException {
        SearchPage search = new SearchPage(page).navigateTo(EnvConfig.baseUrl());

        search.searchForClaim(
            PROVIDER_ACCOUNT,
            "",
            "",
            UFN,
            ""
        );

        search.clickViewForUfn(UFN);

        ClaimDetailsPage claimDetails = new ClaimDetailsPage(page);
        claimDetails.waitForPage();
        claimDetails.assertInfoAlertIsPresent();
        claimDetails.assertUpdateAssessmentOutcomeButtonIsPresent();

        //Thread.sleep(10000);

        claimDetails.assertCost("Fixed fee", "£239.35", "Not applicable", "£1,000.00");
        claimDetails.assertCost("Profit costs", "Not applicable", "£750.00", "£2,000.00");
        claimDetails.assertCost("Disbursements", "£400.00", "£400.00", "Not applicable");
        claimDetails.assertCost("Disbursement VAT", "£80.00", "£80.00", "Not applicable");
        claimDetails.assertCost("Detention travel and waiting costs", "£0.00", "£0.00", "£0.00");
        claimDetails.assertCost("JR and form filling", "£0.00", "£0.00", "£0.00");
        claimDetails.assertCost("Counsel costs", "£0.00", "£0.00", "£0.00");
        claimDetails.assertCost("VAT", "Yes", "Yes", "No");

        claimDetails.assertAssessedTotals("Assessed total VAT", "Not applicable", "Not applicable", "£3,000.00");
        claimDetails.assertAssessedTotals("Assessed total incl VAT", "Not applicable", "Not applicable", "£4,000.00");

        claimDetails.assertAllowedTotals("Allowed total VAT", "£127.87", "Not applicable", "£5,000.00");
        claimDetails.assertAllowedTotals("Allowed total incl VAT", "£767.22", "Not applicable", "£6,000.00");
    }
}
