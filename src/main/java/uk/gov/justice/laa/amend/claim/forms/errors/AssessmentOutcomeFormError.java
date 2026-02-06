package uk.gov.justice.laa.amend.claim.forms.errors;

import java.util.Map;
import org.thymeleaf.spring6.util.DetailedError;

public class AssessmentOutcomeFormError extends FormError {

    public AssessmentOutcomeFormError(DetailedError error) {
        super(error);
    }

    public AssessmentOutcomeFormError(String fieldName, String message) {
        super(fieldName, message);
    }

    @Override
    protected Map<String, Integer> getFieldOrderMap() {
        return Map.of(
                "assessmentOutcome", 1,
                "liabilityForVAT", 2);
    }
}
