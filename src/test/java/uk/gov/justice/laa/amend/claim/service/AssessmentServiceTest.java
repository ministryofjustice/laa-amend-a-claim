package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AssessmentServiceTest {

    private AssessmentService assessmentService;
    private ClaimSummary claimSummary;

    @BeforeEach
    void setUp() {
        assessmentService = new AssessmentService();
        claimSummary = createTestClaimSummary();
    }

    @Test
    void testApplyNilledOutcome_SetsAllMonetaryFieldsToZero() {
        // Given: A claim with non-zero values
        claimSummary.setFixedFee(new ClaimFieldRow("Fixed Fee", "NA", new BigDecimal("100.00"), new BigDecimal("100.00")));
        claimSummary.setNetProfitCost(new ClaimFieldRow("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));
        claimSummary.setNetDisbursementAmount(new ClaimFieldRow("Disbursement", new BigDecimal("200.00"), new BigDecimal("180.00"), new BigDecimal("180.00")));
        claimSummary.setDisbursementVatAmount(new ClaimFieldRow("Disbursement VAT", new BigDecimal("40.00"), new BigDecimal("36.00"), new BigDecimal("36.00")));
        claimSummary.setVatClaimed(new ClaimFieldRow("VAT", new BigDecimal("148.00"), new BigDecimal("133.20"), new BigDecimal("133.20")));
        claimSummary.setTotalAmount(new ClaimFieldRow("Total", new BigDecimal("988.00"), new BigDecimal("899.20"), new BigDecimal("899.20")));

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claimSummary, OutcomeType.NILLED);

        // Then: Only amendable monetary fields should be set to 0 (not VAT, Total, or Fixed Fee)
        assertEquals(new BigDecimal("100.00"), claimSummary.getFixedFee().getAmended()); // Fixed Fee unchanged (NA)
        assertEquals(BigDecimal.ZERO, claimSummary.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, claimSummary.getNetDisbursementAmount().getAmended());
        assertEquals(BigDecimal.ZERO, claimSummary.getDisbursementVatAmount().getAmended());
        assertEquals(new BigDecimal("133.20"), claimSummary.getVatClaimed().getAmended()); // VAT unchanged (calculated)
    }

    @Test
    void testApplyAssessmentOutcome_DoesNotApplyIfOutcomeUnchanged() {
        // Given: A claim with NILLED outcome already set
        claimSummary.setAssessmentOutcome(OutcomeType.NILLED);
        claimSummary.setNetProfitCost(new ClaimFieldRow("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("100.00")));

        BigDecimal originalAmended = new BigDecimal("100.00");

        // When: Same outcome is applied again
        assessmentService.applyAssessmentOutcome(claimSummary, OutcomeType.NILLED);

        // Then: Amended value should remain unchanged
        assertEquals(originalAmended, claimSummary.getNetProfitCost().getAmended());
    }

    @Test
    void testApplyAssessmentOutcome_AppliesWhenOutcomeChanges() {
        // Given: A claim with no outcome set
        claimSummary.setNetProfitCost(new ClaimFieldRow("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claimSummary, OutcomeType.NILLED);

        // Then: Amended value should be set to 0
        assertEquals(BigDecimal.ZERO, claimSummary.getNetProfitCost().getAmended());
    }

    @Test
    void testApplyAssessmentOutcome_HandlesNullFields() {
        // Given: A claim with null fields
        claimSummary.setFixedFee(null);
        claimSummary.setNetProfitCost(null);

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claimSummary, OutcomeType.NILLED);

        // Then: No exception should be thrown
        assertNull(claimSummary.getFixedFee());
        assertNull(claimSummary.getNetProfitCost());
    }

    @Test
    void testApplyAssessmentOutcome_DoesNothingIfClaimSummaryIsNull() {
        // When: Outcome is applied to null claim
        assertDoesNotThrow(() -> assessmentService.applyAssessmentOutcome(null, OutcomeType.NILLED));
    }

    @Test
    void testApplyAssessmentOutcome_DoesNothingIfOutcomeIsNull() {
        // When: Null outcome is applied
        claimSummary.setNetProfitCost(new ClaimFieldRow("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));

        assessmentService.applyAssessmentOutcome(claimSummary, null);

        // Then: Original amended value should remain
        assertEquals(new BigDecimal("450.00"), claimSummary.getNetProfitCost().getAmended());
    }

    private ClaimSummary createTestClaimSummary() {
        ClaimSummary summary = new ClaimSummary();
        summary.setClaimId("test-claim-123");
        summary.setSubmissionId("test-submission-456");
        return summary;
    }
}