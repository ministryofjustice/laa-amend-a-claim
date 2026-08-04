package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AvailableFeeCodesService;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

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
