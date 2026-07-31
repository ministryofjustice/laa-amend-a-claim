package uk.gov.justice.laa.amend.claim.forms.validators;

import java.util.List;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.AmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.BigDecimalAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.BooleanAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.DateAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.EnumAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.NumberAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.TextAmendmentFieldValidator;

public class AmendmentFormValidator implements Validator {

  private static final List<AmendmentFieldValidator> DEFAULT_FIELD_VALIDATORS =
      List.of(
          new TextAmendmentFieldValidator(),
          new EnumAmendmentFieldValidator(),
          new NumberAmendmentFieldValidator(),
          new BigDecimalAmendmentFieldValidator(),
          new BooleanAmendmentFieldValidator(),
          new DateAmendmentFieldValidator());

  private final Class<?> claimDetailsType;
  private final List<AmendmentFieldValidator> fieldValidators;

  public AmendmentFormValidator(Class<?> claimDetailsType) {
    this(claimDetailsType, DEFAULT_FIELD_VALIDATORS);
  }

  AmendmentFormValidator(Class<?> claimDetailsType, List<AmendmentFieldValidator> fieldValidators) {
    this.claimDetailsType = claimDetailsType;
    this.fieldValidators = fieldValidators;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return AmendmentForm.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (AmendmentForm) target;

    for (var entry : form.getFieldValues(claimDetailsType).entrySet()) {
      var field = entry.getKey();

      var matched = false;
      for (var fieldValidator : fieldValidators) {
        if (fieldValidator.supportedType() == field.getFieldType()) {
          matched = true;
          fieldValidator.validate(field, form, errors);
        }
      }

      if (!matched) {
        throw new IllegalStateException("Unsupported field type: " + field.getFieldType());
      }
    }
  }
}
