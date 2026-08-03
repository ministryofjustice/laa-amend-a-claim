package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;

class TextAmendmentFieldValidatorTest {

  private final TextAmendmentFieldValidator validator =
      new TextAmendmentFieldValidator(TestMessageSources.real());

  @Test
  void acceptsTextValueAtMaxLength() {
    var value = "a".repeat(TextAmendmentFieldValidator.MAX_TEXT_LENGTH);
    var errors =
        validate(ClaimDetailsViewField.UNIQUE_FILE_NUMBER, Map.of("UNIQUE_FILE_NUMBER", value));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsTextValueOverMaxLengthNamingTheField() {
    var value = "a".repeat(TextAmendmentFieldValidator.MAX_TEXT_LENGTH + 1);
    var errors =
        validate(ClaimDetailsViewField.UNIQUE_FILE_NUMBER, Map.of("UNIQUE_FILE_NUMBER", value));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[UNIQUE_FILE_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.tooLong");
    assertThat(fieldError.getDefaultMessage())
        .isEqualTo("Unique file number (UFN) must be 50 characters or less");
  }

  @Test
  void rejectsTextValueOverMaxLengthNamingDifferentField() {
    var value = "a".repeat(TextAmendmentFieldValidator.MAX_TEXT_LENGTH + 1);
    var errors =
        validate(
            ClaimDetailsViewField.CASE_REFERENCE_NUMBER, Map.of("CASE_REFERENCE_NUMBER", value));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CASE_REFERENCE_NUMBER]");
    assertThat(fieldError.getDefaultMessage())
        .isEqualTo("Case reference number (CRN) must be 50 characters or less");
  }

  private Errors validate(ClaimDetailsViewField field, Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(field, form, errors);
    return errors;
  }
}
