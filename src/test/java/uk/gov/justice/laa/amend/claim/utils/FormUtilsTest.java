package uk.gov.justice.laa.amend.claim.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class FormUtilsTest {

    @Nested
    class ToFieldIdTests {
        @Test
        void nullStringReturnsDefault() {
            String result = FormUtils.toFieldId(null);
            Assertions.assertEquals("main-content", result);
        }

        @Test
        void camelCaseStringConverted() {
            String result = FormUtils.toFieldId("someRandomId");
            Assertions.assertEquals("some-random-id", result);
        }

        @Test
        void lowerCaseHyphenatedStringUnaffected() {
            String result = FormUtils.toFieldId("some-random-id");
            Assertions.assertEquals("some-random-id", result);
        }
    }
}
