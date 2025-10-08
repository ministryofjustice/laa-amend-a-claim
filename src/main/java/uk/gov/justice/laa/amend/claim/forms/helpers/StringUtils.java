package uk.gov.justice.laa.amend.claim.forms.helpers;

public class StringUtils {

    public static boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }

    public String toId(String fieldName) {
        return fieldName != null ? fieldName.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase() : "main-content";
    }
}
