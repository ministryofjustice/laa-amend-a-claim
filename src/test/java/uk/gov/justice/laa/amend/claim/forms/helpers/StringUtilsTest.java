package uk.gov.justice.laa.amend.claim.forms.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Nested
    class IsEmptyTests {
        @Test
        void nullStringReturnsTrue() {
            boolean result = StringUtils.isEmpty(null);
            Assertions.assertTrue(result);
        }

        @Test
        void emptyStringReturnsTrue() {
            boolean result = StringUtils.isEmpty("");
            Assertions.assertTrue(result);
        }

        @Test
        void blankStringReturnsTrue() {
            boolean result = StringUtils.isEmpty(" ");
            Assertions.assertTrue(result);
        }

        @Test
        void nonEmptyStringReturnsFalse() {
            boolean result = StringUtils.isEmpty("foo");
            Assertions.assertFalse(result);
        }
    }

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
