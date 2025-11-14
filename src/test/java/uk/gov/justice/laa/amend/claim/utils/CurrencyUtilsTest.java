package uk.gov.justice.laa.amend.claim.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyUtilsTest {

    @Test
    void formatCurrency_withValidAmount_returnsFormattedString() {
        BigDecimal amount = new BigDecimal("5.00");
        String result = CurrencyUtils.formatCurrency(amount);
        assertThat(result).isEqualTo("£5.00");
    }

    @Test
    void formatCurrency_withLargeAmount_returnsFormattedStringWithCommas() {
        BigDecimal amount = new BigDecimal("1234.56");
        String result = CurrencyUtils.formatCurrency(amount);
        assertThat(result).isEqualTo("£1,234.56");
    }

    @Test
    void formatCurrency_withZero_returnsFormattedZero() {
        BigDecimal amount = BigDecimal.ZERO;
        String result = CurrencyUtils.formatCurrency(amount);
        assertThat(result).isEqualTo("£0.00");
    }

    @Test
    void formatCurrency_withNegativeAmount_returnsFormattedNegative() {
        BigDecimal amount = new BigDecimal("-10.50");
        String result = CurrencyUtils.formatCurrency(amount);
        assertThat(result).isEqualTo("-£10.50");
    }

    @Test
    void formatCurrency_withNull_returnsZero() {
        String result = CurrencyUtils.formatCurrency(null);
        assertThat(result).isEqualTo("£0.00");
    }

    @Test
    void formatCurrency_withDecimalPlaces_roundsCorrectly() {
        BigDecimal amount = new BigDecimal("5.999");
        String result = CurrencyUtils.formatCurrency(amount);
        assertThat(result).isEqualTo("£6.00");
    }

    @Test
    void formatCurrency_withCustomNullValue_returnsCustomString() {
        String result = CurrencyUtils.formatCurrency(null, "N/A");
        assertThat(result).isEqualTo("N/A");
    }

    @Test
    void formatCurrency_withAmountAndCustomNullValue_returnsFormattedString() {
        BigDecimal amount = new BigDecimal("10.00");
        String result = CurrencyUtils.formatCurrency(amount, "N/A");
        assertThat(result).isEqualTo("£10.00");
    }
}