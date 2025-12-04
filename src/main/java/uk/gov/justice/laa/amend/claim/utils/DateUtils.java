package uk.gov.justice.laa.amend.claim.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_TIME_FORMAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.FULL_DATE_FORMAT;

public class DateUtils {
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    private static String displayDateValue(LocalDate localDate, String format) {
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern(format)) : null;
    }

    public static String displayDateValue(LocalDate localDate) {
        return displayDateValue(localDate, DEFAULT_DATE_FORMAT);
    }

    public static String displayFullDateValue(LocalDate localDate) {
        return displayDateValue(localDate, FULL_DATE_FORMAT);
    }

    public static String toSubmissionPeriod(String month, String year) {
        try {
            return YearMonth
                .of(Integer.parseInt(year), Integer.parseInt(month))
                .format(DateTimeFormatter.ofPattern("MMM-yyyy"))
                .toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDateValue(LocalDateTime localDate) {
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern(FULL_DATE_FORMAT)) : null;
    }

    public static String getTimeValue(LocalDateTime localDate) {
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)) : null;
    }
}
