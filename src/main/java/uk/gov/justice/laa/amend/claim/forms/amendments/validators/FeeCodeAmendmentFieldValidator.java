package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.util.Locale;
import org.springframework.context.MessageSource;
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
  private final MessageSource messageSource;

  public FeeCodeAmendmentFieldValidator(
      AvailableFeeCodesService availableFeeCodesService,
      MessageSource messageSource) {
    this.availableFeeCodesService = availableFeeCodesService;
    this.messageSource = messageSource;
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return field == ClaimDetailsViewField.FEE_CODE;
  }

  @Override
  public void validate(ClaimDetails claimDetails, ClaimViewField<?> field, AmendmentForm form,
      Errors errors) {
    AreaOfLaw areaOfLaw = claimDetails.getAreaOfLaw();
    var value = form.getInputs().get(field.name());
    var availableFeeCodes = availableFeeCodesService.getAvailableFeeCodes(areaOfLaw);
    if (value == null || !availableFeeCodes.containsKey(value)) {
      var message =
          messageSource.getMessage(
              INVALID_CODE, new Object[] {field.label(messageSource)}, Locale.UK);
      errors.rejectValue(
          AmendmentFieldValidator.FIELD_PATH.formatted(field.name()), INVALID_CODE, message);
    }
  }

}
