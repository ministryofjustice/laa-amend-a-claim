package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.CivilClaim;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaim;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentServiceTest {

    private AssessmentService assessmentService;

    @BeforeEach
    void setUp() {
        assessmentService = new AssessmentService();
    }

    private CivilClaim createTestCivilClaim() {
        CivilClaim civilClaim = new CivilClaim();
        civilClaim.setClaimId("test-civil-claim-123");
        civilClaim.setSubmissionId("test-submission-456");
        return civilClaim;
    }

    private CrimeClaim createTestCrimeClaim() {
        CrimeClaim crimeClaim = new CrimeClaim();
        crimeClaim.setClaimId("test-crime-claim-123");
        crimeClaim.setSubmissionId("test-submission-456");
        return crimeClaim;
    }

    @Test
    void testApplyNilledOutcome_SetsAllMonetaryFieldsToZero() {
        // Given: A claim with non-zero values
        CivilClaim claim = createTestCivilClaim();
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
        CivilClaim claim = createTestCivilClaim();
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
        CivilClaim claim = createTestCivilClaim();
        claim.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Amended value should be set to 0
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
    }

    @Test
    void testApplyNilledOutcome_CivilClaimSpecificFields() {
        // Given: A civil claim with non-zero values
        CivilClaim civilClaim = createTestCivilClaim();
        civilClaim.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));
        civilClaim.setCounselsCost(new ClaimField("Counsel Costs", new BigDecimal("200.00"), new BigDecimal("180.00"), new BigDecimal("180.00")));
        civilClaim.setDetentionTravelWaitingCosts(new ClaimField("Detention Costs", new BigDecimal("100.00"), new BigDecimal("90.00"), new BigDecimal("90.00")));
        civilClaim.setJrFormFillingCost(new ClaimField("JR Form Costs", new BigDecimal("50.00"), new BigDecimal("45.00"), new BigDecimal("45.00")));
        civilClaim.setAdjournedHearing(new ClaimField("Adjourned Hearing", "No", "No", true));
        civilClaim.setCmrhTelephone(new ClaimField("CMRH Telephone", 0, 0, 3));
        civilClaim.setCmrhOral(new ClaimField("CMRH Oral", 0, 0, 2));
        civilClaim.setHoInterview(new ClaimField("HO Interview", 0, 0, 1));
        civilClaim.setSubstantiveHearing(new ClaimField("Substantive Hearing", 0, 0, 5));

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
        CrimeClaim crimeClaim = createTestCrimeClaim();
        crimeClaim.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));
        crimeClaim.setTravelCosts(new ClaimField("Travel Costs", new BigDecimal("150.00"), new BigDecimal("135.00"), new BigDecimal("135.00")));
        crimeClaim.setWaitingCosts(new ClaimField("Waiting Costs", new BigDecimal("75.00"), new BigDecimal("67.50"), new BigDecimal("67.50")));

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
        CivilClaim claim = createTestCivilClaim();
        claim.setAssessmentOutcome(OutcomeType.REDUCED);
        claim.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("300.00")));
        claim.setNetDisbursementAmount(new ClaimField("Disbursement", new BigDecimal("200.00"), new BigDecimal("180.00"), new BigDecimal("150.00")));

        // When: Switching to NILLED outcome
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Should apply NILLED logic and set values to zero
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
    }
}