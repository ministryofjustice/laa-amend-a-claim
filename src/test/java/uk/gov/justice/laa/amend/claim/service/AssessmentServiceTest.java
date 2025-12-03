package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.models.AmendStatus;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssessmentServiceTest {

    @Mock
    private ClaimsApiClient claimsApiClient;

    @Mock
    private AssessmentMapper assessmentMapper;

    @InjectMocks
    private AssessmentService assessmentService;

    public AssessmentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class NilledOutcome{
        @Test
        void testApplyNilledOutcome_SetsAllMonetaryFieldsToZero() {
            // Given: A claim with non-zero values
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

            // When: NILLED outcome is applied
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

            // Then: Only amendable monetary fields should be set to 0 (not VAT, Total, or Fixed Fee)
            assertEquals(BigDecimal.valueOf(300), claim.getFixedFee().getAmended()); // Fixed Fee unchanged (NA)
            assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
            assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
            assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAmended());
        }

        @Test
        void testApplyNilledOutcome_CrimeClaimSpecificFields() {
            // Given: A crime claim with non-zero values
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();

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
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.REDUCED);
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());
            claim.setNetDisbursementAmount(MockClaimsFunctions.createClaimField());

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
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.PAID_IN_FULL);
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());
            claim.setNetDisbursementAmount(MockClaimsFunctions.createClaimField());

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
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());

            // When: NILLED outcome is applied
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

            // Then: Amended value should be set to 0
            assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
        }

        @Test
        void testApplyNilledAssessmentOutcome_DoesNotApplyIfOutcomeUnchanged() {
            // Given: A claim with NILLED outcome already set
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.NILLED);
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());

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
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();

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
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

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
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.PAID_IN_FULL);
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());
            claim.setNetDisbursementAmount(MockClaimsFunctions.createClaimField());

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
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.NILLED);
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());
            claim.setNetDisbursementAmount(MockClaimsFunctions.createClaimField());

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
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());

            // When: NILLED outcome is applied
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

            // Then: Amended value should be set to calculated
            assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());
        }

        @Test
        void testApplyReducedAssessmentOutcome_DoesNotApplyIfOutcomeUnchanged() {
            // Given: A claim with NILLED outcome already set
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
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
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();

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

            assertNull(claim.getAllowedTotalInclVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

            assertNull(claim.getAllowedTotalVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
        }

        @Test
        void testApplyReducedFeeOutcome_whenCivilClaim() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

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

            assertNull(claim.getAllowedTotalInclVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

            assertNull(claim.getAllowedTotalVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
        }

        @Test
        void testApplyAssessmentOutcome_SwitchingFromPaidInFullToReduced() {
            // Given: A claim with PAID IN FULL outcome and custom values
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.PAID_IN_FULL);
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());
            claim.setNetDisbursementAmount(MockClaimsFunctions.createClaimField());

            // When: Switching to REDUCED outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED);

            // Then: Should apply REDUCED and set values to zero
            assertNull(claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            assertNull(claim.getAllowedTotalInclVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

            assertNull(claim.getAllowedTotalVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
        }

        @Test
        void testApplyAssessmentOutcome_SwitchingFromNilledToReduced() {
            // Given: A claim with NILLED outcome and custom values
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.NILLED);
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());
            claim.setNetDisbursementAmount(MockClaimsFunctions.createClaimField());

            // When: Switching to REDUCED outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED);

            // Then: Should apply REDUCED and set values to zero
            assertNull(claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            assertNull(claim.getAllowedTotalInclVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

            assertNull(claim.getAllowedTotalVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
        }

        @Test
        void testApplyReducedOutcome_AppliesWhenOutcomeChanges() {
            // Given: A claim with no outcome set
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());

            // When: NILLED outcome is applied
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED);

            // Then: Amended value should be set to calculated
            assertEquals(claim.getNetDisbursementAmount().getCalculated(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            assertNull(claim.getAllowedTotalInclVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

            assertNull(claim.getAllowedTotalVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
        }

        @Test
        void testApplyReducedAssessmentOutcome_DoesNotApplyIfOutcomeUnchanged() {
            // Given: A claim with NILLED outcome already set
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.REDUCED);

            // When: Same outcome is applied again
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

            // Then: Amended value should remain unchanged
            assertEquals(BigDecimal.valueOf(200), claim.getNetDisbursementAmount().getAmended());

            assertNull(claim.getAllowedTotalInclVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

            assertNull(claim.getAllowedTotalVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
        }
    }

    @Nested
    class PaidInFullOutcome {
        @Test
        void testApplyPaidInFullOutcome_whenCrimeClaim() {
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            assertEquals(claim.getVatClaimed().getSubmitted(), claim.getVatClaimed().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getVatClaimed().getStatus());

            assertEquals(claim.getFixedFee().getCalculated(), claim.getFixedFee().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getFixedFee().getStatus());

            assertEquals(claim.getNetProfitCost().getSubmitted(), claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getSubmitted(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            assertEquals(claim.getDisbursementVatAmount().getSubmitted(), claim.getDisbursementVatAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getDisbursementVatAmount().getStatus());

            assertEquals(claim.getTravelCosts().getSubmitted(), claim.getTravelCosts().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getTravelCosts().getStatus());

            assertEquals(claim.getWaitingCosts().getSubmitted(), claim.getWaitingCosts().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getWaitingCosts().getStatus());

            assertNull(claim.getAllowedTotalInclVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

            assertNull(claim.getAllowedTotalVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
        }

        @Test
        void testApplyPaidInFullOutcome_whenCivilClaim() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            assertEquals(claim.getVatClaimed().getSubmitted(), claim.getVatClaimed().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getVatClaimed().getStatus());

            assertEquals(claim.getFixedFee().getCalculated(), claim.getFixedFee().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getFixedFee().getStatus());

            assertEquals(claim.getNetProfitCost().getSubmitted(), claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetProfitCost().getStatus());

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

            assertNull(claim.getAllowedTotalInclVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

            assertNull(claim.getAllowedTotalVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
        }

        @Test
        void testApplyAssessmentOutcome_SwitchingFromReducedToPaidInFull() {
            // Given: A claim with REDUCED outcome and custom values
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.REDUCED);
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());
            claim.setNetDisbursementAmount(MockClaimsFunctions.createClaimField());

            // When: Switching to PAID IN FULL outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            // Then: Should apply PAID IN FULL logic and set values to zero
            assertEquals(claim.getNetProfitCost().getSubmitted(), claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getSubmitted(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            assertNull(claim.getAllowedTotalInclVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

            assertNull(claim.getAllowedTotalVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
        }

        @Test
        void testApplyAssessmentOutcome_SwitchingFromNilledToPaidInFull() {
            // Given: A claim with NILLED outcome and custom values
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAssessmentOutcome(OutcomeType.NILLED);
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());
            claim.setNetDisbursementAmount(MockClaimsFunctions.createClaimField());

            // When: Switching to PAID IN FULL outcome
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            // Then: Should apply PAID IN FULL logic and set values to zero
            assertEquals(claim.getNetProfitCost().getSubmitted(), claim.getNetProfitCost().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetProfitCost().getStatus());

            assertEquals(claim.getNetDisbursementAmount().getSubmitted(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            assertNull(claim.getAllowedTotalInclVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

            assertNull(claim.getAllowedTotalVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
        }

        @Test
        void testApplyPaidInFullOutcome_AppliesWhenOutcomeChanges() {
            // Given: A claim with no outcome set
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setNetProfitCost(MockClaimsFunctions.createClaimField());

            // When: PAID IN FULL outcome is applied
            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            // Then: Amended value should be set to submitted
            assertEquals(claim.getNetDisbursementAmount().getSubmitted(), claim.getNetDisbursementAmount().getAmended());
            assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            assertNull(claim.getAllowedTotalInclVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

            assertNull(claim.getAllowedTotalVat().getAmended());
            assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
        }
    }

    @Nested
    class SubmitAssessmentTests {

        @Test
        void testCivilClaimAssessmentSubmittedToApi() {
            String claimId = UUID.randomUUID().toString();
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setClaimId(claimId);
            String userId = UUID.randomUUID().toString();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCivilClaimToAssessment(claim, userId))
                .thenReturn(assessment);

            ResponseEntity<CreateAssessment201Response> response = ResponseEntity.ok(new CreateAssessment201Response());

            when(claimsApiClient.submitAssessment(claimId, assessment))
                .thenReturn(Mono.just(response));

            CreateAssessment201Response result =
                assessmentService.submitAssessment(claim, userId);

            Assertions.assertNotNull(result);
            assertEquals(response.getBody(), result);

            verify(assessmentMapper).mapCivilClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);
        }

        @Test
        void testCrimeClaimAssessmentSubmittedToApi() {
            String claimId = UUID.randomUUID().toString();
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();
            claim.setClaimId(claimId);
            String userId = UUID.randomUUID().toString();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCrimeClaimToAssessment(claim, userId))
                .thenReturn(assessment);

            ResponseEntity<CreateAssessment201Response> response = ResponseEntity.ok(new CreateAssessment201Response());

            when(claimsApiClient.submitAssessment(claimId, assessment))
                .thenReturn(Mono.just(response));

            CreateAssessment201Response result =
                assessmentService.submitAssessment(claim, userId);

            Assertions.assertNotNull(result);
            assertEquals(response.getBody(), result);

            verify(assessmentMapper).mapCrimeClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);
        }

        @Test
        void testWhenApiReturnsEmpty() {
            String claimId = UUID.randomUUID().toString();
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setClaimId(claimId);
            String userId = UUID.randomUUID().toString();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCivilClaimToAssessment(claim, userId))
                .thenReturn(assessment);

            when(claimsApiClient.submitAssessment(claimId, assessment))
                .thenReturn(Mono.empty());

            assertThrows(
                RuntimeException.class,
                () -> assessmentService.submitAssessment(claim, userId)
            );

            verify(assessmentMapper).mapCivilClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);
        }

        @Test
        void testWhenApiReturnsNullBody() {
            String claimId = UUID.randomUUID().toString();
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setClaimId(claimId);
            String userId = UUID.randomUUID().toString();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCivilClaimToAssessment(claim, userId))
                .thenReturn(assessment);

            when(claimsApiClient.submitAssessment(claimId, assessment))
                .thenReturn(Mono.just(ResponseEntity.ok(null)));

            assertThrows(
                RuntimeException.class,
                () -> assessmentService.submitAssessment(claim, userId)
            );

            verify(assessmentMapper).mapCivilClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);
        }
    }
}