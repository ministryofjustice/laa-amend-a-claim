package uk.gov.justice.laa.amend.claim.forms.errors;

import org.thymeleaf.spring6.util.DetailedError;

import java.util.Map;

public class AllowedTotalFormError extends FormError {

    public AllowedTotalFormError(DetailedError error) {
        super(error);
    }

    public AllowedTotalFormError(String fieldName, String message) {
        super(fieldName, message);
    }

    @Override
    protected Map<String, Integer> getFieldOrderMap() {
        return Map.of(
                "allowedTotalVat", 1,
                "allowedTotalInclVat", 2
        );
    }
}
