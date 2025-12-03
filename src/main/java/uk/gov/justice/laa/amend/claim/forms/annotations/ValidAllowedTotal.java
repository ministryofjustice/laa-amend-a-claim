package uk.gov.justice.laa.amend.claim.forms.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.gov.justice.laa.amend.claim.forms.validators.AllowedTotalsValueValidator;
import uk.gov.justice.laa.amend.claim.forms.validators.MonetaryValueValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = AllowedTotalsValueValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAllowedTotal {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
