package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimDetailsView;

import java.math.BigDecimal;

public class ClaimFieldTest {

    @Test
    void constructorWithoutAmendedValueShouldUseSubmittedValue() {
        ClaimField claimField = new ClaimField("label", BigDecimal.ONE, BigDecimal.TWO);

        Assertions.assertEquals("label", claimField.getLabel());
        Assertions.assertEquals(BigDecimal.ONE, claimField.getSubmitted());
        Assertions.assertEquals(BigDecimal.TWO, claimField.getCalculated());
        Assertions.assertEquals(BigDecimal.ONE, claimField.getAmended());
        Assertions.assertNull(claimField.getCost());
    }

    @Nested
    class GetChangeUrlTests {

        String submissionId = "foo";
        String claimId = "bar";

        @Test
        void returnNullWhenCostIsNull() {
            ClaimField claimField = new ClaimField();
            Assertions.assertNull(claimField.getChangeUrl(submissionId, claimId));
        }

        @Test
        void returnUrlWhenRowCostIsNotNull() {
            ClaimField claimField = new ClaimField();
            claimField.setCost(Cost.PROFIT_COSTS);
            String expectedResult = "/submissions/foo/claims/bar/profit-costs";
            Assertions.assertEquals(expectedResult, claimField.getChangeUrl(submissionId, claimId));
        }
    }
}
