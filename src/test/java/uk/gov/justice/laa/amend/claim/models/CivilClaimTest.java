package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class CivilClaimTest {

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

            claim.setNilledValues();
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getCounselsCost().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getDetentionTravelWaitingCosts().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getJrFormFillingCost().getAmended());
            Assertions.assertEquals(false, claim.getAdjournedHearing().getAmended());
            Assertions.assertEquals(0, claim.getCmrhTelephone().getAmended());
            Assertions.assertEquals(0, claim.getCmrhOral().getAmended());
            Assertions.assertEquals(0, claim.getHoInterview().getAmended());
            Assertions.assertEquals(0, claim.getSubstantiveHearing().getAmended());
        }
    }
}
