package uk.gov.justice.laa.amend.claim.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Utility class for formatting currency.
 */
@UtilityClass
public class CurrencyUtils {

    private static final Locale UK_LOCALE = Locale.UK;
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(UK_LOCALE);

    /**
     * Formats a BigDecimal value as a UK currency string (e.g., £5.00).
     *
     * @param amount the amount to format, can be null
     * @return formatted currency string, or "£0.00" if amount is null
     */
    public static String formatCurrency(BigDecimal amount) {
        return CURRENCY_FORMATTER.format(Objects.requireNonNullElse(amount, BigDecimal.ZERO));
    }

    /**
     * Formats a BigDecimal value as a UK currency string with custom null handling.
     *
     * @param amount the amount to format, can be null
     * @param nullValue the string to return if amount is null
     * @return formatted currency string, or nullValue if amount is null
     */
    public static String formatCurrency(BigDecimal amount, String nullValue) {
        if (amount == null) {
            return nullValue;
        }
        return CURRENCY_FORMATTER.format(amount);
    }
}