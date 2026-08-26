package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.models.enums.FieldType;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

@Component
public class BooleanAmendmentFieldValidator implements GenericAmendmentFieldValidator {

  private static final String INVALID_CODE = "amendmentForm.boolean.invalid";

  private final MessageSource messageSource;

  public BooleanAmendmentFieldValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

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
    boolean isInvalid = false;
    try {
      if (form.getBooleanValue(field.name()) == null) {
        isInvalid = true;
      }
    } catch (IllegalArgumentException e) {
      isInvalid = true;
    }
    if (isInvalid) {
      addUniqueFieldError(field, INVALID_CODE, new String[] {field.label(messageSource)}, errors);
    }
  }
}
