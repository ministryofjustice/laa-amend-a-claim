package uk.gov.justice.laa.amend.claim.forms.amendments.validators.unique;

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
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;

class CaseStartDateValidatorTest {

  CaseStartDateValidator validator;

  @BeforeEach
  void beforeEach() {
    validator = new CaseStartDateValidator(TestMessageSources.real());
  }

  @Test
  void testAppliesTo() {
    assertTrue(validator.appliesTo(ClaimDetailsViewField.CASE_START_DATE));
  }

  @ParameterizedTest
  @MethodSource(
      "uk.gov.justice.laa.amend.claim.forms.amendments.validators.InvalidDateArgumentsProvider#invalidDateProvider")
  void shouldNotAddErrorsForInvalidDates(String day, String month, String year) {
    Map<String, String> input =
        Map.of(
            "CASE_START_DATE-day", day,
            "CASE_START_DATE-month", month,
            "CASE_START_DATE-year", year);

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldNotAddErrorsForValidDate() {
    Map<String, String> input =
        Map.of(
            "CASE_START_DATE-day", "15",
            "CASE_START_DATE-month", "12",
            "CASE_START_DATE-year", "2020");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldNotAddErrorsForFirstValidDate() {
    Map<String, String> input =
        Map.of(
            "CASE_START_DATE-day", "1",
            "CASE_START_DATE-month", "1",
            "CASE_START_DATE-year", "1995");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldAddErrorsForOldDate() {
    Map<String, String> input =
        Map.of(
            "CASE_START_DATE-day", "31",
            "CASE_START_DATE-month", "12",
            "CASE_START_DATE-year", "1994");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isTrue();
    assertThat(result.getFieldError("inputs['CASE_START_DATE']").getCode())
        .isEqualTo(CaseStartDateValidator.INVALID_CODE);
    assertThat(result.getFieldError("inputs['CASE_START_DATE']").getArguments()[0])
        .isEqualTo("Case start date");
    assertThat(result.getFieldError("inputs['CASE_START_DATE']").getArguments()[1])
        .isEqualTo("1 January 1995");
  }

  private Errors validate(Map<String, String> inputs) {
    ClaimDetails claimDetails = MockClaimsFunctions.createMockCivilClaim();

    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(claimDetails, ClaimDetailsViewField.CASE_START_DATE, form, errors);
    return errors;
  }
}
