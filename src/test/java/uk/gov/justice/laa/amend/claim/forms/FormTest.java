package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class FormTest {

    public static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    protected <T> ConstraintViolation<T> getViolation(Set<ConstraintViolation<T>> violations, String field) {
        ConstraintViolation<T> violation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals(field))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(violation);
        return violation;
    }

    protected <T> void checkNoViolations(T form) {
        Set<ConstraintViolation<T>> violations = validator.validate(form);
        Assertions.assertTrue(violations.isEmpty());
    }
}
