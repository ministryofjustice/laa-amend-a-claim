package uk.gov.justice.laa.amend.claim.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class NumberUtils {

    public static BigDecimal getOrElseZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public static BigDecimal add(BigDecimal... values) {
        BigDecimal sum = null;
        for (BigDecimal value : values) {
            sum = sum == null ? value : sum.add(getOrElseZero(value));
        }
        return sum;
    }
}
