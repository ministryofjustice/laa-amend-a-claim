package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

class CaseConcludedDateValidatorTest {

  CaseConcludedDateValidator validator;

  @BeforeEach
  void beforeEach() {
    validator = new CaseConcludedDateValidator(TestMessageSources.real());
  }

  @Nested
  class Civil {

    @Test
    void testAppliesTo() {
      assertTrue(validator.appliesTo(CivilClaimDetailsViewField.CASE_CONCLUDED_CLAIMED_DATE));
    }

    @ParameterizedTest
    @MethodSource(
        "uk.gov.justice.laa.amend.claim.forms.amendments.validators"
            + ".InvalidDateArgumentsProvider#invalidDateProvider")
    void shouldNotAddErrorsForInvalidDates(String day, String month, String year) {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_CLAIMED_DATE-day", day,
              "CASE_CONCLUDED_CLAIMED_DATE-month", month,
              "CASE_CONCLUDED_CLAIMED_DATE-year", year);

      Errors result = validateCivil(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForValidDate() {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_CLAIMED_DATE-day", "20",
              "CASE_CONCLUDED_CLAIMED_DATE-month", "5",
              "CASE_CONCLUDED_CLAIMED_DATE-year", "2013");

      Errors result = validateCivil(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForFirstValidDate() {
      // Day before submission end of next month rule
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_CLAIMED_DATE-day", "20",
              "CASE_CONCLUDED_CLAIMED_DATE-month", "5",
              "CASE_CONCLUDED_CLAIMED_DATE-year", "2013");

      Errors result = validateCivil(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldAddErrorsForOldDate() {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_CLAIMED_DATE-day", "31",
              "CASE_CONCLUDED_CLAIMED_DATE-month", "3",
              "CASE_CONCLUDED_CLAIMED_DATE-year", "2013");

      Errors result = validateCivil(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_CLAIMED_DATE']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_BEFORE_CODE);
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_CLAIMED_DATE']").getArguments()[0])
          .isEqualTo("Case concluded date or case claimed date");
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_CLAIMED_DATE']").getArguments()[1])
          .isEqualTo("1 April 2013");
    }

    @Test
    void shouldAddErrorsForDateInFuture() {
      var tomorrow = LocalDate.now().plusDays(1);
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_CLAIMED_DATE-day", String.valueOf(tomorrow.getDayOfMonth()),
              "CASE_CONCLUDED_CLAIMED_DATE-month", String.valueOf(tomorrow.getMonthValue()),
              "CASE_CONCLUDED_CLAIMED_DATE-year", String.valueOf(tomorrow.getYear()));

      Errors result = validateCivil(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_CLAIMED_DATE']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_IN_FUTURE_CODE);
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_CLAIMED_DATE']").getArguments()[0])
          .isEqualTo("Case concluded date or case claimed date");
    }

    @Test
    void shouldAddErrorsForDateNotInCorrectPeriod() {
      var tomorrow = LocalDate.now().plusDays(1);
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_CLAIMED_DATE-day", "21",
              "CASE_CONCLUDED_CLAIMED_DATE-month", "5",
              "CASE_CONCLUDED_CLAIMED_DATE-year", "2013");

      Errors result = validateCivil(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_CLAIMED_DATE']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_LATER_THAN_SUBMISSION_PERIOD_CODE);
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_CLAIMED_DATE']").getArguments()[0])
          .isEqualTo("Case concluded date or case claimed date");
    }

    private Errors validateCivil(Map<String, String> inputs) {
      ClaimDetails claimDetails = MockClaimsFunctions.createMockCivilClaim();
      claimDetails.setSubmissionPeriod(YearMonth.of(2013, 4));

      var form = new AmendmentForm();
      form.setInputs(inputs);

      var errors = new BeanPropertyBindingResult(form, "amendmentForm");
      validator.validate(
          claimDetails, CivilClaimDetailsViewField.CASE_CONCLUDED_CLAIMED_DATE, form, errors);
      return errors;
    }
  }

  @Nested
  class Mediation {

    @Test
    void testAppliesTo() {
      assertTrue(validator.appliesTo(MediationClaimDetailsViewField.CASE_CONCLUDED_DATE));
    }

    @ParameterizedTest
    @MethodSource(
        "uk.gov.justice.laa.amend.claim.forms.amendments.validators"
            + ".InvalidDateArgumentsProvider#invalidDateProvider")
    void shouldNotAddErrorsForInvalidDates(String day, String month, String year) {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", day,
              "CASE_CONCLUDED_DATE-month", month,
              "CASE_CONCLUDED_DATE-year", year);

      Errors result = validationMediation(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForValidDate() {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", "20",
              "CASE_CONCLUDED_DATE-month", "5",
              "CASE_CONCLUDED_DATE-year", "2013");

      Errors result = validationMediation(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForFirstValidDate() {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", "1",
              "CASE_CONCLUDED_DATE-month", "4",
              "CASE_CONCLUDED_DATE-year", "2013");

      Errors result = validationMediation(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldAddErrorsForOldDate() {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", "31",
              "CASE_CONCLUDED_DATE-month", "3",
              "CASE_CONCLUDED_DATE-year", "2013");

      Errors result = validationMediation(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_BEFORE_CODE);
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getArguments()[0])
          .isEqualTo("Case concluded date");
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getArguments()[1])
          .isEqualTo("1 April 2013");
    }

    @Test
    void shouldAddErrorsForDateInFuture() {
      var tomorrow = LocalDate.now().plusDays(1);
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", String.valueOf(tomorrow.getDayOfMonth()),
              "CASE_CONCLUDED_DATE-month", String.valueOf(tomorrow.getMonthValue()),
              "CASE_CONCLUDED_DATE-year", String.valueOf(tomorrow.getYear()));

      Errors result = validationMediation(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_IN_FUTURE_CODE);
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getArguments()[0])
          .isEqualTo("Case concluded date");
    }

    @Test
    void shouldAddErrorsForDateNotInCorrectPeriod() {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", "21",
              "CASE_CONCLUDED_DATE-month", "5",
              "CASE_CONCLUDED_DATE-year", "2013");

      Errors result = validationMediation(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_LATER_THAN_SUBMISSION_PERIOD_CODE);
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getArguments()[0])
          .isEqualTo("Case concluded date");
    }

    private Errors validationMediation(Map<String, String> inputs) {
      ClaimDetails claimDetails = MockClaimsFunctions.createMockMediationClaim();
      claimDetails.setSubmissionPeriod(YearMonth.of(2013, 4));

      var form = new AmendmentForm();
      form.setInputs(inputs);

      var errors = new BeanPropertyBindingResult(form, "amendmentForm");
      validator.validate(
          claimDetails, MediationClaimDetailsViewField.CASE_CONCLUDED_DATE, form, errors);
      return errors;
    }
  }

  @Nested
  class Crime {

    @Test
    void testAppliesTo() {
      assertTrue(validator.appliesTo(CrimeClaimDetailsViewField.CASE_CONCLUDED_DATE));
    }

    @ParameterizedTest
    @MethodSource(
        "uk.gov.justice.laa.amend.claim.forms.amendments.validators"
            + ".InvalidDateArgumentsProvider#invalidDateProvider")
    void shouldNotAddErrorsForInvalidDates(String day, String month, String year) {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", day,
              "CASE_CONCLUDED_DATE-month", month,
              "CASE_CONCLUDED_DATE-year", year);

      Errors result = validateCrime(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForValidDate() {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", "20",
              "CASE_CONCLUDED_DATE-month", "5",
              "CASE_CONCLUDED_DATE-year", "2016");

      Errors result = validateCrime(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForFirstValidDate() {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", "1",
              "CASE_CONCLUDED_DATE-month", "4",
              "CASE_CONCLUDED_DATE-year", "2016");

      Errors result = validateCrime(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldAddErrorsForOldDate() {
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", "31",
              "CASE_CONCLUDED_DATE-month", "3",
              "CASE_CONCLUDED_DATE-year", "2016");

      Errors result = validateCrime(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_BEFORE_CODE);
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getArguments()[0])
          .isEqualTo("Case concluded date");
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getArguments()[1])
          .isEqualTo("1 April 2016");
    }

    @Test
    void shouldAddErrorsForDateInFuture() {
      var tomorrow = LocalDate.now().plusDays(1);
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", String.valueOf(tomorrow.getDayOfMonth()),
              "CASE_CONCLUDED_DATE-month", String.valueOf(tomorrow.getMonthValue()),
              "CASE_CONCLUDED_DATE-year", String.valueOf(tomorrow.getYear()));

      Errors result = validateCrime(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_IN_FUTURE_CODE);
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getArguments()[0])
          .isEqualTo("Case concluded date");
    }

    @Test
    void shouldAddErrorsForDateNotInCorrectPeriod() {
      var tomorrow = LocalDate.now().plusDays(1);
      Map<String, String> input =
          Map.of(
              "CASE_CONCLUDED_DATE-day", "21",
              "CASE_CONCLUDED_DATE-month", "5",
              "CASE_CONCLUDED_DATE-year", "2016");

      Errors result = validateCrime(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_LATER_THAN_SUBMISSION_PERIOD_CODE);
      assertThat(result.getFieldError("inputs['CASE_CONCLUDED_DATE']").getArguments()[0])
          .isEqualTo("Case concluded date");
    }

    private Errors validateCrime(Map<String, String> inputs) {
      ClaimDetails claimDetails = MockClaimsFunctions.createMockCrimeClaim();
      claimDetails.setSubmissionPeriod(YearMonth.of(2016, 4));

      var form = new AmendmentForm();
      form.setInputs(inputs);

      var errors = new BeanPropertyBindingResult(form, "amendmentForm");
      validator.validate(
          claimDetails, CrimeClaimDetailsViewField.CASE_CONCLUDED_DATE, form, errors);
      return errors;
    }
  }
}
