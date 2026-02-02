package uk.gov.justice.laa.amend.claim.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.BulkSubmissionInsert;
import uk.gov.justice.laa.amend.claim.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.models.SubmissionInsert;

import java.util.List;
import java.util.UUID;

@Epic("ClaimDetails")
@Feature("Crime claim without INVC code")
public class CrimeClaimDetailsWithoutINVCFeeCodeTest extends E2eBaseTest {

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
        submitWithAddedProfitCostsAndAllowedTotals("reduced-still-escaped");
    }

    @Test
    @DisplayName("E2E: Assessed ClaimDetails - Reduced (fixed fee) - Show claim Assessed/Allowed totals")
    void reducedFixedFee() {
        submitWithAddedProfitCostsAndAssessedTotalsAndAllowedTotals("reduced-to-fixed-fee-assessed");
    }

    @Test
    @DisplayName("E2E: Assessed ClaimDetails - Nilled - Show claim Assessed/Allowed totals")
    void nilled() {
        submitNilled();
    }

    @Test
    @DisplayName("E2E: Assessed ClaimDetails - Assessed in full - Show claim Assessed/Allowed totals")
    void assessedInFull() {
        submitWithAddedAllowedTotals("paid-in-full");
    }
}
