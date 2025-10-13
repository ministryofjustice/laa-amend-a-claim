package uk.gov.justice.laa.amend.claim.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Nested
    class ToFieldIdTests {
        @Test
        void nullStringReturnsDefault() {
            String result = StringUtils.toFieldId(null);
            Assertions.assertEquals("main-content", result);
        }

        @Test
        void camelCaseStringConverted() {
            String result = StringUtils.toFieldId("someRandomId");
            Assertions.assertEquals("some-random-id", result);
        }

        @Test
        void lowerCaseHyphenatedStringUnaffected() {
            String result = StringUtils.toFieldId("some-random-id");
            Assertions.assertEquals("some-random-id", result);
        }
    }
}
