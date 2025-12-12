package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;

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
    private final ClaimStatusHandler claimStatusHandler = new ClaimStatusHandler();

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
            claimStatusHandler.updateFieldStatuses(claim, OutcomeType.NILLED);

            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(AssessmentStatus.NOT_ASSESSABLE, claim.getNetProfitCost().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(AssessmentStatus.NOT_ASSESSABLE, claim.getNetDisbursementAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(AssessmentStatus.NOT_ASSESSABLE, claim.getDisbursementVatAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(AssessmentStatus.NOT_ASSESSABLE, claim.getTravelCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getWaitingCosts().getAssessed());
            Assertions.assertEquals(AssessmentStatus.NOT_ASSESSABLE, claim.getWaitingCosts().getStatus());

            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.DO_NOT_DISPLAY, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.DO_NOT_DISPLAY, claim.getAssessedTotalInclVat().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalInclVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.NOT_ASSESSABLE, claim.getAllowedTotalInclVat().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.NOT_ASSESSABLE, claim.getAllowedTotalVat().getStatus());
        }
    }

    @Nested
    class SetPaidInFullValuesTests {

        @Test
        void paidInFull() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFixedFee(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setNetProfitCost(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setVatClaimed(ClaimField.builder().submitted(true).build());
            claim.setNetDisbursementAmount(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setDisbursementVatAmount(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setTravelCosts(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setWaitingCosts(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAllowedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAllowedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());

            claim.setPaidInFullValues();
            claimStatusHandler.updateFieldStatuses(claim, OutcomeType.PAID_IN_FULL);

            Assertions.assertNull(claim.getFixedFee().getAssessed());
            Assertions.assertEquals(AssessmentStatus.NOT_ASSESSABLE, claim.getFixedFee().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getNetProfitCost().getStatus());

            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getVatClaimed().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getNetDisbursementAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getDisbursementVatAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getTravelCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getWaitingCosts().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getWaitingCosts().getStatus());

            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getAllowedTotalVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getAllowedTotalInclVat().getStatus());
        }

        @Test
        void paidInFull_whenFeeCodeIsInListOfAssessedValueFeeCodes() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFeeCode("INVC");
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());

            claim.setPaidInFullValues();
            claimStatusHandler.updateFieldStatuses(claim, OutcomeType.PAID_IN_FULL);

            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getAssessedTotalInclVat().getStatus());
        }

        @Test
        void paidInFull_whenFeeCodeIsNotInListOfAssessedValueFeeCodes() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFeeCode("ABCD");
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());

            claim.setPaidInFullValues();
            claimStatusHandler.updateFieldStatuses(claim, OutcomeType.PAID_IN_FULL);

            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.DO_NOT_DISPLAY, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.DO_NOT_DISPLAY, claim.getAssessedTotalInclVat().getStatus());
        }

        @Test
        void paidInFull_whenFeeCodeIsNull() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFeeCode(null);
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());

            claim.setPaidInFullValues();
            claimStatusHandler.updateFieldStatuses(claim, OutcomeType.PAID_IN_FULL);
            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.DO_NOT_DISPLAY, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.DO_NOT_DISPLAY, claim.getAssessedTotalInclVat().getStatus());
        }
    }

    @Nested
    class SetReducedValuesTests {

        @Test
        void reduced() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFixedFee(ClaimField.builder().key(FIXED_FEE).submitted(BigDecimal.ONE).build());
            claim.setNetProfitCost(ClaimField.builder().key(NET_PROFIT_COST).submitted(BigDecimal.ONE).build());
            claim.setVatClaimed(ClaimField.builder().key(VAT).submitted(true).build());
            claim.setNetDisbursementAmount(ClaimField.builder().key(NET_DISBURSEMENTS_COST).submitted(BigDecimal.ONE).build());
            claim.setDisbursementVatAmount(ClaimField.builder().key(DISBURSEMENT_VAT).submitted(BigDecimal.ONE).build());
            claim.setTravelCosts(ClaimField.builder().key(TRAVEL_COSTS).submitted(BigDecimal.ONE).build());
            claim.setWaitingCosts(ClaimField.builder().key(WAITING_COSTS).submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalVat(ClaimField.builder().key(ASSESSED_TOTAL_VAT).submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).submitted(BigDecimal.ONE).build());
            claim.setAllowedTotalVat(ClaimField.builder().key(ALLOWED_TOTAL_VAT).submitted(BigDecimal.ONE).build());
            claim.setAllowedTotalInclVat(ClaimField.builder().key(ALLOWED_TOTAL_INCL_VAT).submitted(BigDecimal.ONE).build());

            claim.setReducedValues();
            claimStatusHandler.updateFieldStatuses(claim, OutcomeType.REDUCED);

            Assertions.assertNull(claim.getFixedFee().getAssessed());
            Assertions.assertEquals(AssessmentStatus.NOT_ASSESSABLE, claim.getFixedFee().getStatus());

            Assertions.assertNull(claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getNetProfitCost().getStatus());

            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getVatClaimed().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getNetDisbursementAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getDisbursementVatAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getTravelCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getWaitingCosts().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getWaitingCosts().getStatus());

            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getAllowedTotalVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getAllowedTotalInclVat().getStatus());
        }

        @Test
        void reduced_whenFeeCodeIsInListOfAssessedValueFeeCodes() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFeeCode("INVC");
            claim.setAssessedTotalVat(ClaimField.builder().key(ASSESSED_TOTAL_VAT).submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).submitted(BigDecimal.ONE).build());

            claim.setReducedValues();
            claimStatusHandler.updateFieldStatuses(claim, OutcomeType.REDUCED);

            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getAssessedTotalInclVat().getStatus());
        }

        @Test
        void reduced_whenFeeCodeIsNotInListOfAssessedValueFeeCodes() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFeeCode("ABCD");
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).build());

            claim.setReducedValues();
            claimStatusHandler.updateFieldStatuses(claim, OutcomeType.REDUCED);

            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.DO_NOT_DISPLAY, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.DO_NOT_DISPLAY, claim.getAssessedTotalInclVat().getStatus());
        }

        @Test
        void reduced_whenFeeCodeIsNull() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFeeCode(null);
            claim.setAssessedTotalVat(ClaimField.builder().key(ASSESSED_TOTAL_VAT).submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).submitted(BigDecimal.ONE).build());

            claim.setReducedValues();
            claimStatusHandler.updateFieldStatuses(claim, OutcomeType.REDUCED);

            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.DO_NOT_DISPLAY, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.DO_NOT_DISPLAY, claim.getAssessedTotalInclVat().getStatus());
        }
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
            claimStatusHandler.updateFieldStatuses(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

            Assertions.assertEquals(BigDecimal.ONE, claim.getFixedFee().getAssessed());
            Assertions.assertEquals(AssessmentStatus.NOT_ASSESSABLE, claim.getFixedFee().getStatus());

            Assertions.assertNull(claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getNetProfitCost().getStatus());

            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getVatClaimed().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getNetDisbursementAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getDisbursementVatAmount().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getTravelCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getWaitingCosts().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getWaitingCosts().getStatus());

            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getAllowedTotalVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
            Assertions.assertEquals(AssessmentStatus.ASSESSABLE, claim.getAllowedTotalInclVat().getStatus());
        }
    }
}
