package uk.gov.justice.laa.amend.claim.forms.helpers;

public class StringUtils {

    public static boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }
}
