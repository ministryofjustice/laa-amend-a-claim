package uk.gov.justice.laa.amend.claim.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.justice.laa.amend.claim.exceptions.ThousandsSeparatorParseException;

import java.math.BigDecimal;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class NumberUtilsTest {

    @Test
    void parsesWholeNumber() throws Exception {
        BigDecimal result = NumberUtils.parse("1000");
        assertEquals(new BigDecimal("1000"), result);
    }

    @Test
    void parsesDecimalNumber() throws Exception {
        BigDecimal result = NumberUtils.parse("250.50");
        assertEquals(new BigDecimal("250.50"), result);
    }

    @Test
    void parsesNumberWithGroupingSeparators() throws Exception {
        BigDecimal result = NumberUtils.parse("1,305.50");
        assertEquals(new BigDecimal("1305.50"), result);
    }

    @Test
    void allowsLeadingAndTrailingWhitespace() throws Exception {
        BigDecimal result = NumberUtils.parse("  250.50  ");
        assertEquals(new BigDecimal("250.50"), result);
    }

    @Test
    void rejectsNullInput() {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parse(null));

        assertEquals("Value must not be null", ex.getMessage());
    }

    @Test
    void rejectsEmptyString() {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parse(""));

        assertEquals("Parsed value must be a BigDecimal", ex.getMessage());
    }

    @Test
    void rejectsAlphabeticInput() {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parse("abc"));

        assertEquals("Parsed value must be a BigDecimal", ex.getMessage());
    }

    @Test
    void rejectsAlphanumericInput() {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parse("24word53"));

        assertEquals("Value must be fully parsable", ex.getMessage());
    }

    @Test
    void rejectsInputWithSpaces() {
        ParseException ex =
            assertThrows(ParseException.class,
                () -> NumberUtils.parse("1 2 3"));

        assertEquals("Value must be fully parsable", ex.getMessage());
    }

    @Test
    void rejectsTrailingCharacters() {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parse("35kg"));

        assertEquals("Value must be fully parsable", ex.getMessage());
    }

    @Test
    void acceptsMoreThanTwoDecimalPlacesAtParseStage() throws Exception {
        // Business rule enforced in validator, not parser
        BigDecimal result = NumberUtils.parse("1.234");
        assertEquals(new BigDecimal("1.234"), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "200,00",
        "200,00",
        ",100",
        "1,000,00,000",
        "100.1,2345",
        "1234,567",
        "1.305,50"
    })
    void rejectsInvalidSeparators(String input) {
        ThousandsSeparatorParseException ex =
            assertThrows(ThousandsSeparatorParseException.class,
                () -> NumberUtils.parse(input));

        assertEquals("Value must have valid comma separators or none at all", ex.getMessage());
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
            "*",
            "NaN"
    })
    void rejectsSpecialCharactersAndSymbols(String input) {
        ParseException ex =
                assertThrows(ParseException.class,
                        () -> NumberUtils.parse(input));

        assertEquals("Parsed value must be a BigDecimal", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123£",
            "£123",
            "100😊",
            "π3.14",
            "1,000€",
            "50∞",
            "100NaN"
    })
    void rejectsMixedNumbersWithSymbols(String input) {
        assertThrows(ParseException.class,
                () -> NumberUtils.parse(input));
    }
}
