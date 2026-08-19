package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public interface FieldSpecificAmendmentValidator extends AmendmentFieldValidator {

  boolean appliesTo(ClaimViewField<?> field);

  void validate(ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors);
}
