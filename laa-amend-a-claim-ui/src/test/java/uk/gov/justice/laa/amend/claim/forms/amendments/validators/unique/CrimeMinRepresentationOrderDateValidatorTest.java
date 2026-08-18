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
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;

class CrimeMinRepresentationOrderDateValidatorTest {

  CrimeMinRepresentationOrderDateValidator validator;

  @BeforeEach
  void beforeEach() {
    validator = new CrimeMinRepresentationOrderDateValidator(TestMessageSources.real());
  }

  @Test
  void testAppliesTo() {
    assertTrue(validator.appliesTo(CrimeClaimDetailsViewField.REPRESENTATION_ORDER_DATE));
  }

  @ParameterizedTest
  @MethodSource(
      "uk.gov.justice.laa.amend.claim.forms.amendments.validators.InvalidDateArgumentsProvider#invalidDateProvider")
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
        .isEqualTo(FieldSpecificAmendmentValidator.DATE_CANT_BE_BEFORE_CODE);
    assertThat(result.getFieldError("inputs['REPRESENTATION_ORDER_DATE']").getArguments()[0])
        .isEqualTo("Representation order date");
    assertThat(result.getFieldError("inputs['REPRESENTATION_ORDER_DATE']").getArguments()[1])
        .isEqualTo("1 April 2016");
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
