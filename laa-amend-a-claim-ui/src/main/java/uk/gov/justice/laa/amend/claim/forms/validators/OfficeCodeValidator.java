package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.OFFICE_CODE_REGEX;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidOfficeCode;

public class OfficeCodeValidator extends Validator
    implements ConstraintValidator<ValidOfficeCode, SearchForm> {

  private static final String FIELD = "officeCode";

  @Override
  public boolean isValid(SearchForm form, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();

    if (isBlank(form.getOfficeCode())) {
      addViolation(context, FIELD, "{index.officeCode.error.required}");
      return false;
    }

    if (form.getOfficeCode().length() != 6) {
      addViolation(context, FIELD, "{index.officeCode.error.format}");
      return false;
    }

    if (!form.getOfficeCode().matches(OFFICE_CODE_REGEX)) {
      addViolation(context, FIELD, "{index.officeCode.error.invalid}");
      return false;
    }

    return true;
  }
}
