package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;

class DateAmendmentFieldValidatorTest {

  private final DateAmendmentFieldValidator validator = new DateAmendmentFieldValidator();

  @Test
  void acceptsCompleteWellFormedDate() {
    var errors =
        validate(
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
            Map.of(
                "CASE_START_DATE-day", "31",
                "CASE_START_DATE-month", "2",
                "CASE_START_DATE-year", "2025"));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[CASE_START_DATE]").getCode())
        .isEqualTo("amendmentForm.invalidValue");
  }

  private Errors validate(Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(ClaimDetailsViewField.CASE_START_DATE, form, errors);
    return errors;
  }
}
