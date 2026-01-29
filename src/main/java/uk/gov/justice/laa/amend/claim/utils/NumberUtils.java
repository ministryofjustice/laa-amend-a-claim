package uk.gov.justice.laa.amend.claim.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

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
        DecimalFormat format = new DecimalFormat();
        format.setParseBigDecimal(true);
        return (BigDecimal) format.parse(value);
    }
}
