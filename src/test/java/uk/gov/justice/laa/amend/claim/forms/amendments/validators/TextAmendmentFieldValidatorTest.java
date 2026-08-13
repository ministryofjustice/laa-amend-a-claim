package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;

class TextAmendmentFieldValidatorTest {

  private final TextAmendmentFieldValidator validator =
      new TextAmendmentFieldValidator(TestMessageSources.real());

  @Test
  void acceptsTextValueAtMaxLength() {
    var value = "a".repeat(TextAmendmentFieldValidator.MAX_TEXT_LENGTH);
    var errors =
        validate(
            CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER, Map.of("UNIQUE_FILE_NUMBER", value));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsTextValueOverMaxLengthNamingTheField() {
    var value = "a".repeat(TextAmendmentFieldValidator.MAX_TEXT_LENGTH + 1);
    var errors =
        validate(
            CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER, Map.of("UNIQUE_FILE_NUMBER", value));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[UNIQUE_FILE_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.tooLong");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Unique file number (UFN)");
    assertThat(fieldError.getArguments()[1]).isEqualTo("50");
  }

  @Test
  void rejectsTextValueOverMaxLengthNamingDifferentField() {
    var value = "a".repeat(TextAmendmentFieldValidator.MAX_TEXT_LENGTH + 1);
    var errors =
        validate(
            ClaimDetailsViewField.CASE_REFERENCE_NUMBER, Map.of("CASE_REFERENCE_NUMBER", value));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CASE_REFERENCE_NUMBER]");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Case reference number (CRN)");
    assertThat(fieldError.getArguments()[1]).isEqualTo("50");
  }

  private Errors validate(ClaimViewField<?> field, Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(field, form, errors);
    return errors;
  }
}
