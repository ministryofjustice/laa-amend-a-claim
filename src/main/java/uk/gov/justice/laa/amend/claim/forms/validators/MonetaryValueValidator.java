package uk.gov.justice.laa.amend.claim.forms.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import uk.gov.justice.laa.amend.claim.forms.MonetaryValueForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidMonetaryValue;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.isBlank;

@AllArgsConstructor
public class MonetaryValueValidator extends Validator implements ConstraintValidator<ValidMonetaryValue, MonetaryValueForm> {

    private static final BigDecimal MIN = BigDecimal.ZERO;
    private static final BigDecimal MAX = BigDecimal.valueOf(1_000_000);
    private static final String FIELD_NAME = "value";

    @Override
    public boolean isValid(MonetaryValueForm form, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        String prefix = form.getPrefix();

        if (isBlank(form.getValue())) {
            addViolation(context, String.format("{%s.error.required}", prefix));
            return false;
        }

        try {
            BigDecimal value = new BigDecimal(form.getValue());

            if (value.scale() > 2) {
                addViolation(context, String.format("{%s.error.invalid}", prefix));
                return false;
            }

            if (value.compareTo(MIN) < 0) {
                addViolation(context, String.format("{%s.error.min}", prefix));
                return false;
            }

            if (value.compareTo(MAX) >= 0) {
                addViolation(context, String.format("{%s.error.max}", prefix));
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            addViolation(context, String.format("{%s.error.invalid}", prefix));
            return false;
        }
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        addViolation(context, FIELD_NAME, message);
    }
}
