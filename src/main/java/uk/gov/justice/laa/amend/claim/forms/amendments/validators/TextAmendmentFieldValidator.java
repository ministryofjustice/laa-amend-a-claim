package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

public class TextAmendmentFieldValidator implements AmendmentFieldValidator {

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
      var message =
          messageSource.getMessage(
              TOO_LONG_CODE, new Object[] {field.label(messageSource), MAX_TEXT_LENGTH}, Locale.UK);
      errors.rejectValue(FIELD_PATH.formatted(field.name()), TOO_LONG_CODE, message);
    }
  }
}
