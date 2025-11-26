package uk.gov.justice.laa.amend.claim.forms.errors;

import java.util.Map;

public class ReviewAndAmendFormError extends FormError {

    public ReviewAndAmendFormError(String id, String message) {
        super(id, message);
    }

    @Override
    protected Map<String, Integer> getFieldOrderMap() {
        return Map.of();
    }
}
