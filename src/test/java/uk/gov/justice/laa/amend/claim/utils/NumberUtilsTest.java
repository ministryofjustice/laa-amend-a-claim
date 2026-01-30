package uk.gov.justice.laa.amend.claim.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class NumberUtilsTest {

    @Test
    void parsesWholeNumber() throws Exception {
        BigDecimal result = NumberUtils.parseStrictUkNumber("1000");
        assertEquals(new BigDecimal("1000"), result);
    }

    @Test
    void parsesDecimalNumber() throws Exception {
        BigDecimal result = NumberUtils.parseStrictUkNumber("250.50");
        assertEquals(new BigDecimal("250.50"), result);
    }

    @Test
    void parsesNumberWithGroupingSeparators() throws Exception {
        BigDecimal result = NumberUtils.parseStrictUkNumber("1,305.50");
        assertEquals(new BigDecimal("1305.50"), result);
    }

    @Test
    void allowsLeadingAndTrailingWhitespace() throws Exception {
        BigDecimal result = NumberUtils.parseStrictUkNumber("  250.50  ");
        assertEquals(new BigDecimal("250.50"), result);
    }

    @Test
    void rejectsNullInput() {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parseStrictUkNumber(null));

        assertEquals("NUMBER_PARSE_FAILED", ex.getMessage());
    }

    @Test
    void rejectsEmptyString() {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parseStrictUkNumber(""));

        assertEquals("NUMBER_PARSE_FAILED", ex.getMessage());
    }

    @Test
    void rejectsAlphabeticInput() {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parseStrictUkNumber("abc"));

        assertEquals("NUMBER_PARSE_FAILED", ex.getMessage());
    }

    @Test
    void rejectsAlphanumericInput() {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parseStrictUkNumber("24word53"));

        assertEquals("NUMBER_PARSE_FAILED", ex.getMessage());
    }

    @Test
    void rejectsTrailingCharacters() {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parseStrictUkNumber("35kg"));

        assertEquals("NUMBER_PARSE_FAILED", ex.getMessage());
    }

    @Test
    void rejectsNonUkFormattedNumber() {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parseStrictUkNumber("1.305,50"));

        assertEquals("NUMBER_PARSE_FAILED", ex.getMessage());
    }

    @Test
    void acceptsMoreThanTwoDecimalPlacesAtParseStage() throws Exception {
        // Business rule enforced in validator, not parser
        BigDecimal result = NumberUtils.parseStrictUkNumber("1.234");
        assertEquals(new BigDecimal("1.234"), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "£",
            "€",
            "$",
            "π",
            "∞",
            "😊",
            "@",
            "#",
            "!",
            "%",
            "&",
            "*"
    })
    void rejectsSpecialCharactersAndSymbols(String input) {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parseStrictUkNumber(input));

        assertEquals("NUMBER_PARSE_FAILED", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123£",
            "£123",
            "100😊",
            "π3.14",
            "1,000€"
    })
    void rejectsMixedNumbersWithSymbols(String input) {
        assertThrows(ParseException.class,
                () -> NumberUtils.parseStrictUkNumber(input));
    }
}
