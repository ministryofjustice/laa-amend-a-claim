package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public interface AmendmentFieldValidator {

  String FIELD_PATH = "inputs[%s]";

  default void addUniqueFieldError(ClaimViewField<?> field, String errorCode, Errors errors) {
    addUniqueFieldError(field, errorCode, new String[] {}, errors);
  }

  default void addUniqueFieldError(
      ClaimViewField<?> field, String errorCode, Object[] args, Errors errors) {
    // Ensures that fields only have one error at a time, following GDS guidance
    if (!errors.hasFieldErrors(FIELD_PATH.formatted(field.name()))) {
      errors.rejectValue(FIELD_PATH.formatted(field.name()), errorCode, args, null);
    }
  }
}
