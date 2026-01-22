package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

import java.math.BigDecimal;

public class CivilClaimDetailsTest {

    @Nested
    class IsAssessedTotalFieldModifiableTests {

        @Test
        void returnsFalse() {
            CivilClaimDetails claim = new CivilClaimDetails();
            Assertions.assertFalse(claim.isAssessedTotalFieldAssessable());
        }
    }

    @Nested
    class SetNilledValuesTests {

        @Test
        void setsValuesToNilledStatus() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

            claim.applyOutcome(OutcomeType.NILLED);

            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getCounselsCost().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getDetentionTravelWaitingCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getJrFormFillingCost().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAdjournedHearing().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getCmrhTelephone().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getCmrhOral().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getHoInterview().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getSubstantiveHearing().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalVat().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalInclVat().getAssessed());
        }
    }

    @Nested
    class SetReducedToFixedFeeTests {
        @Test
        void setsValuesToReducedToFixedFee() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

            claim.applyOutcome(OutcomeType.REDUCED_TO_FIXED_FEE);

            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getFixedFee().getAssessed());
            Assertions.assertNull(claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(false, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getCounselsCost().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getDetentionTravelWaitingCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getJrFormFillingCost().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getAdjournedHearing().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getCmrhTelephone().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getCmrhOral().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getHoInterview().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getSubstantiveHearing().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
        }
    }

    @Nested
    class SetPaidInFullValuesTests {
        @Test
        void paidInFull() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

            claim.applyOutcome(OutcomeType.PAID_IN_FULL);

            Assertions.assertNull(claim.getFixedFee().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getCounselsCost().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getDetentionTravelWaitingCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getJrFormFillingCost().getAssessed());
            Assertions.assertNull(claim.getAdjournedHearing().getAssessed());
            Assertions.assertNull(claim.getCmrhTelephone().getAssessed());
            Assertions.assertNull(claim.getCmrhOral().getAssessed());
            Assertions.assertNull(claim.getHoInterview().getAssessed());
            Assertions.assertNull(claim.getSubstantiveHearing().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
        }
    }

    @Nested
    class SetReducedValuesTests {
        @Test
        void reduced() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

            claim.applyOutcome(OutcomeType.REDUCED);

            Assertions.assertNull(claim.getFixedFee().getAssessed());
            Assertions.assertNull(claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getCounselsCost().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getDetentionTravelWaitingCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getJrFormFillingCost().getAssessed());
            Assertions.assertNull(claim.getAdjournedHearing().getAssessed());
            Assertions.assertNull(claim.getCmrhTelephone().getAssessed());
            Assertions.assertNull(claim.getCmrhOral().getAssessed());
            Assertions.assertNull(claim.getHoInterview().getAssessed());
            Assertions.assertNull(claim.getSubstantiveHearing().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
        }
    }
}
