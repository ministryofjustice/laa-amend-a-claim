package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;

class AmendmentFormValidatorTest {

  @Test
  void acceptsTextValueAtMaxLength() {
    var value = "a".repeat(AmendmentFormValidator.MAX_TEXT_LENGTH);
    var errors = validate(CrimeClaimDetails.class, Map.of("UNIQUE_FILE_NUMBER", value));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsTextValueOverMaxLength() {
    var value = "a".repeat(AmendmentFormValidator.MAX_TEXT_LENGTH + 1);
    var errors = validate(CrimeClaimDetails.class, Map.of("UNIQUE_FILE_NUMBER", value));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[UNIQUE_FILE_NUMBER]").getCode())
        .isEqualTo("amendmentForm.tooLong");
  }

  @Test
  void acceptsEnumValueMatchingAnAllowedOption() {
    var errors = validate(CrimeClaimDetails.class, Map.of("STAGE_REACHED", "INVA"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsEnumValueNotMatchingAnAllowedOption() {
    var errors = validate(CrimeClaimDetails.class, Map.of("STAGE_REACHED", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[STAGE_REACHED]").getCode())
        .isEqualTo("amendmentForm.invalidOption");
  }

  @Test
  void acceptsBlankEnumValue() {
    var errors = validate(CrimeClaimDetails.class, Map.of("STAGE_REACHED", ""));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsWellFormedNumberValue() {
    var errors = validate(CrimeClaimDetails.class, Map.of("SUSPECTS_DEFENDANTS_COUNT", "3"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedNumberValue() {
    var errors = validate(CrimeClaimDetails.class, Map.of("SUSPECTS_DEFENDANTS_COUNT", "abc"));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[SUSPECTS_DEFENDANTS_COUNT]").getCode())
        .isEqualTo("amendmentForm.invalidValue");
  }

  @Test
  void acceptsWellFormedBigDecimalValue() {
    var errors = validate(CivilClaimDetails.class, Map.of("VALUE_OF_COSTS", "12.34"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedBigDecimalValue() {
    var errors = validate(CivilClaimDetails.class, Map.of("VALUE_OF_COSTS", "not-a-number"));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[VALUE_OF_COSTS]").getCode())
        .isEqualTo("amendmentForm.invalidValue");
  }

  @Test
  void acceptsWellFormedBooleanValue() {
    var errors = validate(CrimeClaimDetails.class, Map.of("IS_DUTY_SOLICITOR", "yes"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedBooleanValue() {
    var errors = validate(CrimeClaimDetails.class, Map.of("IS_DUTY_SOLICITOR", "banana"));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[IS_DUTY_SOLICITOR]").getCode())
        .isEqualTo("amendmentForm.invalidValue");
  }

  @Test
  void acceptsCompleteWellFormedDate() {
    var errors =
        validate(
            MediationClaimDetails.class,
            Map.of(
                "CASE_START_DATE-day", "1",
                "CASE_START_DATE-month", "6",
                "CASE_START_DATE-year", "2025"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsCompletelyBlankDate() {
    var errors =
        validate(
            MediationClaimDetails.class,
            Map.of(
                "CASE_START_DATE-day", "",
                "CASE_START_DATE-month", "",
                "CASE_START_DATE-year", ""));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsPartiallyFilledDate() {
    var errors =
        validate(
            MediationClaimDetails.class,
            Map.of(
                "CASE_START_DATE-day", "1",
                "CASE_START_DATE-month", "",
                "CASE_START_DATE-year", ""));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[CASE_START_DATE]").getCode())
        .isEqualTo("amendmentForm.invalidValue");
  }

  @Test
  void rejectsImpossibleDate() {
    var errors =
        validate(
            MediationClaimDetails.class,
            Map.of(
                "CASE_START_DATE-day", "31",
                "CASE_START_DATE-month", "2",
                "CASE_START_DATE-year", "2025"));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[CASE_START_DATE]").getCode())
        .isEqualTo("amendmentForm.invalidValue");
  }

  @Test
  void resolvesFieldRulesAccordingToTheClaimsAreaOfLaw() {
    // STANDARD_FEE_CATEGORY is declared only on CrimeClaimDetailsViewField, so validating it
    // requires the crime-specific field definition - passing the wrong claim type must not
    // silently apply a generic/shared rule.
    var inputs = Map.of("STANDARD_FEE_CATEGORY", "1A");

    var validForCrime = validate(CrimeClaimDetails.class, inputs);
    assertThat(validForCrime.hasErrors()).isFalse();

    var invalidCodeForCrime = validate(CrimeClaimDetails.class, Map.of("STANDARD_FEE_CATEGORY", "NOPE"));
    assertThat(invalidCodeForCrime.hasErrors()).isTrue();

    assertThatThrownBy(() -> validate(CivilClaimDetails.class, inputs))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private Errors validate(Class<?> claimDetailsType, Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    new AmendmentFormValidator(claimDetailsType).validate(form, errors);
    return errors;
  }
}
