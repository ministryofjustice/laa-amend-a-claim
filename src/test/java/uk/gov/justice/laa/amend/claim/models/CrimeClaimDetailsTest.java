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

            claim.setNilledValues();
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended().getValue());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended().getValue());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAmended().getValue());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getTravelCosts().getAmended().getValue());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getWaitingCosts().getAmended().getValue());
        }
    }
}
