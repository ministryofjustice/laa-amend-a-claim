package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;

class NumberAmendmentFieldValidatorTest {

  private final NumberAmendmentFieldValidator validator =
      new NumberAmendmentFieldValidator(TestMessageSources.real());

  @Test
  void acceptsWellFormedNumberValue() {
    var errors = validate(CrimeClaimDetailsViewField.SUSPECTS_DEFENDANTS_COUNT, "3");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedNumberValueNamingTheField() {
    var errors = validate(CrimeClaimDetailsViewField.SUSPECTS_DEFENDANTS_COUNT, "abc");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[SUSPECTS_DEFENDANTS_COUNT]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.invalid");
    assertThat(fieldError.getDefaultMessage())
        .isEqualTo("Number of suspects or defendants must be entered as numbers only");
  }

  @Test
  void rejectsMalformedNumberValueNamingDifferentField() {
    var errors = validate(CrimeClaimDetailsViewField.POLICE_STATION_COURT_ATTENDANCES_COUNT, "abc");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[POLICE_STATION_COURT_ATTENDANCES_COUNT]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.invalid");
    assertThat(fieldError.getDefaultMessage())
        .isEqualTo("Number of police station or court attendances must be entered as numbers only");
  }

  @Test
  void acceptsBlankNumberValue() {
    var errors = validate(CrimeClaimDetailsViewField.SUSPECTS_DEFENDANTS_COUNT, "");

    assertThat(errors.hasErrors()).isFalse();
  }

  private Errors validate(CrimeClaimDetailsViewField field, String value) {
    var form = new AmendmentForm();
    form.setInputs(Map.of(field.name(), value));

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(field, form, errors);
    return errors;
  }
}
