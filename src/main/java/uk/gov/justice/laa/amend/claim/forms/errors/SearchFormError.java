package uk.gov.justice.laa.amend.claim.forms.errors;

import org.thymeleaf.spring6.util.DetailedError;

import java.util.Map;

public class SearchFormError extends FormError {

    public SearchFormError(DetailedError error) {
        super(error);
    }

    public SearchFormError(String fieldName, String message) {
        super(fieldName, message);
    }

    @Override
    protected Map<String, Integer> getFieldOrderMap() {
        return Map.of(
            "providerAccountNumber", 1,
            "submissionDateMonth",  2,
            "submissionDateYear",   3,
            "referenceNumber",      4
        );
    }
}
