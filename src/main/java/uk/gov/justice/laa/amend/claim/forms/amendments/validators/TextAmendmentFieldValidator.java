package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

@Component
public class TextAmendmentFieldValidator implements GenericAmendmentFieldValidator {

  static final int MAX_TEXT_LENGTH = 50;

  private static final String TOO_LONG_CODE = "amendmentForm.text.tooLong";

  private final MessageSource messageSource;

  public TextAmendmentFieldValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public FieldType supportedType() {
    return FieldType.TEXT;
  }

  @Override
  public void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var value = form.getInputs().get(field.name());
    if (value != null && value.length() > MAX_TEXT_LENGTH) {
      addUniqueFieldError(field, TOO_LONG_CODE, new String[] {field.label(messageSource), String.valueOf(MAX_TEXT_LENGTH)}, errors);
    }
  }
}
