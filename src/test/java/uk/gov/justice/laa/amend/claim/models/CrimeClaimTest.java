package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class CrimeClaimTest {

    @Nested
    class GetIsCrimeClaimTests {

        @Test
        void getIsCrimeClaimReturnsTrue() {
            CrimeClaim claim = new CrimeClaim();
            Assertions.assertTrue(claim.getIsCrimeClaim());
        }
    }

    @Nested
    class SetNilledValuesTests {

        @Test
        void setsValuesToNilledStatus() {
            CrimeClaim claim = new CrimeClaim();

            claim.setNetProfitCost(new ClaimField());
            claim.setNetDisbursementAmount(new ClaimField());
            claim.setDisbursementVatAmount(new ClaimField());
            claim.setTravelCosts(new ClaimField());
            claim.setWaitingCosts(new ClaimField());

            claim.setNilledValues();
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getTravelCosts().getAmended());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getWaitingCosts().getAmended());
        }
    }
}
