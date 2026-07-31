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
    var errors = validate(Map.of("IS_DUTY_SOLICITOR", "yes"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedBooleanValue() {
    var errors = validate(Map.of("IS_DUTY_SOLICITOR", "banana"));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[IS_DUTY_SOLICITOR]").getCode())
        .isEqualTo("amendmentForm.invalidValue");
  }

  private Errors validate(Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(CrimeClaimDetailsViewField.IS_DUTY_SOLICITOR, form, errors);
    return errors;
  }
}
