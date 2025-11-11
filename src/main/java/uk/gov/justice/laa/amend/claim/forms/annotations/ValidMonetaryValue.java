package uk.gov.justice.laa.amend.claim.forms.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.gov.justice.laa.amend.claim.forms.validators.MonetaryValueValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = MonetaryValueValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMonetaryValue {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
