package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.text.ParseException;
import uk.gov.justice.laa.amend.claim.utils.NumberUtils;

public class CurrencyValidator extends Validator {

    private static final BigDecimal MIN = BigDecimal.ZERO;
    private static final BigDecimal MAX = BigDecimal.valueOf(1_000_000);

    public boolean isValid(String value, ConstraintValidatorContext context, String fieldName, String prefix) {
        context.disableDefaultConstraintViolation();

        if (isBlank(value)) {
            addViolation(context, fieldName, String.format("{%s.error.required}", prefix));
            return false;
        }

        try {
            BigDecimal amount = NumberUtils.parse(value);

            if (amount.scale() > 2) {
                addViolation(context, fieldName, String.format("{%s.error.invalid}", prefix));
                return false;
            }

            if (amount.compareTo(MIN) < 0) {
                addViolation(context, fieldName, String.format("{%s.error.min}", prefix));
                return false;
            }

            if (amount.compareTo(MAX) >= 0) {
                addViolation(context, fieldName, String.format("{%s.error.max}", prefix));
                return false;
            }

            return true;
        } catch (NumberFormatException | ParseException e) {
            addViolation(context, fieldName, String.format("{%s.error.invalid}", prefix));
            return false;
        }
    }
}
