package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssessmentServiceTest {

    private AssessmentService assessmentService;

    @BeforeEach
    void setUp() {
        assessmentService = new AssessmentService();
    }

    private ClaimField createClaimField() {
        return new ClaimField(
            "foo",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(200),
            BigDecimal.valueOf(300)
        );
    }

    private CivilClaimDetails createTestCivilClaim() {
        CivilClaimDetails civilClaimDetails = new CivilClaimDetails();
        civilClaimDetails.setClaimId("test-civil-claim-123");
        civilClaimDetails.setSubmissionId("test-submission-456");
        return civilClaimDetails;
    }

    private CrimeClaimDetails createTestCrimeClaim() {
        CrimeClaimDetails crimeClaimDetails = new CrimeClaimDetails();
        crimeClaimDetails.setClaimId("test-crime-claim-123");
        crimeClaimDetails.setSubmissionId("test-submission-456");
        return crimeClaimDetails;
    }

    @Test
    void testApplyNilledOutcome_SetsAllMonetaryFieldsToZero() {
        // Given: A claim with non-zero values
        CivilClaimDetails claim = createTestCivilClaim();
        claim.setFixedFee(createClaimField());
        claim.setNetProfitCost(createClaimField());
        claim.setNetDisbursementAmount(createClaimField());
        claim.setDisbursementVatAmount(createClaimField());
        claim.setVatClaimed(createClaimField());
        claim.setTotalAmount(createClaimField());

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Only amendable monetary fields should be set to 0 (not VAT, Total, or Fixed Fee)
        assertEquals(BigDecimal.valueOf(300), claim.getFixedFee().getAmended()); // Fixed Fee unchanged (NA)
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
        assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAmended());
        assertEquals(BigDecimal.valueOf(300), claim.getVatClaimed().getAmended()); // VAT unchanged (calculated)
    }

    @Test
    void testApplyAssessmentOutcome_DoesNotApplyIfOutcomeUnchanged() {
        // Given: A claim with NILLED outcome already set
        CivilClaimDetails claim = createTestCivilClaim();
        claim.setAssessmentOutcome(OutcomeType.NILLED);
        claim.setNetProfitCost(createClaimField());

        // When: Same outcome is applied again
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Amended value should remain unchanged
        assertEquals(BigDecimal.valueOf(300), claim.getNetProfitCost().getAmended());
    }

    @Test
    void testApplyAssessmentOutcome_AppliesWhenOutcomeChanges() {
        // Given: A claim with no outcome set
        CivilClaimDetails claim = createTestCivilClaim();
        claim.setNetProfitCost(createClaimField());

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Amended value should be set to 0
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
    }

    @Test
    void testApplyNilledOutcome_CivilClaimSpecificFields() {
        // Given: A civil claim with non-zero values
        CivilClaimDetails civilClaimDetails = createTestCivilClaim();
        civilClaimDetails.setNetProfitCost(createClaimField());
        civilClaimDetails.setCounselsCost(createClaimField());
        civilClaimDetails.setDetentionTravelWaitingCosts(createClaimField());
        civilClaimDetails.setJrFormFillingCost(createClaimField());
        civilClaimDetails.setAdjournedHearing(createClaimField());
        civilClaimDetails.setCmrhTelephone(createClaimField());
        civilClaimDetails.setCmrhOral(createClaimField());
        civilClaimDetails.setHoInterview(createClaimField());
        civilClaimDetails.setSubstantiveHearing(createClaimField());

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(civilClaimDetails, OutcomeType.NILLED);

        // Then: All civil-specific fields should be set appropriately
        assertEquals(BigDecimal.ZERO, civilClaimDetails.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, civilClaimDetails.getCounselsCost().getAmended());
        assertEquals(BigDecimal.ZERO, civilClaimDetails.getDetentionTravelWaitingCosts().getAmended());
        assertEquals(BigDecimal.ZERO, civilClaimDetails.getJrFormFillingCost().getAmended());
        assertEquals(false, civilClaimDetails.getAdjournedHearing().getAmended());
        assertEquals(0, civilClaimDetails.getCmrhTelephone().getAmended());
        assertEquals(0, civilClaimDetails.getCmrhOral().getAmended());
        assertEquals(0, civilClaimDetails.getHoInterview().getAmended());
        assertEquals(0, civilClaimDetails.getSubstantiveHearing().getAmended());
    }

    @Test
    void testApplyNilledOutcome_CrimeClaimSpecificFields() {
        // Given: A crime claim with non-zero values
        CrimeClaimDetails crimeClaimDetails = createTestCrimeClaim();
        crimeClaimDetails.setNetProfitCost(createClaimField());
        crimeClaimDetails.setTravelCosts(createClaimField());
        crimeClaimDetails.setWaitingCosts(createClaimField());

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(crimeClaimDetails, OutcomeType.NILLED);

        // Then: All crime-specific fields should be set to zero
        assertEquals(BigDecimal.ZERO, crimeClaimDetails.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, crimeClaimDetails.getTravelCosts().getAmended());
        assertEquals(BigDecimal.ZERO, crimeClaimDetails.getWaitingCosts().getAmended());
    }

    @Test
    void testApplyAssessmentOutcome_SwitchingFromReducedToNilled() {
        // Given: A claim with REDUCED outcome and custom values
        CivilClaimDetails claim = createTestCivilClaim();
        claim.setAssessmentOutcome(OutcomeType.REDUCED);
        claim.setNetProfitCost(createClaimField());
        claim.setNetDisbursementAmount(createClaimField());

        // When: Switching to NILLED outcome
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Should apply NILLED logic and set values to zero
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
    }

    @Test
    void testApplyReducedToFixedFeeOutcome_whenCivilClaim() {
        CivilClaimDetails claim = createTestCivilClaim();
        claim.setVatClaimed(createClaimField());
        claim.setFixedFee(createClaimField());
        claim.setNetProfitCost(createClaimField());
        claim.setNetDisbursementAmount(createClaimField());
        claim.setDisbursementVatAmount(createClaimField());
        claim.setDetentionTravelWaitingCosts(createClaimField());
        claim.setJrFormFillingCost(createClaimField());
        claim.setAdjournedHearing(createClaimField());
        claim.setCmrhTelephone(createClaimField());
        claim.setCmrhOral(createClaimField());
        claim.setHoInterview(createClaimField());
        claim.setSubstantiveHearing(createClaimField());
        claim.setCounselsCost(createClaimField());

        assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

        assertEquals(claim.getVatClaimed().getCalculated(), claim.getVatClaimed().getAmended());
        assertEquals(claim.getFixedFee().getCalculated(), claim.getFixedFee().getAmended());
        assertNull(claim.getNetProfitCost().getAmended());
        assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
        assertEquals(claim.getDisbursementVatAmount().getCalculated(), claim.getDisbursementVatAmount().getAmended());
        assertEquals(claim.getDetentionTravelWaitingCosts().getCalculated(), claim.getDetentionTravelWaitingCosts().getAmended());
        assertEquals(claim.getJrFormFillingCost().getCalculated(), claim.getJrFormFillingCost().getAmended());
        assertEquals(claim.getAdjournedHearing().getCalculated(), claim.getAdjournedHearing().getAmended());
        assertEquals(claim.getCmrhTelephone().getCalculated(), claim.getCmrhTelephone().getAmended());
        assertEquals(claim.getCmrhOral().getCalculated(), claim.getCmrhOral().getAmended());
        assertEquals(claim.getHoInterview().getCalculated(), claim.getHoInterview().getAmended());
        assertEquals(claim.getSubstantiveHearing().getCalculated(), claim.getSubstantiveHearing().getAmended());
        assertEquals(claim.getCounselsCost().getCalculated(), claim.getCounselsCost().getAmended());
    }

    @Test
    void testApplyReducedToFixedFeeOutcome_whenCrimeClaim() {
        CrimeClaimDetails claim = createTestCrimeClaim();
        claim.setVatClaimed(createClaimField());
        claim.setFixedFee(createClaimField());
        claim.setNetProfitCost(createClaimField());
        claim.setNetDisbursementAmount(createClaimField());
        claim.setDisbursementVatAmount(createClaimField());
        claim.setTravelCosts(createClaimField());
        claim.setWaitingCosts(createClaimField());

        assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

        assertEquals(claim.getVatClaimed().getCalculated(), claim.getVatClaimed().getAmended());
        assertEquals(claim.getFixedFee().getCalculated(), claim.getFixedFee().getAmended());
        assertNull(claim.getNetProfitCost().getAmended());
        assertTrue(claim.getNetProfitCost().isNeedsAmending());
        assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
        assertEquals(claim.getDisbursementVatAmount().getCalculated(), claim.getDisbursementVatAmount().getAmended());
        assertEquals(claim.getTravelCosts().getCalculated(), claim.getTravelCosts().getAmended());
        assertEquals(claim.getWaitingCosts().getCalculated(), claim.getWaitingCosts().getAmended());
    }
}