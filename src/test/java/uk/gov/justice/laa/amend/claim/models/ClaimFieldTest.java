package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ClaimFieldTest {

    @Test
    void constructorWithoutAmendedValueShouldUseSubmittedValue() {
        ClaimField claimField = new ClaimField("label", ClaimFieldValue.of(BigDecimal.ONE), ClaimFieldValue.of(BigDecimal.TWO));

        Assertions.assertEquals("label", claimField.getLabel());
        Assertions.assertEquals(BigDecimal.ONE, claimField.getSubmitted().getValue());
        Assertions.assertEquals(BigDecimal.TWO, claimField.getCalculated().getValue());
        Assertions.assertEquals(BigDecimal.ONE, claimField.getAmended().getValue());
        Assertions.assertNull(claimField.getCost());
    }
}
