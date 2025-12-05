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
            claim.setAllowedTotalInclVat(new ClaimField());
            claim.setAllowedTotalVat(new ClaimField());

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
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalVat().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalInclVat().getAssessed());
        }
    }
}
