package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;

class EnumAmendmentFieldValidatorTest {

  private final EnumAmendmentFieldValidator validator = new EnumAmendmentFieldValidator();

  @Test
  void acceptsEnumValueMatchingAnAllowedOption() {
    var errors = validate(Map.of("GENDER", "M"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsEnumValueNotMatchingAnAllowedOption() {
    var errors = validate(Map.of("GENDER", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[GENDER]").getCode())
        .isEqualTo("amendmentForm.invalidOption");
  }

  @Test
  void acceptsBlankEnumValue() {
    var errors = validate(Map.of("GENDER", ""));

    assertThat(errors.hasErrors()).isFalse();
  }

  private Errors validate(Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(ClaimDetailsViewField.GENDER, form, errors);
    return errors;
  }
}
