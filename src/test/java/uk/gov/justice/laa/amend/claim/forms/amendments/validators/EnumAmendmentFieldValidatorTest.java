package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;

class EnumAmendmentFieldValidatorTest {

  private final EnumAmendmentFieldValidator validator =
      new EnumAmendmentFieldValidator(TestMessageSources.real());

  @Test
  void acceptsEnumValueMatchingAnAllowedOption() {
    var errors = validate(ClaimDetailsViewField.GENDER, Map.of("GENDER", "M"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsEnumValueNotMatchingAnAllowedOptionNamingTheField() {
    var errors = validate(ClaimDetailsViewField.GENDER, Map.of("GENDER", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[GENDER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.enum.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Gender");
  }

  @Test
  void rejectsEnumValueNotMatchingAnAllowedOptionNamingDifferentField() {
    var errors = validate(ClaimDetailsViewField.ETHNICITY, Map.of("ETHNICITY", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[ETHNICITY]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.enum.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Ethnicity");
  }

  @Test
  void acceptsBlankEnumValue() {
    var errors = validate(ClaimDetailsViewField.GENDER, Map.of("GENDER", ""));

    assertThat(errors.hasErrors()).isFalse();
  }

  private Errors validate(ClaimDetailsViewField field, Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(field, form, errors);
    return errors;
  }
}
