package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

class DateAmendmentFieldValidatorTest {

  private final DateAmendmentFieldValidator validator =
      new DateAmendmentFieldValidator(TestMessageSources.real());

  @Test
  void acceptsCompleteWellFormedDate() {
    var errors =
        validate(
            ClaimDetailsViewField.CASE_START_DATE,
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
            ClaimDetailsViewField.CASE_START_DATE,
            Map.of(
                "CASE_START_DATE-day", "",
                "CASE_START_DATE-month", "",
                "CASE_START_DATE-year", ""));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsPartiallyFilledDateNamingTheField() {
    var errors =
        validate(
            ClaimDetailsViewField.CASE_START_DATE,
            Map.of(
                "CASE_START_DATE-day", "1",
                "CASE_START_DATE-month", "",
                "CASE_START_DATE-year", ""));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CASE_START_DATE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.date.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Case start date");
  }

  @Test
  void rejectsImpossibleDateNamingDifferentField() {
    var errors =
        validate(
            CrimeClaimDetailsViewField.CASE_CONCLUDED_DATE,
            Map.of(
                "CASE_CONCLUDED_DATE-day", "31",
                "CASE_CONCLUDED_DATE-month", "2",
                "CASE_CONCLUDED_DATE-year", "2025"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CASE_CONCLUDED_DATE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.date.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Case concluded date");
  }

  @Test
  void acceptsWellFormedCivilDateOfBirth() {
    var errors =
        validate(
            CivilClaimDetailsViewField.DATE_OF_BIRTH,
            Map.of(
                "DATE_OF_BIRTH-day", "15",
                "DATE_OF_BIRTH-month", "3",
                "DATE_OF_BIRTH-year", "1990"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsImpossibleCivilDateOfBirthNamingTheField() {
    var errors =
        validate(
            CivilClaimDetailsViewField.DATE_OF_BIRTH,
            Map.of(
                "DATE_OF_BIRTH-day", "31",
                "DATE_OF_BIRTH-month", "2",
                "DATE_OF_BIRTH-year", "1990"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[DATE_OF_BIRTH]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.date.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Date of birth");
  }

  @Test
  void acceptsWellFormedMediationDateOfBirth() {
    var errors =
        validate(
            MediationClaimDetailsViewField.DATE_OF_BIRTH,
            Map.of(
                "DATE_OF_BIRTH-day", "15",
                "DATE_OF_BIRTH-month", "3",
                "DATE_OF_BIRTH-year", "1990"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsImpossibleMediationDateOfBirthNamingTheField() {
    var errors =
        validate(
            MediationClaimDetailsViewField.DATE_OF_BIRTH,
            Map.of(
                "DATE_OF_BIRTH-day", "31",
                "DATE_OF_BIRTH-month", "2",
                "DATE_OF_BIRTH-year", "1990"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[DATE_OF_BIRTH]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.date.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Date of birth");
  }

  private Errors validate(ClaimViewField<?> field, Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(field, form, errors);
    return errors;
  }
}
