package uk.gov.justice.laa.amend.claim.forms.helpers;

public class StringUtils {

    public static String toFieldId(String fieldName) {
        return fieldName != null ? fieldName.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase() : "main-content";
    }
}
