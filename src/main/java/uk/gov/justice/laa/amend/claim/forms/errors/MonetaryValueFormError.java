package uk.gov.justice.laa.amend.claim.forms.errors;

import java.util.Map;
import org.thymeleaf.spring6.util.DetailedError;

public class MonetaryValueFormError extends FormError {

    public MonetaryValueFormError(DetailedError error) {
        super(error);
    }

    public MonetaryValueFormError(String fieldName, String message) {
        super(fieldName, message);
    }

    @Override
    protected Map<String, Integer> getFieldOrderMap() {
        return Map.of("value", 1);
    }
}
