package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;

class BigDecimalAmendmentFieldValidatorTest {

  private final BigDecimalAmendmentFieldValidator validator =
      new BigDecimalAmendmentFieldValidator();

  @Test
  void acceptsWellFormedBigDecimalValue() {
    var errors = validate(Map.of("VALUE_OF_COSTS", "12.34"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedBigDecimalValue() {
    var errors = validate(Map.of("VALUE_OF_COSTS", "not-a-number"));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[VALUE_OF_COSTS]").getCode())
        .isEqualTo("amendmentForm.invalidValue");
  }

  @Test
  void rejectsBigDecimalWithMoreThanTwoDecimalPlaces() {
    var errors = validate(Map.of("VALUE_OF_COSTS", "12.345"));

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[VALUE_OF_COSTS]").getCode())
        .isEqualTo("amendmentForm.invalidValue");
  }

  private Errors validate(Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(CivilClaimDetailsViewField.VALUE_OF_COSTS, form, errors);
    return errors;
  }
}
