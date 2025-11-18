package uk.gov.justice.laa.amend.claim.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;

@Component
public class FormUtils {

    private static MessageSource messageSource;

    // Inject MessageSource into static field
    public FormUtils(MessageSource messageSource) {
        FormUtils.messageSource = messageSource;
    }

    public static String toFieldId(String fieldName) {
        return fieldName != null ? fieldName.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase() : "main-content";
    }

    public static String getClientFullName(String clientForename, String clientSurname) {
        if (clientForename != null & clientSurname != null) {
            return String.format("%s %s", clientForename, clientSurname);
        } else if (clientForename != null) {
            return clientForename;
        } else if (clientSurname != null) {
            return clientSurname;
        } else {
            return null;
        }
    }

    public static String displayDateValue(LocalDate localDate) {
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : null;
    }
}
