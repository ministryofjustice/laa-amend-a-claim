package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public interface FieldSpecificAmendmentValidator extends AmendmentFieldValidator {

  boolean appliesTo(ClaimViewField<?> field);

  void validate(ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors);
}
