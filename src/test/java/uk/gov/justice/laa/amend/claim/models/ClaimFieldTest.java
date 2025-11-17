package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
}
