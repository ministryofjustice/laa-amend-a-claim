package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

public class TextAmendmentFieldValidator implements AmendmentFieldValidator {

  static final int MAX_TEXT_LENGTH = 255;

  private static final String TOO_LONG_CODE = "amendmentForm.tooLong";

  @Override
  public FieldType supportedType() {
    return FieldType.TEXT;
  }

  @Override
  public void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var value = form.getInputs().get(field.name());
    if (value != null && value.length() > MAX_TEXT_LENGTH) {
      reject(errors, field, TOO_LONG_CODE, "Value exceeds maximum length", MAX_TEXT_LENGTH);
    }
  }
}
