package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ALLOWED_TOTAL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ASSESSED_TOTAL_INCL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ASSESSED_TOTAL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DISBURSEMENT_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_DISBURSEMENTS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TRAVEL_COSTS;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.WAITING_COSTS;

public class CrimeClaimDetailsTest {

    @Nested
    class IsAssessedTotalFieldModifiableTests {

        @Test
        void returnsFalseWhenFeeCodeIsNull() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            Assertions.assertFalse(claim.isAssessedTotalFieldModifiable());
        }

        @Test
        void returnsTrueWhenFeeCodeIsINVC() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFeeCode("INVC");
            Assertions.assertTrue(claim.isAssessedTotalFieldModifiable());
        }

        @Test
        void returnsFalseWhenFeeCodeIsSomethingElse() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFeeCode("ABCD");
            Assertions.assertFalse(claim.isAssessedTotalFieldModifiable());
        }
    }

    @Nested
    class IsBoltOnFieldTests {

        @Test
        void returnsFalse() {
            ClaimField field = ClaimField.builder().key(TRAVEL_COSTS).build();
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setTravelCosts(field);
            Assertions.assertFalse(claim.isBoltOnField(field));
        }
    }

    @Nested
    class SetNilledValuesTests {

        @Test
        void setsValuesToNilledStatus() {
            CrimeClaimDetails claim = new CrimeClaimDetails();

            claim.setNetProfitCost(ClaimField.builder().build());
            claim.setNetDisbursementAmount(ClaimField.builder().build());
            claim.setDisbursementVatAmount(ClaimField.builder().build());
            claim.setTravelCosts(ClaimField.builder().build());
            claim.setWaitingCosts(ClaimField.builder().build());
            claim.setAssessedTotalInclVat(ClaimField.builder().build());
            claim.setAssessedTotalVat(ClaimField.builder().build());
            claim.setAllowedTotalInclVat(ClaimField.builder().build());
            claim.setAllowedTotalVat(ClaimField.builder().build());

            claim.setNilledValues();

            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getWaitingCosts().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalInclVat().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalVat().getAssessed());
        }
    }

    @Nested
    class SetPaidInFullValuesTests {

        @Test
        void paidInFull() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            buildCrimeDetails(claim);

            claim.setPaidInFullValues();

            Assertions.assertNull(claim.getFixedFee().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getWaitingCosts().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
        }
    }

    @Nested
    class SetReducedValuesTests {

        @Test
        void reduced() {
            CrimeClaimDetails claim = new CrimeClaimDetails();

            buildCrimeDetails(claim);

            claim.setReducedValues();

            Assertions.assertNull(claim.getFixedFee().getAssessed());
            Assertions.assertNull(claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getWaitingCosts().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
        }
    }

    private static void buildCrimeDetails(CrimeClaimDetails claim) {
        claim.setFixedFee(ClaimField.builder().calculated(BigDecimal.ONE).key(FIXED_FEE).build());
        claim.setNetProfitCost(ClaimField.builder().submitted(BigDecimal.ONE).key(NET_PROFIT_COST).build());
        claim.setVatClaimed(ClaimField.builder().submitted(true).key(VAT).build());
        claim.setNetDisbursementAmount(ClaimField.builder().submitted(BigDecimal.ONE).key(NET_DISBURSEMENTS_COST).build());
        claim.setDisbursementVatAmount(ClaimField.builder().submitted(BigDecimal.ONE).key(DISBURSEMENT_VAT).build());
        claim.setTravelCosts(ClaimField.builder().submitted(BigDecimal.ONE).key(TRAVEL_COSTS).build());
        claim.setWaitingCosts(ClaimField.builder().submitted(BigDecimal.ONE).key(WAITING_COSTS).build());
        claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).key(ASSESSED_TOTAL_VAT).build());
        claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).key(ASSESSED_TOTAL_INCL_VAT).build());
        claim.setAllowedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).key(ALLOWED_TOTAL_VAT).build());
        claim.setAllowedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).key(ALLOWED_TOTAL_INCL_VAT).build());
    }

    @Nested
    class SetReducedToFixedFeeValuesTests {

        @Test
        void reducedToFixedFee() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFixedFee(ClaimField.builder().key(FIXED_FEE).calculated(BigDecimal.ONE).build());
            claim.setNetProfitCost(ClaimField.builder().key(NET_PROFIT_COST).calculated(BigDecimal.ONE).build());
            claim.setVatClaimed(ClaimField.builder().key(VAT).calculated(true).build());
            claim.setNetDisbursementAmount(ClaimField.builder().key(NET_DISBURSEMENTS_COST).calculated(BigDecimal.ONE).build());
            claim.setDisbursementVatAmount(ClaimField.builder().key(DISBURSEMENT_VAT).calculated(BigDecimal.ONE).build());
            claim.setTravelCosts(ClaimField.builder().key(TRAVEL_COSTS).calculated(BigDecimal.ONE).build());
            claim.setWaitingCosts(ClaimField.builder().key(WAITING_COSTS).calculated(BigDecimal.ONE).build());
            claim.setAssessedTotalVat(ClaimField.builder().key(ASSESSED_TOTAL_VAT).calculated(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).calculated(BigDecimal.ONE).build());
            claim.setAllowedTotalVat(ClaimField.builder().key(ALLOWED_TOTAL_VAT).calculated(BigDecimal.ONE).build());
            claim.setAllowedTotalInclVat(ClaimField.builder().key(ALLOWED_TOTAL_INCL_VAT).calculated(BigDecimal.ONE).build());


            claim.setReducedToFixedFeeValues();

            Assertions.assertEquals(BigDecimal.ONE, claim.getFixedFee().getAssessed());
            Assertions.assertNull(claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getWaitingCosts().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
        }
    }
}
