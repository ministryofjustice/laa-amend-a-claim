package uk.gov.justice.laa.amend.claim.forms.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import uk.gov.justice.laa.amend.claim.forms.AllowedTotalForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidAllowedTotal;

@AllArgsConstructor
public class AllowedTotalsValueValidator extends CurrencyValidator implements ConstraintValidator<ValidAllowedTotal, AllowedTotalForm> {

    @Override
    public boolean isValid(AllowedTotalForm form, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        return isValid(form.getAllowedTotalVat(), context, "allowedTotalVat")
                & isValid(form.getAllowedTotalInclVat(), context, "allowedTotalInclVat");
    }
}
