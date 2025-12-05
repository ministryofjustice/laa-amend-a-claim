package uk.gov.justice.laa.amend.claim.forms.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import uk.gov.justice.laa.amend.claim.forms.AssessedTotalForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidAssessedTotal;

@AllArgsConstructor
public class AssessedTotalsValueValidator extends CurrencyValidator implements ConstraintValidator<ValidAssessedTotal, AssessedTotalForm> {

    @Override
    public boolean isValid(AssessedTotalForm form, ConstraintValidatorContext context) {
        return isValid(form.getAssessedTotalVat(), context, "assessedTotalVat", "assessedTotals.assessedTotalVat")
            & isValid(form.getAssessedTotalInclVat(), context, "assessedTotalInclVat", "assessedTotals.assessedTotalInclVat");
    }
}
