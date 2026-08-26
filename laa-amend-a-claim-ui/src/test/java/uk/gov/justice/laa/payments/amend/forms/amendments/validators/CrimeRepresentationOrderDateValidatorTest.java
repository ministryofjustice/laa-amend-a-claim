package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.support.TestMessageSources;
import uk.gov.justice.laa.payments.amend.utils.DateWrapperUtil;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField;

@ExtendWith(MockitoExtension.class)
class CrimeRepresentationOrderDateValidatorTest {

  CrimeRepresentationOrderDateValidator validator;

  @Mock DateWrapperUtil dateWrapperUtil;

  @BeforeEach
  void beforeEach() {
    validator =
        new CrimeRepresentationOrderDateValidator(TestMessageSources.real(), dateWrapperUtil);
  }

  @Test
  void testAppliesTo() {
    assertTrue(validator.appliesTo(CrimeClaimDetailsViewField.REPRESENTATION_ORDER_DATE));
  }

  @ParameterizedTest
  @MethodSource(
      "uk.gov.justice.laa.payments.amend.forms.amendments.validators.InvalidDateArgumentsProvider#invalidDateProvider")
  void shouldNotAddErrorsForInvalidDates(String day, String month, String year) {
    Map<String, String> input =
        Map.of(
            "REPRESENTATION_ORDER_DATE-day", day,
            "REPRESENTATION_ORDER_DATE-month", month,
            "REPRESENTATION_ORDER_DATE-year", year);

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldNotAddErrorsForValidDate() {
    Map<String, String> input =
        Map.of(
            "REPRESENTATION_ORDER_DATE-day", "15",
            "REPRESENTATION_ORDER_DATE-month", "12",
            "REPRESENTATION_ORDER_DATE-year", "2020");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldNotAddErrorsForFirstValidDate() {
    Map<String, String> input =
        Map.of(
            "REPRESENTATION_ORDER_DATE-day", "1",
            "REPRESENTATION_ORDER_DATE-month", "4",
            "REPRESENTATION_ORDER_DATE-year", "2016");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldAddErrorsForOldDate() {
    Map<String, String> input =
        Map.of(
            "REPRESENTATION_ORDER_DATE-day", "31",
            "REPRESENTATION_ORDER_DATE-month", "3",
            "REPRESENTATION_ORDER_DATE-year", "2016");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isTrue();
    assertThat(result.getFieldError("inputs['REPRESENTATION_ORDER_DATE']").getCode())
        .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_BEFORE_CODE);
    assertThat(result.getFieldError("inputs['REPRESENTATION_ORDER_DATE']").getArguments()[0])
        .isEqualTo("Representation order date");
    assertThat(result.getFieldError("inputs['REPRESENTATION_ORDER_DATE']").getArguments()[1])
        .isEqualTo("1 April 2016");
  }

  @Test
  void shouldAddErrorsForDateInFuture() {
    when(dateWrapperUtil.isFutureDate(any())).thenReturn(true);
    Map<String, String> input =
        Map.of(
            "REPRESENTATION_ORDER_DATE-day", "2",
            "REPRESENTATION_ORDER_DATE-month", "1",
            "REPRESENTATION_ORDER_DATE-year", "2020");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isTrue();
    assertThat(result.getFieldError("inputs['REPRESENTATION_ORDER_DATE']").getCode())
        .isEqualTo(AmendmentDateValidator.DATE_CANT_BE_IN_FUTURE_CODE);
    assertThat(result.getFieldError("inputs['REPRESENTATION_ORDER_DATE']").getArguments()[0])
        .isEqualTo("Representation order date");
  }

  private Errors validate(Map<String, String> inputs) {
    ClaimDetails claimDetails = MockClaimsFunctions.createMockCrimeClaim();
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(
        claimDetails, CrimeClaimDetailsViewField.REPRESENTATION_ORDER_DATE, form, errors);
    return errors;
  }
}
