package uk.gov.justice.laa.amend.claim.forms.errors;

import java.util.Map;
import org.thymeleaf.spring6.util.DetailedError;

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
                "submissionDateMonth", 2,
                "submissionDateYear", 3,
                "uniqueFileNumber", 4,
                "caseReferenceNumber", 5);
    }
}
