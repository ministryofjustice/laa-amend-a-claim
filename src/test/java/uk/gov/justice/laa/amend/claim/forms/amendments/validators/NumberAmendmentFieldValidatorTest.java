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
    var errors = validate(Map.of("SUSPECTS_DEFENDANTS_COUNT", "3"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedNumberValue() {
    var errors = validate(Map.of("SUSPECTS_DEFENDANTS_COUNT", "abc"));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[SUSPECTS_DEFENDANTS_COUNT]").getCode())
        .isEqualTo("amendmentForm.invalidValue");
  }

  private Errors validate(Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(CrimeClaimDetailsViewField.SUSPECTS_DEFENDANTS_COUNT, form, errors);
    return errors;
  }
}
