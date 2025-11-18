package uk.gov.justice.laa.amend.claim.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;

public class DateUtils {
    public static String displayDateValue(LocalDate localDate) {
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : null;
    }
}
