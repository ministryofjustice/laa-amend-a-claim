package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

public class DateAmendmentFieldValidator implements AmendmentFieldValidator {

  private static final String INVALID_CODE = "amendmentForm.date.invalid";

  private final MessageSource messageSource;

  public DateAmendmentFieldValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public FieldType supportedType() {
    return FieldType.DATE;
  }

  @Override
  public void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var fieldName = field.name();
    if (form.isDateInputProvided(fieldName) && form.getDateValue(fieldName) == null) {
      var message =
          messageSource.getMessage(
              INVALID_CODE, new Object[] {field.label(messageSource)}, Locale.UK);
      errors.rejectValue(FIELD_PATH.formatted(fieldName), INVALID_CODE, message);
    }
  }
}
