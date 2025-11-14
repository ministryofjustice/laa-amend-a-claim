package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimSummary;

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

    private ClaimSummary createTestClaimSummary() {
        ClaimSummary summary = new ClaimSummary();
        summary.setClaimId("test-claim-123");
        summary.setSubmissionId("test-submission-456");
        return summary;
    }

    private CivilClaimSummary createTestCivilClaimSummary() {
        CivilClaimSummary civilClaim = new CivilClaimSummary();
        civilClaim.setClaimId("test-civil-claim-123");
        civilClaim.setSubmissionId("test-submission-456");
        return civilClaim;
    }

    private CrimeClaimSummary createTestCrimeClaimSummary() {
        CrimeClaimSummary crimeClaim = new CrimeClaimSummary();
        crimeClaim.setClaimId("test-crime-claim-123");
        crimeClaim.setSubmissionId("test-submission-456");
        return crimeClaim;
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
    void testApplyNilledOutcome_CivilClaimSpecificFields() {
        // Given: A civil claim with non-zero values
        CivilClaimSummary civilClaim = createTestCivilClaimSummary();
        civilClaim.setNetProfitCost(new ClaimFieldRow("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));
        civilClaim.setCounselsCost(new ClaimFieldRow("Counsel Costs", new BigDecimal("200.00"), new BigDecimal("180.00"), new BigDecimal("180.00")));
        civilClaim.setDetentionTravelWaitingCosts(new ClaimFieldRow("Detention Costs", new BigDecimal("100.00"), new BigDecimal("90.00"), new BigDecimal("90.00")));
        civilClaim.setJrFormFillingCost(new ClaimFieldRow("JR Form Costs", new BigDecimal("50.00"), new BigDecimal("45.00"), new BigDecimal("45.00")));
        civilClaim.setAdjournedHearing(new ClaimFieldRow("Adjourned Hearing", "No", "No", true));
        civilClaim.setCmrhTelephone(new ClaimFieldRow("CMRH Telephone", 0, 0, 3));
        civilClaim.setCmrhOral(new ClaimFieldRow("CMRH Oral", 0, 0, 2));
        civilClaim.setHoInterview(new ClaimFieldRow("HO Interview", 0, 0, 1));
        civilClaim.setSubstantiveHearing(new ClaimFieldRow("Substantive Hearing", 0, 0, 5));

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(civilClaim, OutcomeType.NILLED);

        // Then: All civil-specific fields should be set appropriately
        assertEquals(BigDecimal.ZERO, civilClaim.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, civilClaim.getCounselsCost().getAmended());
        assertEquals(BigDecimal.ZERO, civilClaim.getDetentionTravelWaitingCosts().getAmended());
        assertEquals(BigDecimal.ZERO, civilClaim.getJrFormFillingCost().getAmended());
        assertEquals(false, civilClaim.getAdjournedHearing().getAmended());
        assertEquals(0, civilClaim.getCmrhTelephone().getAmended());
        assertEquals(0, civilClaim.getCmrhOral().getAmended());
        assertEquals(0, civilClaim.getHoInterview().getAmended());
        assertEquals(0, civilClaim.getSubstantiveHearing().getAmended());
    }

    @Test
    void testApplyNilledOutcome_CrimeClaimSpecificFields() {
        // Given: A crime claim with non-zero values
        CrimeClaimSummary crimeClaim = createTestCrimeClaimSummary();
        crimeClaim.setNetProfitCost(new ClaimFieldRow("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));
        crimeClaim.setTravelCosts(new ClaimFieldRow("Travel Costs", new BigDecimal("150.00"), new BigDecimal("135.00"), new BigDecimal("135.00")));
        crimeClaim.setWaitingCosts(new ClaimFieldRow("Waiting Costs", new BigDecimal("75.00"), new BigDecimal("67.50"), new BigDecimal("67.50")));

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(crimeClaim, OutcomeType.NILLED);

        // Then: All crime-specific fields should be set to zero
        assertEquals(BigDecimal.ZERO, crimeClaim.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, crimeClaim.getTravelCosts().getAmended());
        assertEquals(BigDecimal.ZERO, crimeClaim.getWaitingCosts().getAmended());
    }

    @Test
    void testApplyAssessmentOutcome_SwitchingFromReducedToNilled() {
        // Given: A claim with REDUCED outcome and custom values
        claimSummary.setAssessmentOutcome(OutcomeType.REDUCED);
        claimSummary.setNetProfitCost(new ClaimFieldRow("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("300.00")));
        claimSummary.setNetDisbursementAmount(new ClaimFieldRow("Disbursement", new BigDecimal("200.00"), new BigDecimal("180.00"), new BigDecimal("150.00")));

        // When: Switching to NILLED outcome
        assessmentService.applyAssessmentOutcome(claimSummary, OutcomeType.NILLED);

        // Then: Should apply NILLED logic and set values to zero
        assertEquals(BigDecimal.ZERO, claimSummary.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, claimSummary.getNetDisbursementAmount().getAmended());
    }
}