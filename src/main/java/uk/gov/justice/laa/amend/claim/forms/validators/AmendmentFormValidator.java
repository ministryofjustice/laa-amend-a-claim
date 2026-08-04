package uk.gov.justice.laa.amend.claim.forms.validators;

import java.util.List;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.GenericAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.BigDecimalAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.BooleanAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.DateAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.EnumAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.NumberAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.TextAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

public class AmendmentFormValidator implements Validator {

  private final ClaimDetails claimDetails;
  private final Class<?> claimDetailsType;
  private final List<GenericAmendmentFieldValidator> fieldValidators;
  private final List<FieldSpecificAmendmentValidator> fieldSpecificValidators;

  public AmendmentFormValidator(
      ClaimDetails claimDetails,
      List<GenericAmendmentFieldValidator> fieldValidators,
      List<FieldSpecificAmendmentValidator> fieldSpecificValidators) {
    this.claimDetails = claimDetails;
    this.claimDetailsType = claimDetails.getClass();
    this.fieldValidators = fieldValidators;
    this.fieldSpecificValidators = fieldSpecificValidators;
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

      for (var fieldSpecificValidator : fieldSpecificValidators) {
        if (fieldSpecificValidator.appliesTo(field)) {
          fieldSpecificValidator.validate(claimDetails, field, form, errors);
        }
      }
    }
  }
}
