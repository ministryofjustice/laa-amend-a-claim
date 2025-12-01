package uk.gov.justice.laa.amend.claim.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.FULL_DATE_FORMAT;

public class DateUtils {
    private static String displayDateValue(LocalDate localDate, String format) {
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern(format)) : null;
    }

    public static String displayDateValue(LocalDate localDate) {
        return displayDateValue(localDate, DEFAULT_DATE_FORMAT);
    }

    public static String displayFullDateValue(LocalDate localDate) {
        return displayDateValue(localDate, FULL_DATE_FORMAT);
    }
}
