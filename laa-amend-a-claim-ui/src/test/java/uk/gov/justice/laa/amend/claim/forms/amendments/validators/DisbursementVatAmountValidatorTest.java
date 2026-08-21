package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;

class DisbursementVatAmountValidatorTest {

  DisbursementVatAmountValidator validator;
  ClaimDetails claimDetails;

  @BeforeEach
  void beforeEach() {
    validator = new DisbursementVatAmountValidator();
    claimDetails = MockClaimsFunctions.createMockCivilClaim();
  }

  @Test
  void testAppliesTo() {
    assertTrue(validator.appliesTo(ClaimDetailsViewField.DISBURSEMENTS_VAT));
  }

  @Test
  void shouldIgnoreIfDisbursementsVatAmountIsNull() {
    Map<String, String> input = Map.of("DISBURSEMENTS_VAT", "");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldIgnoreIfDisbursementsVatAmountIsMalformed() {
    Map<String, String> input = Map.of("DISBURSEMENTS_VAT", "abc");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldIgnoreIfAreaOfLawIsNull() {
    claimDetails.setAreaOfLaw(null);
    Map<String, String> input = Map.of("DISBURSEMENTS_VAT", "100");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldPassCivilValidation() {
    claimDetails.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    Map<String, String> input = Map.of("DISBURSEMENTS_VAT", "99999.99");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldPassCrimeValidation() {
    claimDetails.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    Map<String, String> input = Map.of("DISBURSEMENTS_VAT", "999999.99");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldPassMediationValidation() {
    claimDetails.setAreaOfLaw(AreaOfLaw.MEDIATION);
    Map<String, String> input = Map.of("DISBURSEMENTS_VAT", "9999999.99");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldFailCivilValidation() {
    claimDetails.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    Map<String, String> input = Map.of("DISBURSEMENTS_VAT", "100000.00");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isTrue();
    assertThat(result.getFieldError("inputs['DISBURSEMENTS_VAT']").getCode())
        .isEqualTo(DisbursementVatAmountValidator.ERROR_CODE);
  }

  @Test
  void shouldFailCrimeValidation() {
    claimDetails.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    Map<String, String> input = Map.of("DISBURSEMENTS_VAT", "1000000.00");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isTrue();
    assertThat(result.getFieldError("inputs['DISBURSEMENTS_VAT']").getCode())
        .isEqualTo(DisbursementVatAmountValidator.ERROR_CODE);
  }

  @Test
  void shouldFailMediationValidation() {
    claimDetails.setAreaOfLaw(AreaOfLaw.MEDIATION);
    Map<String, String> input = Map.of("DISBURSEMENTS_VAT", "10000000.00");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isTrue();
    assertThat(result.getFieldError("inputs['DISBURSEMENTS_VAT']").getCode())
        .isEqualTo(DisbursementVatAmountValidator.ERROR_CODE);
  }

  private Errors validate(Map<String, String> inputs) {

    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(claimDetails, ClaimDetailsViewField.DISBURSEMENTS_VAT, form, errors);
    return errors;
  }
}
