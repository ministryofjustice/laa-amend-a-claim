package uk.gov.justice.laa.payments.amend.forms.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import uk.gov.justice.laa.payments.amend.forms.AllowedTotalForm;
import uk.gov.justice.laa.payments.amend.forms.annotations.ValidAllowedTotal;

@AllArgsConstructor
public class AllowedTotalsValueValidator extends CurrencyValidator
    implements ConstraintValidator<ValidAllowedTotal, AllowedTotalForm> {

  @Override
  public boolean isValid(AllowedTotalForm form, ConstraintValidatorContext context) {
    return isValid(
            form.getAllowedTotalVat(), context, "allowedTotalVat", "allowedTotals.allowedTotalVat")
        & isValid(
            form.getAllowedTotalInclVat(),
            context,
            "allowedTotalInclVat",
            "allowedTotals.allowedTotalInclVat");
  }
}
