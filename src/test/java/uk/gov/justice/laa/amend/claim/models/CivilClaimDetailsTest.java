package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class CivilClaimDetailsTest {

    @Nested
    class GetIsCrimeClaimDetailsTests {

        @Test
        void getIsCrimeClaimReturnsFalse() {
            CivilClaimDetails claim = new CivilClaimDetails();
            Assertions.assertFalse(claim.getIsCrimeClaim());
        }
    }

    @Nested
    class SetNilledValuesTests {

        @Test
        void setsValuesToNilledStatus() {
            CivilClaimDetails claim = new CivilClaimDetails();

            claim.setNetProfitCost(new ClaimField());
            claim.setNetDisbursementAmount(new ClaimField());
            claim.setDisbursementVatAmount(new ClaimField());
            claim.setCounselsCost(new ClaimField());
            claim.setDetentionTravelWaitingCosts(new ClaimField());
            claim.setJrFormFillingCost(new ClaimField());
            claim.setAdjournedHearing(new ClaimField());
            claim.setCmrhTelephone(new ClaimField());
            claim.setCmrhOral(new ClaimField());
            claim.setHoInterview(new ClaimField());
            claim.setSubstantiveHearing(new ClaimField());
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

            Assertions.assertEquals(BigDecimal.ZERO, claim.getCounselsCost().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getCounselsCost().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getDetentionTravelWaitingCosts().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getDetentionTravelWaitingCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getJrFormFillingCost().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getJrFormFillingCost().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getAdjournedHearing().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getAdjournedHearing().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getCmrhTelephone().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getCmrhTelephone().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getCmrhOral().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getCmrhOral().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getHoInterview().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getHoInterview().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getSubstantiveHearing().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getSubstantiveHearing().getStatus());

            Assertions.assertNull(claim.getAssessedTotalVat().getAmended());
            Assertions.assertNull(claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAmended());
            Assertions.assertNull(claim.getAssessedTotalInclVat().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getAllowedTotalVat().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getAllowedTotalInclVat().getStatus());
        }
    }

    @Nested
    class SetReducedToFixedFeeTests {
        @Test
        void setsValuesToReducedToFixedFee() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setNetProfitCost(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setNetDisbursementAmount(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setDisbursementVatAmount(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setCounselsCost(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setJrFormFillingCost(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setAdjournedHearing(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setCmrhTelephone(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setCmrhOral(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setHoInterview(ClaimField.builder().calculated(BigDecimal.ONE).build());
            claim.setSubstantiveHearing(ClaimField.builder().calculated(BigDecimal.ONE).build());
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

            Assertions.assertEquals(BigDecimal.ONE, claim.getCounselsCost().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getCounselsCost().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getDetentionTravelWaitingCosts().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getDetentionTravelWaitingCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getJrFormFillingCost().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getJrFormFillingCost().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getAdjournedHearing().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getAdjournedHearing().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getCmrhTelephone().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getCmrhTelephone().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getCmrhOral().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getCmrhOral().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getHoInterview().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getHoInterview().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getSubstantiveHearing().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getSubstantiveHearing().getStatus());

            Assertions.assertNull(claim.getAssessedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAssessedTotalInclVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());
        }
    }

    @Nested
    class SetPaidInFullValuesTests {
        @Test
        void paidInFull() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setNetProfitCost(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setNetDisbursementAmount(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setDisbursementVatAmount(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setCounselsCost(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setJrFormFillingCost(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAdjournedHearing(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setCmrhTelephone(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setCmrhOral(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setHoInterview(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setSubstantiveHearing(ClaimField.builder().submitted(BigDecimal.ONE).build());
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

            Assertions.assertEquals(BigDecimal.ONE, claim.getCounselsCost().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getCounselsCost().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getDetentionTravelWaitingCosts().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getDetentionTravelWaitingCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getJrFormFillingCost().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getJrFormFillingCost().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getAdjournedHearing().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getAdjournedHearing().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getCmrhTelephone().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getCmrhTelephone().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getCmrhOral().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getCmrhOral().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getHoInterview().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getHoInterview().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getSubstantiveHearing().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getSubstantiveHearing().getStatus());

            Assertions.assertNull(claim.getAssessedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAssessedTotalInclVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());
        }
    }

    @Nested
    class SetReducedValuesTests {
        @Test
        void reduced() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setNetProfitCost(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setNetDisbursementAmount(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setDisbursementVatAmount(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setCounselsCost(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setJrFormFillingCost(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setAdjournedHearing(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setCmrhTelephone(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setCmrhOral(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setHoInterview(ClaimField.builder().submitted(BigDecimal.ONE).build());
            claim.setSubstantiveHearing(ClaimField.builder().submitted(BigDecimal.ONE).build());
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

            Assertions.assertEquals(BigDecimal.ONE, claim.getCounselsCost().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getCounselsCost().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getDetentionTravelWaitingCosts().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getDetentionTravelWaitingCosts().getStatus());

            Assertions.assertEquals(BigDecimal.ONE, claim.getJrFormFillingCost().getAmended());
            Assertions.assertEquals(AmendStatus.AMENDABLE, claim.getJrFormFillingCost().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getAdjournedHearing().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getAdjournedHearing().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getCmrhTelephone().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getCmrhTelephone().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getCmrhOral().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getCmrhOral().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getHoInterview().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getHoInterview().getStatus());

            Assertions.assertEquals(BigDecimal.ZERO, claim.getSubstantiveHearing().getAmended());
            Assertions.assertEquals(AmendStatus.NOT_AMENDABLE, claim.getSubstantiveHearing().getStatus());

            Assertions.assertNull(claim.getAssessedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAssessedTotalVat().getStatus());

            Assertions.assertNull(claim.getAssessedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAssessedTotalInclVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());

            Assertions.assertNull(claim.getAllowedTotalInclVat().getAmended());
            Assertions.assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());
        }
    }
}
