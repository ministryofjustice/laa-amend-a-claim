package uk.gov.justice.laa.amend.claim.forms.amendments.validators;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;

class CivilTransferDateValidatorTest {

  CivilTransferDateValidator validator;

  @BeforeEach
  void beforeEach() {
    validator = new CivilTransferDateValidator(TestMessageSources.real());
  }

  @Test
  void testAppliesTo() {
    assertTrue(validator.appliesTo(CivilClaimDetailsViewField.TRANSFER_DATE));
  }

  @ParameterizedTest
  @MethodSource(
      "uk.gov.justice.laa.amend.claim.forms.amendments.validators.InvalidDateArgumentsProvider#invalidDateProvider")
  void shouldNotAddErrorsForInvalidDates(String day, String month, String year) {
    Map<String, String> input =
        Map.of(
            "TRANSFER_DATE-day", day,
            "TRANSFER_DATE-month", month,
            "TRANSFER_DATE-year", year);

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldNotAddErrorsForValidDate() {
    Map<String, String> input =
        Map.of(
            "TRANSFER_DATE-day", "15",
            "TRANSFER_DATE-month", "12",
            "TRANSFER_DATE-year", "2020");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldNotAddErrorsForFirstValidDate() {
    Map<String, String> input =
        Map.of(
            "TRANSFER_DATE-day", "1",
            "TRANSFER_DATE-month", "1",
            "TRANSFER_DATE-year", "1995");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldAddErrorsForOldDate() {
    Map<String, String> input =
        Map.of(
            "TRANSFER_DATE-day", "31",
            "TRANSFER_DATE-month", "12",
            "TRANSFER_DATE-year", "1994");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isTrue();
    assertThat(result.getFieldError("inputs['TRANSFER_DATE']").getCode())
        .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_BEFORE_CODE);
    assertThat(result.getFieldError("inputs['TRANSFER_DATE']").getArguments()[0])
        .isEqualTo("Transfer date");
    assertThat(result.getFieldError("inputs['TRANSFER_DATE']").getArguments()[1])
        .isEqualTo("1 January 1995");
  }

  private Errors validate(Map<String, String> inputs) {
    ClaimDetails claimDetails = MockClaimsFunctions.createMockCivilClaim();

    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(claimDetails, CivilClaimDetailsViewField.TRANSFER_DATE, form, errors);
    return errors;
  }
}