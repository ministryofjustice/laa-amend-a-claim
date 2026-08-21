package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.utils.DateWrapperUtil;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

@ExtendWith(MockitoExtension.class)
class ClientDateOfBirthDateValidatorTest {

  ClientDateOfBirthDateValidator validator;

  @Mock DateWrapperUtil dateWrapperUtil;

  @BeforeEach
  void beforeEach() {
    validator = new ClientDateOfBirthDateValidator(TestMessageSources.real(), dateWrapperUtil);
  }

  @Nested
  class Civil {

    @Test
    void testAppliesTo() {
      assertTrue(validator.appliesTo(CivilClaimDetailsViewField.DATE_OF_BIRTH));
    }

    @ParameterizedTest
    @MethodSource(
        "uk.gov.justice.laa.amend.claim.forms.amendments.validators"
            + ".InvalidDateArgumentsProvider#invalidDateProvider")
    void shouldIgnoreUnparseableDateInputs(String day, String month, String year) {
      Map<String, String> input =
          Map.of(
              "DATE_OF_BIRTH-day", day,
              "DATE_OF_BIRTH-month", month,
              "DATE_OF_BIRTH-year", year);

      Errors result = validateCivil(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForValidDate() {
      Map<String, String> input =
          Map.of(
              "DATE_OF_BIRTH-day", "20",
              "DATE_OF_BIRTH-month", "5",
              "DATE_OF_BIRTH-year", "2013");

      Errors result = validateCivil(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForFirstValidDate() {
      Map<String, String> input =
          Map.of(
              "DATE_OF_BIRTH-day", "1",
              "DATE_OF_BIRTH-month", "1",
              "DATE_OF_BIRTH-year", "1900");

      Errors result = validateCivil(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldAddErrorsForOldDate() {
      Map<String, String> input =
          Map.of(
              "DATE_OF_BIRTH-day", "31",
              "DATE_OF_BIRTH-month", "12",
              "DATE_OF_BIRTH-year", "1899");

      Errors result = validateCivil(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['DATE_OF_BIRTH']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_BEFORE_CODE);
      assertThat(result.getFieldError("inputs['DATE_OF_BIRTH']").getArguments()[0])
          .isEqualTo("Date of birth");
      assertThat(result.getFieldError("inputs['DATE_OF_BIRTH']").getArguments()[1])
          .isEqualTo("1 January 1900");
    }

    @Test
    void shouldAddErrorsForDateInFuture() {
      when(dateWrapperUtil.isFutureDate(any())).thenReturn(true);
      Map<String, String> input =
          Map.of(
              "DATE_OF_BIRTH-day", "1",
              "DATE_OF_BIRTH-month", "1",
              "DATE_OF_BIRTH-year", "2020");

      Errors result = validateCivil(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['DATE_OF_BIRTH']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_IN_FUTURE_CODE);
      assertThat(result.getFieldError("inputs['DATE_OF_BIRTH']").getArguments()[0])
          .isEqualTo("Date of birth");
    }

    private Errors validateCivil(Map<String, String> inputs) {
      ClaimDetails claimDetails = MockClaimsFunctions.createMockCivilClaim();

      var form = new AmendmentForm();
      form.setInputs(inputs);

      var errors = new BeanPropertyBindingResult(form, "amendmentForm");
      validator.validate(claimDetails, CivilClaimDetailsViewField.DATE_OF_BIRTH, form, errors);
      return errors;
    }
  }

  @Nested
  class Mediation {

    @Test
    void testAppliesTo() {
      assertTrue(validator.appliesTo(MediationClaimDetailsViewField.DATE_OF_BIRTH));
    }

    @ParameterizedTest
    @MethodSource(
        "uk.gov.justice.laa.amend.claim.forms.amendments.validators"
            + ".InvalidDateArgumentsProvider#invalidDateProvider")
    void shouldNotAddErrorsForInvalidDates(String day, String month, String year) {
      Map<String, String> input =
          Map.of(
              "DATE_OF_BIRTH-day", day,
              "DATE_OF_BIRTH-month", month,
              "DATE_OF_BIRTH-year", year);

      Errors result = validateMediationClient2(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForValidDate() {
      Map<String, String> input =
          Map.of(
              "DATE_OF_BIRTH-day", "20",
              "DATE_OF_BIRTH-month", "5",
              "DATE_OF_BIRTH-year", "2013");

      Errors result = validateMediationClient2(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForFirstValidDate() {
      Map<String, String> input =
          Map.of(
              "DATE_OF_BIRTH-day", "1",
              "DATE_OF_BIRTH-month", "1",
              "DATE_OF_BIRTH-year", "1900");

      Errors result = validateMediationClient2(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldAddErrorsForOldDate() {
      Map<String, String> input =
          Map.of(
              "DATE_OF_BIRTH-day", "31",
              "DATE_OF_BIRTH-month", "12",
              "DATE_OF_BIRTH-year", "1899");

      Errors result = validateMediationClient2(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['DATE_OF_BIRTH']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_BEFORE_CODE);
      assertThat(result.getFieldError("inputs['DATE_OF_BIRTH']").getArguments()[0])
          .isEqualTo("Date of birth");
      assertThat(result.getFieldError("inputs['DATE_OF_BIRTH']").getArguments()[1])
          .isEqualTo("1 January 1900");
    }

    @Test
    void shouldAddErrorsForDateInFuture() {
      when(dateWrapperUtil.isFutureDate(any())).thenReturn(true);
      Map<String, String> input =
          Map.of(
              "DATE_OF_BIRTH-day", "1",
              "DATE_OF_BIRTH-month", "1",
              "DATE_OF_BIRTH-year", "2020");

      Errors result = validateMediationClient2(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['DATE_OF_BIRTH']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_IN_FUTURE_CODE);
      assertThat(result.getFieldError("inputs['DATE_OF_BIRTH']").getArguments()[0])
          .isEqualTo("Date of birth");
    }

    private Errors validateMediationClient2(Map<String, String> inputs) {
      ClaimDetails claimDetails = MockClaimsFunctions.createMockMediationClaim();

      var form = new AmendmentForm();
      form.setInputs(inputs);

      var errors = new BeanPropertyBindingResult(form, "amendmentForm");
      validator.validate(claimDetails, MediationClaimDetailsViewField.DATE_OF_BIRTH, form, errors);
      return errors;
    }
  }

  @Nested
  class MediationClient2 {

    @Test
    void testAppliesTo() {
      assertTrue(validator.appliesTo(MediationClaimDetailsViewField.CLIENT_2_DATE_OF_BIRTH));
    }

    @ParameterizedTest
    @MethodSource(
        "uk.gov.justice.laa.amend.claim.forms.amendments.validators"
            + ".InvalidDateArgumentsProvider#invalidDateProvider")
    void shouldNotAddErrorsForInvalidDates(String day, String month, String year) {
      Map<String, String> input =
          Map.of(
              "CLIENT_2_DATE_OF_BIRTH-day", day,
              "CLIENT_2_DATE_OF_BIRTH-month", month,
              "CLIENT_2_DATE_OF_BIRTH-year", year);

      Errors result = validateMediation(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForValidDate() {
      Map<String, String> input =
          Map.of(
              "CLIENT_2_DATE_OF_BIRTH-day", "20",
              "CLIENT_2_DATE_OF_BIRTH-month", "5",
              "CLIENT_2_DATE_OF_BIRTH-year", "2013");

      Errors result = validateMediation(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldNotAddErrorsForFirstValidDate() {
      Map<String, String> input =
          Map.of(
              "CLIENT_2_DATE_OF_BIRTH-day", "1",
              "CLIENT_2_DATE_OF_BIRTH-month", "1",
              "CLIENT_2_DATE_OF_BIRTH-year", "1900");

      Errors result = validateMediation(input);

      assertThat(result.hasFieldErrors()).isFalse();
    }

    @Test
    void shouldAddErrorsForOldDate() {
      Map<String, String> input =
          Map.of(
              "CLIENT_2_DATE_OF_BIRTH-day", "31",
              "CLIENT_2_DATE_OF_BIRTH-month", "12",
              "CLIENT_2_DATE_OF_BIRTH-year", "1899");

      Errors result = validateMediation(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['CLIENT_2_DATE_OF_BIRTH']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_BEFORE_CODE);
      assertThat(result.getFieldError("inputs['CLIENT_2_DATE_OF_BIRTH']").getArguments()[0])
          .isEqualTo("Date of birth");
      assertThat(result.getFieldError("inputs['CLIENT_2_DATE_OF_BIRTH']").getArguments()[1])
          .isEqualTo("1 January 1900");
    }

    @Test
    void shouldAddErrorsForDateInFuture() {
      when(dateWrapperUtil.isFutureDate(any())).thenReturn(true);
      Map<String, String> input =
          Map.of(
              "CLIENT_2_DATE_OF_BIRTH-day", "1",
              "CLIENT_2_DATE_OF_BIRTH-month", "1",
              "CLIENT_2_DATE_OF_BIRTH-year", "2020");

      Errors result = validateMediation(input);

      assertThat(result.hasFieldErrors()).isTrue();
      assertThat(result.getFieldError("inputs['CLIENT_2_DATE_OF_BIRTH']").getCode())
          .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_IN_FUTURE_CODE);
      assertThat(result.getFieldError("inputs['CLIENT_2_DATE_OF_BIRTH']").getArguments()[0])
          .isEqualTo("Date of birth");
    }

    private Errors validateMediation(Map<String, String> inputs) {
      ClaimDetails claimDetails = MockClaimsFunctions.createMockMediationClaim();

      var form = new AmendmentForm();
      form.setInputs(inputs);

      var errors = new BeanPropertyBindingResult(form, "amendmentForm");
      validator.validate(
          claimDetails, MediationClaimDetailsViewField.CLIENT_2_DATE_OF_BIRTH, form, errors);
      return errors;
    }
  }
}
