package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

public class NumberAmendmentFieldValidator implements AmendmentFieldValidator {

  private static final String INVALID_VALUE_CODE = "amendmentForm.invalidValue";

  private final MessageSource messageSource;

  public NumberAmendmentFieldValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public FieldType supportedType() {
    return FieldType.NUMBER;
  }

  @Override
  public void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var value = form.getInputs().get(field.name());
    if (isBlank(value)) {
      return;
    }
    if (form.getIntegerValue(field.name()) == null) {
      reject(errors, field, INVALID_VALUE_CODE, "Value is not valid", value);
    }
  }
}
