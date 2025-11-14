package uk.gov.justice.laa.amend.claim.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BooleanUtils {

    public static String formatBoolean(Boolean value) {
        return (value != null && value) ? "service.yes" : "service.no";
    }
}
