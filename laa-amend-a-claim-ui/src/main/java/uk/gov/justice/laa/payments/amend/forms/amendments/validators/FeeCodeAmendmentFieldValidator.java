package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.service.AvailableFeeCodesService;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

@Component
public class FeeCodeAmendmentFieldValidator implements FieldSpecificAmendmentValidator {

  private static final String INVALID_CODE = "amendmentForm.feeCode.invalid";

  private final AvailableFeeCodesService availableFeeCodesService;

  public FeeCodeAmendmentFieldValidator(AvailableFeeCodesService availableFeeCodesService) {
    this.availableFeeCodesService = availableFeeCodesService;
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return field == ClaimDetailsViewField.FEE_CODE;
  }

  @Override
  public void validate(
      ClaimDetails claimDetails, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    AreaOfLaw areaOfLaw = claimDetails.getAreaOfLaw();
    var value = form.getInputs().get(field.name());
    var availableFeeCodes = availableFeeCodesService.getAvailableFeeCodes(areaOfLaw);
    if (value == null || !availableFeeCodes.containsKey(value)) {
      addUniqueFieldError(field, INVALID_CODE, errors);
    }
  }
}
