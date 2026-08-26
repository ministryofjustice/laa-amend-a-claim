package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.models.enums.FieldType;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public interface GenericAmendmentFieldValidator extends AmendmentFieldValidator {

  FieldType supportedType();

  void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors);
}
