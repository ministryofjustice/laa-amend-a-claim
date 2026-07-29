package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

public class DateAmendmentFieldValidator implements AmendmentFieldValidator {

  private static final String INVALID_VALUE_CODE = "amendmentForm.invalidValue";

  @Override
  public FieldType supportedType() {
    return FieldType.DATE;
  }

  @Override
  public void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var fieldName = field.name();
    if (form.isDateInputProvided(fieldName) && form.getDateValue(fieldName) == null) {
      reject(errors, field, INVALID_VALUE_CODE, "Value is not a valid date", (Object) null);
    }
  }
}
