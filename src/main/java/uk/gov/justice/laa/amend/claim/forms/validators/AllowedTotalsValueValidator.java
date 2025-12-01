package uk.gov.justice.laa.amend.claim.forms.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import uk.gov.justice.laa.amend.claim.forms.AllowedTotalForm;
import uk.gov.justice.laa.amend.claim.forms.MonetaryValueForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidAllowedTotal;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidMonetaryValue;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.isBlank;

@AllArgsConstructor
public class AllowedTotalsValueValidator extends Validator implements ConstraintValidator<ValidAllowedTotal, AllowedTotalForm> {

    private static final BigDecimal MIN = BigDecimal.ZERO;
    private static final BigDecimal MAX = BigDecimal.valueOf(1_000_000);

    private boolean isValid(String field, ConstraintValidatorContext context, String fieldName) {

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

    @Override
    public boolean isValid(AllowedTotalForm form, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        return isValid(form.getAllowedTotalVat(), context, "allowedTotalVat") &
                isValid(form.getAllowedTotalInclVat(), context, "allowedTotalInclVat");
    }
}
