package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public interface FieldSpecificAmendmentValidator {

  boolean appliesTo(ClaimViewField<?> field);

  void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors);
}
