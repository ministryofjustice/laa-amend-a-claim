package uk.gov.justice.laa.amend.claim.forms.amendments.validators.unique;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

class MediationCaseConcludedDateValidatorTest {

  MediationCaseConcludedDateValidator validator;

  @BeforeEach
  void beforeEach() {
    validator = new MediationCaseConcludedDateValidator(TestMessageSources.real());
  }

  @Test
  void testAppliesTo() {
    assertTrue(validator.appliesTo(MediationClaimDetailsViewField.CASE_CONCLUDED_DATE));
  }

  static Stream<Arguments> invalidDateProvider() {
    return Stream.of(
        // Empty strings
        Arguments.of("", "", ""),
        Arguments.of("", "12", "2020"),
        Arguments.of("15", "", "2020"),
        Arguments.of("15", "12", ""),
        // Invalid day (40th date)
        Arguments.of("40", "12", "2020"),
        Arguments.of("32", "01", "2020"),
        Arguments.of("00", "12", "2020"),
        // Invalid month
        Arguments.of("15", "13", "2020"),
        Arguments.of("15", "00", "2020"),
        // Characters instead of numbers
        Arguments.of("abc", "12", "2020"),
        Arguments.of("15", "xyz", "2020"),
        Arguments.of("15", "12", "abcd"),
        Arguments.of("!@#", "$%^", "&*()"),
        // Mixed valid and invalid characters
        Arguments.of("1a", "12", "2020"),
        Arguments.of("15", "1b", "2020"),
        Arguments.of("15", "12", "20c0"));
  }

  @ParameterizedTest
  @MethodSource("invalidDateProvider")
  void shouldNotAddErrorsForInvalidDates(String day, String month, String year) {
    Map<String, String> input =
        Map.of(
            "CASE_CONCLUDED_DATE-day", day,
            "CASE_CONCLUDED_DATE-month", month,
            "CASE_CONCLUDED_DATE-year", year);

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldNotAddErrorsForValidDate() {
    Map<String, String> input =
        Map.of(
            "CASE_CONCLUDED_DATE-day", "15",
            "CASE_CONCLUDED_DATE-month", "12",
            "CASE_CONCLUDED_DATE-year", "2020");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldNotAddErrorsForFirstValidDate() {
    Map<String, String> input =
        Map.of(
            "CASE_CONCLUDED_DATE-day", "1",
            "CASE_CONCLUDED_DATE-month", "4",
            "CASE_CONCLUDED_DATE-year", "2013");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldAddErrorsForOldDate() {
    Map<String, String> input =
        Map.of(
            "CASE_CONCLUDED_DATE-day", "31",
            "CASE_CONCLUDED_DATE-month", "3",
            "CASE_CONCLUDED_DATE-year", "2013");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isTrue();
    assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getCode())
        .isEqualTo(FieldSpecificAmendmentValidator.DATE_CANT_BE_BEFORE_CODE);
    assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getArguments()[0])
        .isEqualTo("Case concluded date");
    assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getArguments()[1])
        .isEqualTo("1 April 2013");
  }

  private Errors validate(Map<String, String> inputs) {
    ClaimDetails claimDetails = MockClaimsFunctions.createMockMediationClaim();

    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(
        claimDetails, MediationClaimDetailsViewField.CASE_CONCLUDED_DATE, form, errors);
    return errors;
  }
}
