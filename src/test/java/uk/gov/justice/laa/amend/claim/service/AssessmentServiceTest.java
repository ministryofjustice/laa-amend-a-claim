package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.AmendStatus;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.resources.CreateMockClaims;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AssessmentServiceTest {

    private AssessmentService assessmentService;
    private CreateMockClaims mockClaims;

    @BeforeEach
    void setUp() {
        assessmentService = new AssessmentService();
        mockClaims = new CreateMockClaims();
    }

    @Test
    void testApplyNilledOutcome_SetsAllMonetaryFieldsToZero() {
        // Given: A claim with non-zero values
        CivilClaimDetails claim = mockClaims.createMockCivilClaim();

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
        CivilClaimDetails claim = mockClaims.createMockCivilClaim();
        claim.setAssessmentOutcome(OutcomeType.NILLED);
        claim.setNetProfitCost(mockClaims.createClaimField());

        // When: Same outcome is applied again
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Amended value should remain unchanged
        assertEquals(BigDecimal.valueOf(300), claim.getNetProfitCost().getAmended());
    }

    @Test
    void testApplyAssessmentOutcome_AppliesWhenOutcomeChanges() {
        // Given: A claim with no outcome set
        CivilClaimDetails claim = mockClaims.createMockCivilClaim();
        claim.setNetProfitCost(mockClaims.createClaimField());

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Amended value should be set to 0
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
    }

    @Test
    void testApplyNilledOutcome_CivilClaimSpecificFields() {
        // Given: A civil claim with non-zero values
        CivilClaimDetails claim = mockClaims.createMockCivilClaim();

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: All civil-specific fields should be set appropriately
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getNetProfitCost().getStatus());

        assertEquals(BigDecimal.ZERO, claim.getCounselsCost().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getCounselsCost().getStatus());

        assertEquals(BigDecimal.ZERO, claim.getDetentionTravelWaitingCosts().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getDetentionTravelWaitingCosts().getStatus());

        assertEquals(BigDecimal.ZERO, claim.getJrFormFillingCost().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getJrFormFillingCost().getStatus());

        assertEquals(false, claim.getAdjournedHearing().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getAdjournedHearing().getStatus());

        assertEquals(0, claim.getCmrhTelephone().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getCmrhTelephone().getStatus());

        assertEquals(0, claim.getCmrhOral().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getCmrhOral().getStatus());

        assertEquals(0, claim.getHoInterview().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getHoInterview().getStatus());

        assertEquals(0, claim.getSubstantiveHearing().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getSubstantiveHearing().getStatus());
    }

    @Test
    void testApplyNilledOutcome_CrimeClaimSpecificFields() {
        // Given: A crime claim with non-zero values
        CrimeClaimDetails claim = mockClaims.createMockCrimeClaim();

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: All crime-specific fields should be set to zero
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getNetProfitCost().getStatus());

        assertEquals(BigDecimal.ZERO, claim.getTravelCosts().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getTravelCosts().getStatus());

        assertEquals(BigDecimal.ZERO, claim.getWaitingCosts().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getWaitingCosts().getStatus());
    }

    @Test
    void testApplyAssessmentOutcome_SwitchingFromReducedToNilled() {
        // Given: A claim with REDUCED outcome and custom values
        CivilClaimDetails claim = mockClaims.createMockCivilClaim();
        claim.setAssessmentOutcome(OutcomeType.REDUCED);
        claim.setNetProfitCost(mockClaims.createClaimField());
        claim.setNetDisbursementAmount(mockClaims.createClaimField());

        // When: Switching to NILLED outcome
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Should apply NILLED logic and set values to zero
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getNetProfitCost().getStatus());

        assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
        assertEquals(AmendStatus.NOT_AMENDABLE, claim.getNetDisbursementAmount().getStatus());
    }

    @Test
    void testApplyReducedToFixedFeeOutcome_whenCivilClaim() {
        CivilClaimDetails claim = mockClaims.createMockCivilClaim();

        assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

        assertEquals(claim.getVatClaimed().getCalculated(), claim.getVatClaimed().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getVatClaimed().getStatus());

        assertEquals(claim.getFixedFee().getCalculated(), claim.getFixedFee().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getFixedFee().getStatus());

        assertNull(claim.getNetProfitCost().getAmended());
        assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

        assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

        assertEquals(claim.getDisbursementVatAmount().getCalculated(), claim.getDisbursementVatAmount().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getDisbursementVatAmount().getStatus());

        assertEquals(claim.getDetentionTravelWaitingCosts().getCalculated(), claim.getDetentionTravelWaitingCosts().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getDetentionTravelWaitingCosts().getStatus());

        assertEquals(claim.getJrFormFillingCost().getCalculated(), claim.getJrFormFillingCost().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getJrFormFillingCost().getStatus());

        assertEquals(claim.getAdjournedHearing().getCalculated(), claim.getAdjournedHearing().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getAdjournedHearing().getStatus());

        assertEquals(claim.getCmrhTelephone().getCalculated(), claim.getCmrhTelephone().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getCmrhTelephone().getStatus());

        assertEquals(claim.getCmrhOral().getCalculated(), claim.getCmrhOral().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getCmrhOral().getStatus());

        assertEquals(claim.getHoInterview().getCalculated(), claim.getHoInterview().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getHoInterview().getStatus());

        assertEquals(claim.getSubstantiveHearing().getCalculated(), claim.getSubstantiveHearing().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getSubstantiveHearing().getStatus());

        assertEquals(claim.getCounselsCost().getCalculated(), claim.getCounselsCost().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getCounselsCost().getStatus());
    }

    @Test
    void testApplyReducedToFixedFeeOutcome_whenCrimeClaim() {
        CrimeClaimDetails claim = mockClaims.createMockCrimeClaim();

        assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

        assertEquals(claim.getVatClaimed().getCalculated(), claim.getVatClaimed().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getVatClaimed().getStatus());

        assertEquals(claim.getFixedFee().getCalculated(), claim.getFixedFee().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getFixedFee().getStatus());

        assertNull(claim.getNetProfitCost().getAmended());
        assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

        assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

        assertEquals(claim.getDisbursementVatAmount().getCalculated(), claim.getDisbursementVatAmount().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getDisbursementVatAmount().getStatus());

        assertEquals(claim.getTravelCosts().getCalculated(), claim.getTravelCosts().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getTravelCosts().getStatus());

        assertEquals(claim.getWaitingCosts().getCalculated(), claim.getWaitingCosts().getAmended());
        assertEquals(AmendStatus.AMENDABLE, claim.getWaitingCosts().getStatus());
    }
}