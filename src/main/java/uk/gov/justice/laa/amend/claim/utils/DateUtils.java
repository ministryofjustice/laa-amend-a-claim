package uk.gov.justice.laa.amend.claim.utils;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_TIME_FORMAT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    static final ZoneId LONDON_TIMEZONE = ZoneId.of("Europe/London");

    public static String displayDateValue(LocalDate value) {
        return value != null ? value.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : null;
    }

    public static String displayDateTimeDateValue(OffsetDateTime value) {
        return value != null ? displayDateTimeValue(toLondonLocalDateTime(value), DEFAULT_DATE_FORMAT) : null;
    }

    public static String displayDateTimeTimeValue(OffsetDateTime value) {
        return value != null ? displayDateTimeValue(toLondonLocalDateTime(value), DEFAULT_TIME_FORMAT) : null;
    }

    private static LocalDateTime toLondonLocalDateTime(OffsetDateTime value) {
        return value.atZoneSameInstant(LONDON_TIMEZONE).toLocalDateTime();
    }

    private static String displayDateTimeValue(LocalDateTime value, String format) {
        return value != null ? value.format(DateTimeFormatter.ofPattern(format)) : null;
    }

    public static String toSubmissionPeriod(String month, String year) {
        try {
            return YearMonth.of(Integer.parseInt(year), Integer.parseInt(month))
                    .format(DateTimeFormatter.ofPattern("MMM-yyyy"))
                    .toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }
}
