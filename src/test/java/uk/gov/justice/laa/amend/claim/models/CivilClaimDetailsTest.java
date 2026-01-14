package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ALLOWED_TOTAL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ASSESSED_TOTAL_INCL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ASSESSED_TOTAL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.COUNSELS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DETENTION_TRAVEL_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DISBURSEMENT_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.JR_FORM_FILLING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_DISBURSEMENTS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.VAT;

public class CivilClaimDetailsTest {

    @Nested
    class IsAssessedTotalFieldModifiableTests {

        @Test
        void returnsFalse() {
            CivilClaimDetails claim = new CivilClaimDetails();
            Assertions.assertFalse(claim.isAssessedTotalFieldModifiable());
        }
    }

    @Nested
    class SetNilledValuesTests {

        @Test
        void setsValuesToNilledStatus() {
            CivilClaimDetails claim = new CivilClaimDetails();

            claim.setNetProfitCost(ClaimField.builder().key(NET_PROFIT_COST).build());
            claim.setNetDisbursementAmount(ClaimField.builder().build());
            claim.setDisbursementVatAmount(ClaimField.builder().build());
            claim.setCounselsCost(ClaimField.builder().build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().build());
            claim.setJrFormFillingCost(ClaimField.builder().build());
            claim.setAdjournedHearing(ClaimField.builder().build());
            claim.setCmrhTelephone(ClaimField.builder().build());
            claim.setCmrhOral(ClaimField.builder().build());
            claim.setHoInterview(ClaimField.builder().build());
            claim.setSubstantiveHearing(ClaimField.builder().build());
            claim.setAssessedTotalInclVat(ClaimField.builder().build());
            claim.setAssessedTotalVat(ClaimField.builder().build());
            claim.setAllowedTotalInclVat(ClaimField.builder().build());
            claim.setAllowedTotalVat(ClaimField.builder().build());

            claim.setNilledValues();

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
            CivilClaimDetails claim = new CivilClaimDetails();
            buildCivilClaim(claim);

            claim.setReducedToFixedFeeValues();

            Assertions.assertEquals(BigDecimal.ONE, claim.getFixedFee().getAssessed());
            Assertions.assertNull(claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getCounselsCost().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getDetentionTravelWaitingCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getJrFormFillingCost().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getAdjournedHearing().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getCmrhTelephone().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getCmrhOral().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getHoInterview().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getSubstantiveHearing().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAssessedTotalInclVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalVat().getAssessed());
            Assertions.assertNull(claim.getAllowedTotalInclVat().getAssessed());
        }
    }

    private static void buildCivilClaim(CivilClaimDetails claim) {
        claim.setFixedFee(ClaimField.builder().key(FIXED_FEE).calculated(BigDecimal.ONE).build());
        claim.setNetProfitCost(ClaimField.builder().key(NET_PROFIT_COST).calculated(BigDecimal.ONE).build());
        claim.setVatClaimed(ClaimField.builder().key(VAT).calculated(true).build());
        claim.setNetDisbursementAmount(ClaimField.builder().key(NET_DISBURSEMENTS_COST).calculated(BigDecimal.ONE).build());
        claim.setDisbursementVatAmount(ClaimField.builder().key(DISBURSEMENT_VAT).calculated(BigDecimal.ONE).build());
        claim.setCounselsCost(ClaimField.builder().key(COUNSELS_COST).calculated(BigDecimal.ONE).build());
        claim.setDetentionTravelWaitingCosts(ClaimField.builder().key(DETENTION_TRAVEL_COST).calculated(BigDecimal.ONE).build());
        claim.setJrFormFillingCost(ClaimField.builder().key(JR_FORM_FILLING).calculated(BigDecimal.ONE).build());
        claim.setAdjournedHearing(ClaimField.builder().key(ADJOURNED_FEE).calculated(BigDecimal.ONE).build());
        claim.setCmrhTelephone(ClaimField.builder().key(CMRH_TELEPHONE).calculated(BigDecimal.ONE).build());
        claim.setCmrhOral(ClaimField.builder().key(CMRH_ORAL).calculated(BigDecimal.ONE).build());
        claim.setHoInterview(ClaimField.builder().key(HO_INTERVIEW).calculated(BigDecimal.ONE).build());
        claim.setSubstantiveHearing(ClaimField.builder().key(SUBSTANTIVE_HEARING).calculated(BigDecimal.ONE).build());
        claim.setAssessedTotalVat(ClaimField.builder().key(ASSESSED_TOTAL_VAT).calculated(BigDecimal.ONE).build());
        claim.setAssessedTotalInclVat(ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).calculated(BigDecimal.ONE).build());
        claim.setAllowedTotalVat(ClaimField.builder().key(ALLOWED_TOTAL_VAT).calculated(BigDecimal.ONE).build());
        claim.setAllowedTotalInclVat(ClaimField.builder().key(ALLOWED_TOTAL_INCL_VAT).calculated(BigDecimal.ONE).build());
    }

    @Nested
    class SetPaidInFullValuesTests {
        @Test
        void paidInFull() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(ClaimField.builder().calculated(BigDecimal.ONE).key(FIXED_FEE).build());
            claim.setNetProfitCost(ClaimField.builder().submitted(BigDecimal.ONE).key(NET_PROFIT_COST).build());
            claim.setVatClaimed(ClaimField.builder().submitted(true).key(VAT).build());
            claim.setNetDisbursementAmount(ClaimField.builder().submitted(BigDecimal.ONE).key(NET_DISBURSEMENTS_COST).build());
            claim.setDisbursementVatAmount(ClaimField.builder().submitted(BigDecimal.ONE).key(DISBURSEMENT_VAT).build());
            claim.setCounselsCost(ClaimField.builder().submitted(BigDecimal.ONE).key(COUNSELS_COST).build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().submitted(BigDecimal.ONE).key(DETENTION_TRAVEL_COST).build());
            claim.setJrFormFillingCost(ClaimField.builder().submitted(BigDecimal.ONE).key(JR_FORM_FILLING).build());
            claim.setAdjournedHearing(ClaimField.builder().submitted(BigDecimal.ONE).key(ADJOURNED_FEE).build());
            claim.setCmrhTelephone(ClaimField.builder().submitted(BigDecimal.ONE).key(CMRH_TELEPHONE).build());
            claim.setCmrhOral(ClaimField.builder().submitted(BigDecimal.ONE).key(CMRH_ORAL).build());
            claim.setHoInterview(ClaimField.builder().submitted(BigDecimal.ONE).key(HO_INTERVIEW).build());
            claim.setSubstantiveHearing(ClaimField.builder().submitted(BigDecimal.ONE).key(SUBSTANTIVE_HEARING).build());
            claim.setAssessedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).key(ASSESSED_TOTAL_VAT).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).key(ASSESSED_TOTAL_INCL_VAT).build());
            claim.setAllowedTotalVat(ClaimField.builder().submitted(BigDecimal.ONE).key(ALLOWED_TOTAL_VAT).build());
            claim.setAllowedTotalInclVat(ClaimField.builder().submitted(BigDecimal.ONE).key(ALLOWED_TOTAL_INCL_VAT).build());

            claim.setPaidInFullValues();

            Assertions.assertNull(claim.getFixedFee().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getCounselsCost().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getDetentionTravelWaitingCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getJrFormFillingCost().getAssessed());
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
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(ClaimField.builder().key(FIXED_FEE).submitted(BigDecimal.ONE).build());
            claim.setNetProfitCost(ClaimField.builder().key(NET_PROFIT_COST).submitted(BigDecimal.ONE).build());
            claim.setVatClaimed(ClaimField.builder().key(VAT).submitted(true).build());
            claim.setNetDisbursementAmount(ClaimField.builder().key(NET_DISBURSEMENTS_COST).submitted(BigDecimal.ONE).build());
            claim.setDisbursementVatAmount(ClaimField.builder().key(DISBURSEMENT_VAT).submitted(BigDecimal.ONE).build());
            claim.setCounselsCost(ClaimField.builder().key(COUNSELS_COST).submitted(BigDecimal.ONE).build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().key(DETENTION_TRAVEL_COST).submitted(BigDecimal.ONE).build());
            claim.setJrFormFillingCost(ClaimField.builder().key(JR_FORM_FILLING).submitted(BigDecimal.ONE).build());
            claim.setAdjournedHearing(ClaimField.builder().key(ADJOURNED_FEE).submitted(BigDecimal.ONE).build());
            claim.setCmrhTelephone(ClaimField.builder().key(CMRH_TELEPHONE).submitted(BigDecimal.ONE).build());
            claim.setCmrhOral(ClaimField.builder().key(CMRH_ORAL).submitted(BigDecimal.ONE).build());
            claim.setHoInterview(ClaimField.builder().key(HO_INTERVIEW).submitted(BigDecimal.ONE).build());
            claim.setSubstantiveHearing(ClaimField.builder().key(SUBSTANTIVE_HEARING).submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalVat(ClaimField.builder().key(ASSESSED_TOTAL_VAT).submitted(BigDecimal.ONE).build());
            claim.setAssessedTotalInclVat(ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).submitted(BigDecimal.ONE).build());
            claim.setAllowedTotalVat(ClaimField.builder().key(ALLOWED_TOTAL_VAT).submitted(BigDecimal.ONE).build());
            claim.setAllowedTotalInclVat(ClaimField.builder().key(ALLOWED_TOTAL_INCL_VAT).submitted(BigDecimal.ONE).build());

            claim.setReducedValues();

            Assertions.assertNull(claim.getFixedFee().getAssessed());
            Assertions.assertNull(claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(true, claim.getVatClaimed().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getCounselsCost().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getDetentionTravelWaitingCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ONE, claim.getJrFormFillingCost().getAssessed());
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
