package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;

class TextAmendmentFieldValidatorTest {

  private final TextAmendmentFieldValidator validator = new TextAmendmentFieldValidator();

  @Test
  void acceptsTextValueAtMaxLength() {
    var value = "a".repeat(TextAmendmentFieldValidator.MAX_TEXT_LENGTH);
    var errors = validate(Map.of("UNIQUE_FILE_NUMBER", value));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsTextValueOverMaxLength() {
    var value = "a".repeat(TextAmendmentFieldValidator.MAX_TEXT_LENGTH + 1);
    var errors = validate(Map.of("UNIQUE_FILE_NUMBER", value));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[UNIQUE_FILE_NUMBER]").getCode())
        .isEqualTo("amendmentForm.tooLong");
  }

  private Errors validate(Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(ClaimDetailsViewField.UNIQUE_FILE_NUMBER, form, errors);
    return errors;
  }
}
