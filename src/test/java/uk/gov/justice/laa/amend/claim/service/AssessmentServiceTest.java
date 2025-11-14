package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.Claim2;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentServiceTest {

    private AssessmentService assessmentService;
    private Claim2 claim;

    @BeforeEach
    void setUp() {
        assessmentService = new AssessmentService();
    }

    @Test
    void testApplyNilledOutcome_SetsAllMonetaryFieldsToZero() {
        // Given: A claim with non-zero values
        claim.setFixedFee(new ClaimField("Fixed Fee", "NA", new BigDecimal("100.00"), new BigDecimal("100.00")));
        claim.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));
        claim.setNetDisbursementAmount(new ClaimField("Disbursement", new BigDecimal("200.00"), new BigDecimal("180.00"), new BigDecimal("180.00")));
        claim.setDisbursementVatAmount(new ClaimField("Disbursement VAT", new BigDecimal("40.00"), new BigDecimal("36.00"), new BigDecimal("36.00")));
        claim.setVatClaimed(new ClaimField("VAT", new BigDecimal("148.00"), new BigDecimal("133.20"), new BigDecimal("133.20")));
        claim.setTotalAmount(new ClaimField("Total", new BigDecimal("988.00"), new BigDecimal("899.20"), new BigDecimal("899.20")));

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Only amendable monetary fields should be set to 0 (not VAT, Total, or Fixed Fee)
        assertEquals(new BigDecimal("100.00"), claim.getFixedFee().getAmended()); // Fixed Fee unchanged (NA)
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
        assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAmended());
        assertEquals(new BigDecimal("133.20"), claim.getVatClaimed().getAmended()); // VAT unchanged (calculated)
    }

    @Test
    void testApplyAssessmentOutcome_DoesNotApplyIfOutcomeUnchanged() {
        // Given: A claim with NILLED outcome already set
        claim.setAssessmentOutcome(OutcomeType.NILLED);
        claim.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("100.00")));

        BigDecimal originalAmended = new BigDecimal("100.00");

        // When: Same outcome is applied again
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Amended value should remain unchanged
        assertEquals(originalAmended, claim.getNetProfitCost().getAmended());
    }

    @Test
    void testApplyAssessmentOutcome_AppliesWhenOutcomeChanges() {
        // Given: A claim with no outcome set
        claim.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Amended value should be set to 0
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
    }
}