package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

@RequiredArgsConstructor
public class AmendmentFormValidator implements Validator {

  static final int MAX_TEXT_LENGTH = 255;

  private static final String FIELD_PATH = "inputs[%s]";
  private static final String TOO_LONG_CODE = "amendmentForm.tooLong";
  private static final String INVALID_OPTION_CODE = "amendmentForm.invalidOption";
  private static final String INVALID_VALUE_CODE = "amendmentForm.invalidValue";

  private final Class<?> claimDetailsType;

  @Override
  public boolean supports(Class<?> clazz) {
    return AmendmentForm.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (AmendmentForm) target;

    for (var entry : form.getFieldInputs(claimDetailsType).entrySet()) {
      var field = entry.getKey();
      var value = entry.getValue();

      switch (field.getType()) {
        case TEXT -> validateTextLength(field, value, errors);
        case ENUM -> validateEnumOption(field, value, errors);
        case NUMBER -> validateParses(field, value, form::getIntegerValue, errors);
        case BIG_DECIMAL -> validateParses(field, value, form::getBigDecimalValue, errors);
        case BOOLEAN -> validateBoolean(field, value, form, errors);
        case DATE -> validateDate(field, form, errors);
        default -> throw new IllegalStateException("Unsupported field type: " + field.getType());
      }
    }
  }

  private void validateTextLength(ClaimViewField<?> field, String value, Errors errors) {
    if (value != null && value.length() > MAX_TEXT_LENGTH) {
      reject(errors, field, TOO_LONG_CODE, "Value exceeds maximum length", MAX_TEXT_LENGTH);
    }
  }

  private void validateEnumOption(ClaimViewField<?> field, String value, Errors errors) {
    if (isBlank(value)) {
      return;
    }
    var isAllowedOption =
        field.getOptions().stream().anyMatch(option -> option.value().equals(value));
    if (!isAllowedOption) {
      reject(errors, field, INVALID_OPTION_CODE, "Value is not a recognised option", value);
    }
  }

  private void validateParses(
      ClaimViewField<?> field, String value, Function<String, ?> parser, Errors errors) {
    if (isBlank(value)) {
      return;
    }
    if (parser.apply(field.name()) == null) {
      reject(errors, field, INVALID_VALUE_CODE, "Value is not valid", value);
    }
  }

  private void validateBoolean(
      ClaimViewField<?> field, String value, AmendmentForm form, Errors errors) {
    if (isBlank(value)) {
      return;
    }
    try {
      form.getBooleanValue(field.name());
    } catch (IllegalArgumentException e) {
      reject(errors, field, INVALID_VALUE_CODE, "Value is not valid", value);
    }
  }

  private void validateDate(ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var fieldName = field.name();
    if (form.isDateInputProvided(fieldName) && form.getDateValue(fieldName) == null) {
      reject(errors, field, INVALID_VALUE_CODE, "Value is not a valid date", (Object) null);
    }
  }

  private void reject(
      Errors errors, ClaimViewField<?> field, String code, String defaultMessage, Object arg) {
    errors.rejectValue(
        FIELD_PATH.formatted(field.name()), code, new Object[] {field.name(), arg}, defaultMessage);
  }
}
