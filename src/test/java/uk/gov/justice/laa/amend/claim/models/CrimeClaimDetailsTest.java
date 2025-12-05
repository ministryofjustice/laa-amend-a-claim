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
            claim.setAllowedTotalInclVat(new ClaimField());
            claim.setAllowedTotalVat(new ClaimField());

            claim.setNilledValues();
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getTravelCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getWaitingCosts().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalInclVat().getAssessed());
            Assertions.assertEquals(BigDecimal.ZERO, claim.getAllowedTotalVat().getAssessed());
        }
    }
}
