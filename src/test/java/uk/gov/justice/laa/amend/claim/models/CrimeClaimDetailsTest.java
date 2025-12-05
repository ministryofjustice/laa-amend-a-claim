package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class CrimeClaimDetailsTest {

    @Nested
    class GetIsCrimeClaimDetailsTests {

        @Test
        void getIsCrimeClaimReturnsTrue() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            Assertions.assertTrue(claim.getIsCrimeClaim());
        }
    }

    @Nested
    class SetNilledValuesTests {

        @Test
        void setsValuesToNilledStatus() {
            CrimeClaimDetails claim = new CrimeClaimDetails();

            claim.setNetProfitCost(new ClaimField());
            claim.setNetDisbursementAmount(new ClaimField());
            claim.setDisbursementVatAmount(new ClaimField());
            claim.setTravelCosts(new ClaimField());
            claim.setWaitingCosts(new ClaimField());
            claim.setAssessedTotalInclVat(new ClaimField());
            claim.setAssessedTotalVat(new ClaimField());
            claim.setAllowedTotalInclVat(new ClaimField());
            claim.setAllowedTotalVat(new ClaimField());

            claim.setNilledValues();

            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getNetProfitCost().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getDisbursementVatAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getTravelCosts().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getTravelCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getWaitingCosts().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getWaitingCosts().getStatus());

            Assertions.assertNull(claim.getAssessedTotalVat().getAmended());
            Assertions.assertNull(claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAmended());
            Assertions.assertNull(claim.getAssessedTotalInclVat().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getAllowedTotalInclVat().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getAllowedTotalVat().getStatus());
        }
    }

    @Nested
    class SetPaidInFullValuesTests {

        @Test
        void paidInFull() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setNetProfitCost(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setNetDisbursementAmount(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setDisbursementVatAmount(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setTravelCosts(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setWaitingCosts(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAllowedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAllowedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());

            claim.setPaidInFullValues();

            Assertions.assertEquals(BigDecimal.ONE, claim.getNetProfitCost().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getNetProfitCost().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getDisbursementVatAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getTravelCosts().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getTravelCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getWaitingCosts().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getWaitingCosts().getStatus());

            Assertions.assertNull(claim.getAllowedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());
        }

        @Test
        void paidInFull_whenPoliceSchemeIsPresent() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setPoliceStationCourtPrisonId("Police");
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());

            claim.setPaidInFullValues();

            Assertions.assertNull(claim.getAssessedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAssessedTotalInclVat().getStatus());
        }

        @Test
        void paidInFull_whenPoliceSchemeIsNotPresent() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setPoliceStationCourtPrisonId(null);
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());

            claim.setPaidInFullValues();

            Assertions.assertNull(claim.getAssessedTotalVat().getAmended());
            Assertions.assertNull(claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAmended());
            Assertions.assertNull(claim.getAssessedTotalInclVat().getStatus());
        }
    }

    @Nested
    class SetReducedValuesTests {

        @Test
        void reduced() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setNetProfitCost(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setNetDisbursementAmount(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setDisbursementVatAmount(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setTravelCosts(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setWaitingCosts(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAllowedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAllowedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());

            claim.setReducedValues();

            Assertions.assertNull(claim.getNetProfitCost().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getDisbursementVatAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getTravelCosts().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getTravelCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getWaitingCosts().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getWaitingCosts().getStatus());

            Assertions.assertNull(claim.getAllowedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());
        }

        @Test
        void reduced_whenPoliceSchemeIsPresent() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setPoliceStationCourtPrisonId("Police");
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());

            claim.setReducedValues();

            Assertions.assertNull(claim.getAssessedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAssessedTotalInclVat().getStatus());
        }

        @Test
        void reduced_whenPoliceSchemeIsNotPresent() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setPoliceStationCourtPrisonId(null);
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());

            claim.setReducedValues();

            Assertions.assertNull(claim.getAssessedTotalVat().getAmended());
            Assertions.assertNull(claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAmended());
            Assertions.assertNull(claim.getAssessedTotalInclVat().getStatus());
        }
    }

    @Nested
    class SetReducedToFixedFeeValuesTests {

        @Test
        void reducedToFixedFee() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setNetProfitCost(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setNetDisbursementAmount(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setDisbursementVatAmount(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setTravelCosts(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setWaitingCosts(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setAssessedTotalVat(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setAllowedTotalVat(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setAllowedTotalInclVat(ClaimField.builder().calculated(BigDecimal.ONE).build());

            claim.setReducedToFixedFeeValues();

            Assertions.assertNull(claim.getNetProfitCost().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getNetProfitCost().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getNetDisbursementAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getDisbursementVatAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getTravelCosts().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getTravelCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getWaitingCosts().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getWaitingCosts().getStatus());

            Assertions.assertNull(claim.getAllowedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());
        }
    }
}
