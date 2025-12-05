package uk.gov.justice.laa.amend.claim.forms.errors;

import org.thymeleaf.spring6.util.DetailedError;

import java.util.Map;

public class AssessedTotalFormError extends FormError {

    public AssessedTotalFormError(DetailedError error) {
        super(error);
    }

    public AssessedTotalFormError(String fieldName, String message) {
        super(fieldName, message);
    }

    @Override
    protected Map<String, Integer> getFieldOrderMap() {
        return Map.of(
            "assessedTotalVat", 1,
            "assessedTotalInclVat", 2
        );
    }
}
