package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

public class BooleanAmendmentFieldValidator implements AmendmentFieldValidator {

  private static final String INVALID_VALUE_CODE = "amendmentForm.invalidValue";

  @Override
  public FieldType supportedType() {
    return FieldType.BOOLEAN;
  }

  @Override
  public void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var value = form.getInputs().get(field.name());
    if (isBlank(value)) {
      return;
    }
    try {
      form.getBooleanValue(field.name());
    } catch (IllegalArgumentException e) {
      reject(errors, field, INVALID_VALUE_CODE, "Value is not valid", value);
    }
  }
}
