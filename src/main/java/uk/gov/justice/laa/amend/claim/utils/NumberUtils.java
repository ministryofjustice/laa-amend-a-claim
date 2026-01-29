package uk.gov.justice.laa.amend.claim.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

@UtilityClass
public class NumberUtils {

    public static Object getOrElseZero(Object value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public static BigDecimal add(BigDecimal... values) {
        BigDecimal sum = null;
        for (BigDecimal value : values) {
            sum = sum == null ? value : sum.add((BigDecimal) getOrElseZero(value));
        }
        return sum;
    }

    public static BigDecimal parseStrictUkNumber(String value) throws ParseException {
        if (value == null) {
            throw new ParseException("NUMBER_PARSE_FAILED", 0);
        }

        DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.UK);
        format.setParseBigDecimal(true);

        String trimmed = value.trim();  // Allow leading and trailing whitespace

        ParsePosition pos = new ParsePosition(0);

        BigDecimal result = (BigDecimal) format.parse(trimmed, pos);

        // Reject partial parsing i.e. 35kg and 1abc3 should fail
        if (result == null || pos.getIndex() != trimmed.length()) {
            throw new ParseException("NUMBER_PARSE_FAILED", pos.getIndex());
        }

        return result;
    }
}
