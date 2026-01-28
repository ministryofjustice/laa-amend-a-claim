package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

import java.math.BigDecimal;

public class CrimeClaimDetailsTest {

    @Nested
    class IsAssessedTotalFieldModifiableTests {

        @Test
        void returnsFalseWhenFeeCodeIsNull() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            Assertions.assertFalse(claim.isAssessedTotalFieldAssessable());
        }

        @Test
        void returnsTrueWhenFeeCodeIsINVC() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFeeCode("INVC");
            Assertions.assertTrue(claim.isAssessedTotalFieldAssessable());
        }

        @Test
        void returnsFalseWhenFeeCodeIsSomethingElse() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFeeCode("ABCD");
            Assertions.assertFalse(claim.isAssessedTotalFieldAssessable());
        }
    }

    @Nested
    class SetNilledValuesTests {

        @Test
        void setsValuesToNilledStatus() {
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();

            claim.applyOutcome(OutcomeType.NILLED);

            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getWaitingCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAssessedTotalVat().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalInclVat().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalVat().getAssessed());
        }
    }

    @Nested
    class SetPaidInFullValuesTests {

        @Test
        void paidInFull() {
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();

            claim.applyOutcome(OutcomeType.PAID_IN_FULL);

            Assertions.assertNull(claim.getFixedFee().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getWaitingCosts().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
        }
    }

    @Nested
    class SetReducedValuesTests {

        @Test
        void reduced() {
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();

            claim.applyOutcome(OutcomeType.REDUCED);

            Assertions.assertNull(claim.getFixedFee().getAssessed());
            Assertions.assertNull(claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(100), claim.getWaitingCosts().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
        }
    }

    @Nested
    class SetReducedToFixedFeeValuesTests {

        @Test
        void reducedToFixedFee() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFixedFee(MockClaimsFunctions.createFixedFeeField());
            claim.setNetProfitCost(MockClaimsFunctions.createNetProfitCostField());
            claim.setVatClaimed(MockClaimsFunctions.createVatClaimedField());
            claim.setNetDisbursementAmount(MockClaimsFunctions.createDisbursementCostField());
            claim.setDisbursementVatAmount(MockClaimsFunctions.createDisbursementVatCostField());
            claim.setTravelCosts(MockClaimsFunctions.createTravelCostField());
            claim.setWaitingCosts(MockClaimsFunctions.createWaitingCostField());
            claim.setAssessedTotalVat(MockClaimsFunctions.createAssessedTotalVatField());
            claim.setAssessedTotalInclVat(MockClaimsFunctions.createAssessedTotalInclVatField());
            claim.setAllowedTotalVat(MockClaimsFunctions.createAllowedTotalVatField());
            claim.setAllowedTotalInclVat(MockClaimsFunctions.createAllowedTotalInclVatField());


            claim.applyOutcome(OutcomeType.REDUCED_TO_FIXED_FEE);

            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getFixedFee().getAssessed());
            Assertions.assertNull(claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(false, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.valueOf(200), claim.getWaitingCosts().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
        }
    }
}
