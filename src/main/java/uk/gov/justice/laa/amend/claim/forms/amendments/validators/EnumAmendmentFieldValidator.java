package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

public class EnumAmendmentFieldValidator implements AmendmentFieldValidator {

  private static final String INVALID_OPTION_CODE = "amendmentForm.invalidOption";

  private final MessageSource messageSource;

  public EnumAmendmentFieldValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public FieldType supportedType() {
    return FieldType.ENUM;
  }

  @Override
  public void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var value = form.getInputs().get(field.name());
    if (isBlank(value)) {
      return;
    }
    var isAllowedOption =
        field.getOptions().stream().anyMatch(option -> option.value().equals(value));
    if (!isAllowedOption) {
      reject(errors, field, INVALID_OPTION_CODE, "Value is not a recognised option", value);
    }
  }
}
