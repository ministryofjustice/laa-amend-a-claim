package uk.gov.justice.laa.amend.claim.forms.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import uk.gov.justice.laa.amend.claim.forms.MonetaryValueForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidMonetaryValue;

@AllArgsConstructor
public class MonetaryValueValidator extends CurrencyValidator
        implements ConstraintValidator<ValidMonetaryValue, MonetaryValueForm> {

    @Override
    public boolean isValid(MonetaryValueForm form, ConstraintValidatorContext context) {
        return isValid(form.getValue(), context, "value", form.getCost().getPrefix());
    }
}
