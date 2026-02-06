package uk.gov.justice.laa.amend.claim.utils;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_TIME_FORMAT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String displayDateValue(LocalDate value) {
        return value != null ? value.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : null;
    }

    public static String displayDateTimeDateValue(LocalDateTime value) {
        return displayDateTimeValue(value, DEFAULT_DATE_FORMAT);
    }

    public static String displayDateTimeTimeValue(LocalDateTime value) {
        return displayDateTimeValue(value, DEFAULT_TIME_FORMAT);
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
