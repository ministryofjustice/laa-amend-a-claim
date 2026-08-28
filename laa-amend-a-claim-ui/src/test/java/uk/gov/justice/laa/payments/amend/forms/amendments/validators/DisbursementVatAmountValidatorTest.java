package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;

class DisbursementVatAmountValidatorTest {

  DisbursementVatAmountValidator validator;
  ClaimDetails claimDetails;
  MessageSource messageSource;

  @BeforeEach
  void beforeEach() {
    messageSource = mock(MessageSource.class);
    when(messageSource.getMessage(eq("areaOfLaw.legalHelp"), isNull(), any()))
        .thenReturn("Legal help");
    when(messageSource.getMessage(eq("areaOfLaw.crimeLower"), isNull(), any()))
        .thenReturn("Crime lower");
    when(messageSource.getMessage(eq("areaOfLaw.mediation"), isNull(), any()))
        .thenReturn("Mediation");
    validator = new DisbursementVatAmountValidator(messageSource);
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

    assertFieldError(
        result, "Legal help", DisbursementVatAmountValidator.MAX_LEGAL_HELP_VAT_AMOUNT);
  }

  @Test
  void shouldFailCrimeValidation() {
    claimDetails.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    Map<String, String> input = Map.of("DISBURSEMENTS_VAT", "1000000.00");

    Errors result = validate(input);

    assertFieldError(
        result, "Crime lower", DisbursementVatAmountValidator.MAX_CRIME_LOWER_VAT_AMOUNT);
  }

  @Test
  void shouldFailMediationValidation() {
    claimDetails.setAreaOfLaw(AreaOfLaw.MEDIATION);
    Map<String, String> input = Map.of("DISBURSEMENTS_VAT", "10000000.00");

    Errors result = validate(input);

    assertFieldError(result, "Mediation", DisbursementVatAmountValidator.MAX_MEDIATION_VAT_AMOUNT);
  }

  private void assertFieldError(Errors result, String areaOfLawLabel, double maxVatAmount) {
    assertThat(result.hasFieldErrors()).isTrue();

    FieldError fieldError = result.getFieldError("inputs['DISBURSEMENTS_VAT']");

    assertThat(fieldError).isNotNull();
    assertThat(fieldError.getCode()).isEqualTo(DisbursementVatAmountValidator.ERROR_CODE);
    assertThat(fieldError.getArguments()[0]).isEqualTo(areaOfLawLabel);
    assertThat(fieldError.getArguments()[1]).isEqualTo(BigDecimal.valueOf(maxVatAmount));
  }

  private Errors validate(Map<String, String> inputs) {

    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(claimDetails, ClaimDetailsViewField.DISBURSEMENTS_VAT, form, errors);
    return errors;
  }
}
