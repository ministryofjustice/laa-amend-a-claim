package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;

class BooleanAmendmentFieldValidatorTest {

  private final BooleanAmendmentFieldValidator validator =
      new BooleanAmendmentFieldValidator(TestMessageSources.real());

  @Test
  void acceptsWellFormedBooleanValue() {
    var errors = validate(CrimeClaimDetailsViewField.IS_DUTY_SOLICITOR, "yes");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedBooleanValueNamingTheField() {
    var errors = validate(CrimeClaimDetailsViewField.IS_DUTY_SOLICITOR, "banana");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[IS_DUTY_SOLICITOR]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.boolean.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Duty solicitor");
  }

  @Test
  void rejectsMalformedBooleanValueNamingDifferentField() {
    var errors = validate(CrimeClaimDetailsViewField.IS_YOUTH_COURT, "banana");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[IS_YOUTH_COURT]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.boolean.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Youth court");
  }

  @Test
  void acceptsBlankBooleanValue() {
    var errors = validate(CrimeClaimDetailsViewField.IS_DUTY_SOLICITOR, "");

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
