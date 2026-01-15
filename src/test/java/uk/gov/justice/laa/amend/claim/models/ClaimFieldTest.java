package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ClaimFieldTest {

    @Test
    void constructorWithoutAmendedValueShouldUseSubmittedValue() {
        ClaimField claimField = new ClaimField("fooBar", BigDecimal.ONE, BigDecimal.TWO, ClaimFieldType.NORMAL);

        Assertions.assertEquals("fooBar", claimField.getKey());
        Assertions.assertEquals("claimSummary.rows.fooBar", claimField.getLabel());
        Assertions.assertEquals("claimSummary.rows.fooBar.error", claimField.getErrorKey());
        Assertions.assertEquals("foo-bar", claimField.getId());
        Assertions.assertEquals(BigDecimal.ONE, claimField.getSubmitted());
        Assertions.assertEquals(BigDecimal.TWO, claimField.getCalculated());
        Assertions.assertEquals(BigDecimal.ONE, claimField.getAssessed());
        Assertions.assertNull(claimField.getChangeUrl());
        Assertions.assertEquals(ClaimFieldType.NORMAL, claimField.getType());
    }


    @Nested
    class GetChangeUrlTests {

        String submissionId = "foo";
        String claimId = "bar";

        @Test
        void returnNullWhenCostIsNull() {
            ClaimField claimField = ClaimField.builder().build();
            Assertions.assertNull(claimField.getChangeUrl(submissionId, claimId));
        }

        @Test
        void returnUrlWhenRowCostIsNotNull() {
            ClaimField claimField = ClaimField.builder().build();
            claimField.setChangeUrl(Cost.PROFIT_COSTS.getChangeUrl());
            String expectedResult = "/submissions/foo/claims/bar/profit-costs";
            Assertions.assertEquals(expectedResult, claimField.getChangeUrl(submissionId, claimId));
        }
    }
}
