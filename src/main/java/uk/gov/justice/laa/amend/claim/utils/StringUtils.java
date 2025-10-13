package uk.gov.justice.laa.amend.claim.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class StringUtils {

    public static boolean nonBlank(String value) {
        return !isBlank(value);
    }

    public static String toFieldId(String fieldName) {
        return fieldName != null ? fieldName.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase() : "main-content";
    }
}
