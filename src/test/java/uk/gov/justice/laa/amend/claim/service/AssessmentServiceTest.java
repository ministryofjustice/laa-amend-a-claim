package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
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

    @BeforeEach
    void setUp() {
        assessmentService = new AssessmentService();
    }

    @Nested
    class NilledOutcome{
        @Test
        void testApplyNilledOutcome_SetsAllMonetaryFieldsToZero() {
            // Given: A claim with non-zero values
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();

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
        void testApplyNilledOutcome_CrimeClaimSpecificFields() {
            // Given: A crime claim with non-zero values
            CrimeClaimDetails claim = CreateMockClaims.createMockCrimeClaim();

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
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.REDUCED);
            claim.setNetProfitCost(CreateMockClaims.createClaimField());
            claim.setNetDisbursementAmount(CreateMockClaims.createClaimField());

            // When: Switching to NILLED outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

            // Then: Should apply NILLED logic and set values to zero
            assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NOT_AMENDABLE, claim.getNetProfitCost().getStatus());

            assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.NOT_AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }

        @Test
        void testApplyAssessmentOutcome_SwitchingFromPaidInFullToNilled() {
            // Given: A claim with PAID IN FULL outcome and custom values
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.PAID_IN_FULL);
            claim.setNetProfitCost(CreateMockClaims.createClaimField());
            claim.setNetDisbursementAmount(CreateMockClaims.createClaimField());

            // When: Switching to NILLED outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

            // Then: Should apply NILLED logic and set values to zero
            assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NOT_AMENDABLE, claim.getNetProfitCost().getStatus());

            assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.NOT_AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }

        @Test
        void testApplyNilledOutcome_AppliesWhenOutcomeChanges() {
            // Given: A claim with no outcome set
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setNetProfitCost(CreateMockClaims.createClaimField());

            // When: NILLED outcome is applied
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

            // Then: Amended value should be set to 0
            assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
        }

        @Test
        void testApplyNilledAssessmentOutcome_DoesNotApplyIfOutcomeUnchanged() {
            // Given: A claim with NILLED outcome already set
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.NILLED);
            claim.setNetProfitCost(CreateMockClaims.createClaimField());

            // When: Same outcome is applied again
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

            // Then: Amended value should remain unchanged
            assertEquals(BigDecimal.valueOf(300), claim.getNetProfitCost().getAmended());
        }
    }

    @Nested
    class ReducedToFixedFeeOutcome{
        @Test
        void testApplyReducedToFixedFeeOutcome_whenCrimeClaim() {
            CrimeClaimDetails claim = CreateMockClaims.createMockCrimeClaim();

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

        @Test
        void testApplyReducedToFixedFeeOutcome_whenCivilClaim() {
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();

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
        void testApplyAssessmentOutcome_SwitchingFromPaidInFullToReduced() {
            // Given: A claim with PAID IN FULL outcome and custom values
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.PAID_IN_FULL);
            claim.setNetProfitCost(CreateMockClaims.createClaimField());
            claim.setNetDisbursementAmount(CreateMockClaims.createClaimField());

            // When: Switching to REDUCED outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

            // Then: Should apply REDUCED and set values to zero
            assertNull(claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }

        @Test
        void testApplyAssessmentOutcome_SwitchingFromNilledToReduced() {
            // Given: A claim with NILLED outcome and custom values
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.NILLED);
            claim.setNetProfitCost(CreateMockClaims.createClaimField());
            claim.setNetDisbursementAmount(CreateMockClaims.createClaimField());

            // When: Switching to REDUCED outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

            // Then: Should apply REDUCED and set values to zero
            assertNull(claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }

        @Test
        void testApplyReducedOutcome_AppliesWhenOutcomeChanges() {
            // Given: A claim with no outcome set
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setNetProfitCost(CreateMockClaims.createClaimField());

            // When: NILLED outcome is applied
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

            // Then: Amended value should be set to calculated
            assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }

        @Test
        void testApplyReducedAssessmentOutcome_DoesNotApplyIfOutcomeUnchanged() {
            // Given: A claim with NILLED outcome already set
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.REDUCED_TO_FIXED_FEE);

            // When: Same outcome is applied again
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

            // Then: Amended value should remain unchanged
            assertEquals(BigDecimal.valueOf(300), claim.getNetDisbursementAmount().getAmended());
        }
    }

    @Nested
    class ReducedOutcome{
        @Test
        void testApplyReducedOutcome_whenCrimeClaim() {
            CrimeClaimDetails claim = CreateMockClaims.createMockCrimeClaim();

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED);

            assertEquals(claim.getVatClaimed().getSubmitted(), claim.getVatClaimed().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getVatClaimed().getStatus());

            assertEquals(claim.getFixedFee().getSubmitted(), claim.getFixedFee().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getFixedFee().getStatus());

            assertNull(claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getSubmitted(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            assertEquals(claim.getDisbursementVatAmount().getSubmitted(), claim.getDisbursementVatAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getDisbursementVatAmount().getStatus());

            assertEquals(claim.getTravelCosts().getSubmitted(), claim.getTravelCosts().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getTravelCosts().getStatus());

            assertEquals(claim.getWaitingCosts().getSubmitted(), claim.getWaitingCosts().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getWaitingCosts().getStatus());
        }

        @Test
        void testApplyReducedFeeOutcome_whenCivilClaim() {
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED);

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

            assertEquals(claim.getDetentionTravelWaitingCosts().getSubmitted(), claim.getDetentionTravelWaitingCosts().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getDetentionTravelWaitingCosts().getStatus());

            assertEquals(claim.getJrFormFillingCost().getSubmitted(), claim.getJrFormFillingCost().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getJrFormFillingCost().getStatus());

            assertEquals(BigDecimal.ZERO, claim.getAdjournedHearing().getAmended());
            assertEquals(AmendStatus.NOT_AMENDABLE, claim.getAdjournedHearing().getStatus());

            assertEquals(BigDecimal.ZERO, claim.getCmrhTelephone().getAmended());
            assertEquals(AmendStatus.NOT_AMENDABLE, claim.getCmrhTelephone().getStatus());

            assertEquals(BigDecimal.ZERO, claim.getCmrhOral().getAmended());
            assertEquals(AmendStatus.NOT_AMENDABLE, claim.getCmrhOral().getStatus());

            assertEquals(BigDecimal.ZERO, claim.getHoInterview().getAmended());
            assertEquals(AmendStatus.NOT_AMENDABLE, claim.getHoInterview().getStatus());

            assertEquals(BigDecimal.ZERO, claim.getSubstantiveHearing().getAmended());
            assertEquals(AmendStatus.NOT_AMENDABLE, claim.getSubstantiveHearing().getStatus());

            assertEquals(claim.getCounselsCost().getSubmitted(), claim.getCounselsCost().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getCounselsCost().getStatus());
        }

        @Test
        void testApplyAssessmentOutcome_SwitchingFromPaidInFullToReduced() {
            // Given: A claim with PAID IN FULL outcome and custom values
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.PAID_IN_FULL);
            claim.setNetProfitCost(CreateMockClaims.createClaimField());
            claim.setNetDisbursementAmount(CreateMockClaims.createClaimField());

            // When: Switching to REDUCED outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED);

            // Then: Should apply REDUCED and set values to zero
            assertNull(claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }

        @Test
        void testApplyAssessmentOutcome_SwitchingFromNilledToReduced() {
            // Given: A claim with NILLED outcome and custom values
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.NILLED);
            claim.setNetProfitCost(CreateMockClaims.createClaimField());
            claim.setNetDisbursementAmount(CreateMockClaims.createClaimField());

            // When: Switching to REDUCED outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED);

            // Then: Should apply REDUCED and set values to zero
            assertNull(claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }

        @Test
        void testApplyReducedOutcome_AppliesWhenOutcomeChanges() {
            // Given: A claim with no outcome set
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setNetProfitCost(CreateMockClaims.createClaimField());

            // When: NILLED outcome is applied
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED);

            // Then: Amended value should be set to calculated
            assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }

        @Test
        void testApplyReducedAssessmentOutcome_DoesNotApplyIfOutcomeUnchanged() {
            // Given: A claim with NILLED outcome already set
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.REDUCED);

            // When: Same outcome is applied again
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

            // Then: Amended value should remain unchanged
            assertEquals(BigDecimal.valueOf(200), claim.getNetDisbursementAmount().getAmended());
        }
    }

    @Nested
    class PaidInFullOutcome {
        @Test
        void testApplyPaidInFullOutcome_whenCrimeClaim() {
            CrimeClaimDetails claim = CreateMockClaims.createMockCrimeClaim();

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            assertEquals(claim.getVatClaimed().getSubmitted(), claim.getVatClaimed().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getVatClaimed().getStatus());

            assertEquals(claim.getFixedFee().getCalculated(), claim.getFixedFee().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getFixedFee().getStatus());

            assertNull(claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getSubmitted(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            assertEquals(claim.getDisbursementVatAmount().getSubmitted(), claim.getDisbursementVatAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getDisbursementVatAmount().getStatus());

            assertEquals(claim.getTravelCosts().getSubmitted(), claim.getTravelCosts().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getTravelCosts().getStatus());

            assertEquals(claim.getWaitingCosts().getSubmitted(), claim.getWaitingCosts().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getWaitingCosts().getStatus());
        }

        @Test
        void testApplyPaidInFullOutcome_whenCivilClaim() {
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            assertEquals(claim.getVatClaimed().getSubmitted(), claim.getVatClaimed().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getVatClaimed().getStatus());

            assertEquals(claim.getFixedFee().getCalculated(), claim.getFixedFee().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getFixedFee().getStatus());

            assertNull(claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getSubmitted(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            assertEquals(claim.getDisbursementVatAmount().getSubmitted(), claim.getDisbursementVatAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getDisbursementVatAmount().getStatus());

            assertEquals(claim.getDetentionTravelWaitingCosts().getSubmitted(), claim.getDetentionTravelWaitingCosts().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getDetentionTravelWaitingCosts().getStatus());

            assertEquals(claim.getJrFormFillingCost().getSubmitted(), claim.getJrFormFillingCost().getAmended());
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

            assertEquals(claim.getCounselsCost().getSubmitted(), claim.getCounselsCost().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getCounselsCost().getStatus());
        }

        @Test
        void testApplyAssessmentOutcome_SwitchingFromReducedToPaidInFull() {
            // Given: A claim with REDUCED outcome and custom values
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.REDUCED);
            claim.setNetProfitCost(CreateMockClaims.createClaimField());
            claim.setNetDisbursementAmount(CreateMockClaims.createClaimField());

            // When: Switching to PAID IN FULL outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            // Then: Should apply PAID IN FULL logic and set values to zero
            assertNull(claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getSubmitted(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }

        @Test
        void testApplyAssessmentOutcome_SwitchingFromNilledToPaidInFull() {
            // Given: A claim with NILLED outcome and custom values
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.NILLED);
            claim.setNetProfitCost(CreateMockClaims.createClaimField());
            claim.setNetDisbursementAmount(CreateMockClaims.createClaimField());

            // When: Switching to PAID IN FULL outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            // Then: Should apply PAID IN FULL logic and set values to zero
            assertNull(claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getSubmitted(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }

        @Test
        void testApplyPaidInFullOutcome_AppliesWhenOutcomeChanges() {
            // Given: A claim with no outcome set
            CivilClaimDetails claim = CreateMockClaims.createMockCivilClaim();
            claim.setNetProfitCost(CreateMockClaims.createClaimField());

            // When: PAID IN FULL outcome is applied
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            // Then: Amended value should be set to submitted
            assertEquals(claim.getNetDisbursementAmount().getSubmitted(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }
    }
}