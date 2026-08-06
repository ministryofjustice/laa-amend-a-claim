package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AvailableFeeCodesService;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;

class FeeCodeAmendmentFieldValidatorTest {

  private final AvailableFeeCodesService availableFeeCodesService =
      mock(AvailableFeeCodesService.class);

  private final FeeCodeAmendmentFieldValidator validator =
      new FeeCodeAmendmentFieldValidator(availableFeeCodesService);

  @Test
  void appliesToFeeCodeFieldOnly() {
    assertThat(validator.appliesTo(ClaimDetailsViewField.FEE_CODE)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.GENDER)).isFalse();
  }

  @Test
  void acceptsFeeCodePresentInAvailableFeeCodes() {
    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenReturn(Map.of("FEECODE", "Fee code description"));

    var errors = validate(Map.of("FEE_CODE", "FEECODE"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsFeeCodeNotPresentInAvailableFeeCodes() {
    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenReturn(Map.of("FEECODE", "Fee code description"));

    var errors = validate(Map.of("FEE_CODE", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[FEE_CODE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.feeCode.invalid");
  }

  @Test
  void rejectsBlankFeeCode() {
    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenReturn(Map.of("FEECODE", "Fee code description"));

    var errors = validate(Map.of("FEE_CODE", ""));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[FEE_CODE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.feeCode.invalid");
  }

  private Errors validate(Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    CrimeClaimDetails mockCrimeClaim = MockClaimsFunctions.createMockCrimeClaim();

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(mockCrimeClaim, ClaimDetailsViewField.FEE_CODE, form, errors);
    return errors;
  }
}
