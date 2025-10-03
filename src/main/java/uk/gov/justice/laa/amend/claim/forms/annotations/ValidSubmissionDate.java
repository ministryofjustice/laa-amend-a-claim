package uk.gov.justice.laa.amend.claim.forms.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.gov.justice.laa.amend.claim.forms.validators.DateValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSubmissionDate {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
