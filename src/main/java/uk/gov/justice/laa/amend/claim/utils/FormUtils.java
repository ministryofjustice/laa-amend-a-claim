package uk.gov.justice.laa.amend.claim.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class FormUtils {

    public static boolean nonEmpty(String value) {
        return !isBlank(value);
    }

    public static String toFieldId(String fieldName) {
        return fieldName != null ? fieldName.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase() : "main-content";
    }
}
