package uk.gov.justice.laa.amend.claim.forms.validators;

import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class CurrencyValidator extends Validator {

    private static final BigDecimal MIN = BigDecimal.ZERO;
    private static final BigDecimal MAX = BigDecimal.valueOf(1_000_000);

    public boolean isValid(String field, ConstraintValidatorContext context, String fieldName) {

        if (isBlank(field)) {
            addViolation(context, fieldName, String.format("{allowedTotals.%s.error.required}", fieldName));
            return false;
        }

        try {
            BigDecimal value = BigDecimal.valueOf(Long.parseLong(field));

            if (value.scale() > 2) {
                addViolation(context, fieldName, String.format("{allowedTotals.%s.error.invalid}", fieldName));
                return false;
            }

            if (value.compareTo(MIN) < 0) {
                addViolation(context, fieldName, String.format("{allowedTotals.%s.error.min}", fieldName));
                return false;
            }

            if (value.compareTo(MAX) >= 0) {
                addViolation(context, fieldName, String.format("{allowedTotals.%s.error.max}", fieldName));
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            addViolation(context, fieldName, String.format("{allowedTotals.%s.error.invalid}", fieldName));
            return false;
        }
    }
}
