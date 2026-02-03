package uk.gov.justice.laa.amend.claim.exceptions;

import java.text.ParseException;

public class ThousandsSeparatorParseException extends ParseException {
    public ThousandsSeparatorParseException(String s) {
        super(s, 0);
    }
}
