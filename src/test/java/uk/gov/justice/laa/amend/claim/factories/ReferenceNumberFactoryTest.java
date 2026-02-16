package uk.gov.justice.laa.amend.claim.factories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

public class ReferenceNumberFactoryTest {

    @RepeatedTest(10)
    void referenceNumberShouldBe6Characters() {
        ReferenceNumberFactory factory = new ReferenceNumberFactory();
        String result = factory.create();
        Assertions.assertEquals(6, result.length());
    }

    @RepeatedTest(10)
    void referenceNumberShouldOnlyContainUppercaseLettersAndNumbers() {
        ReferenceNumberFactory factory = new ReferenceNumberFactory();
        String result = factory.create();
        Assertions.assertTrue(result.matches("[A-Z0-9]+"));
    }

    @RepeatedTest(10)
    void referenceNumberShouldNotContainDisallowedCharacters() {
        ReferenceNumberFactory factory = new ReferenceNumberFactory();
        String result = factory.create();

        Assertions.assertFalse(result.contains("G")); // Often confused with 6
        Assertions.assertFalse(result.contains("I")); // Often confused with 1
        Assertions.assertFalse(result.contains("O")); // Often confused with 0
        Assertions.assertFalse(result.contains("Q")); // Often confused with 0
        Assertions.assertFalse(result.contains("W")); // Often excluded from DEC alphabet
        Assertions.assertFalse(result.contains("X")); // Often excluded from DEC alphabet
        Assertions.assertFalse(result.contains("Y")); // Often excluded from DEC alphabet
        Assertions.assertFalse(result.contains("Z")); // Often excluded from DEC alphabet
        Assertions.assertFalse(result.contains("0")); // Often confused with O and Q
        Assertions.assertFalse(result.contains("1")); // Often confused with I
        Assertions.assertFalse(result.contains("6")); // Often confused with G
    }
}
