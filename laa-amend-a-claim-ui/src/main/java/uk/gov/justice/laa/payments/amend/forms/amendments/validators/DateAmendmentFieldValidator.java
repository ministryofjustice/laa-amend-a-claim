package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.models.enums.FieldType;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

@Component
public class DateAmendmentFieldValidator implements GenericAmendmentFieldValidator {

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
      addUniqueFieldError(field, INVALID_CODE, new String[] {field.label(messageSource)}, errors);
    }
  }
}
