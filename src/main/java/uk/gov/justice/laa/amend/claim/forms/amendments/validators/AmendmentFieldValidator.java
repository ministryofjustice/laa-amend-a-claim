package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

public interface AmendmentFieldValidator {

  String FIELD_PATH = "inputs[%s]";

  FieldType supportedType();

  void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors);
}
