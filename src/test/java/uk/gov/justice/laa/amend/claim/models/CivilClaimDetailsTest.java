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
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getCounselsCost().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getDetentionTravelWaitingCosts().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getJrFormFillingCost().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAdjournedHearing().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getCmrhTelephone().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getCmrhOral().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getHoInterview().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getSubstantiveHearing().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalVat().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalInclVat().getAmended());
        }
    }
}
