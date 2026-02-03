package uk.gov.justice.laa.amend.claim.utils;

import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.amend.claim.exceptions.ThousandsSeparatorParseException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.regex.Pattern;

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

    public static BigDecimal parse(String value) throws ParseException {
        if (value == null) {
            throw new ParseException("Value must not be null", 0);
        }

        DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.UK);
        format.setParseBigDecimal(true);

        // Allow leading and trailing whitespace
        String trimmed = value.trim();

        // Check comma separators, if any, are valid
        checkCommaSeparators(trimmed);

        ParsePosition pos = new ParsePosition(0);

        Number parsed = format.parse(trimmed, pos);

        // No number parsed or non-finite value (e.g. ∞)
        if (!(parsed instanceof BigDecimal)) {
            throw new ParseException("Parsed value must be a BigDecimal", pos.getIndex());
        }

        // Reject partial parsing i.e. 35kg and 1abc3 should fail
        if (pos.getIndex() != trimmed.length()) {
            throw new ParseException("Value must be fully parsable", pos.getIndex());
        }

        return (BigDecimal) parsed;
    }

    private void checkCommaSeparators(String value) throws ParseException {
        if (value.contains(",")) {
            Pattern pattern = Pattern.compile("^\\d{1,3}(,\\d{3})*(\\.\\d+)?$");
            if (!pattern.matcher(value).matches()) {
                throw new ThousandsSeparatorParseException("Value must have valid comma separators or none at all");
            }
        }
    }
}
