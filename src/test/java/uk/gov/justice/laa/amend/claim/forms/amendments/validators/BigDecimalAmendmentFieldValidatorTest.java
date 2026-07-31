package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;

class BigDecimalAmendmentFieldValidatorTest {

  private final BigDecimalAmendmentFieldValidator validator =
      new BigDecimalAmendmentFieldValidator(TestMessageSources.real());

  @Test
  void acceptsWellFormedBigDecimalValue() {
    var errors = validate(CivilClaimDetailsViewField.VALUE_OF_COSTS, "12.34");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedBigDecimalValueNamingTheField() {
    var errors = validate(CivilClaimDetailsViewField.VALUE_OF_COSTS, "not-a-number");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[VALUE_OF_COSTS]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.invalid");
    assertThat(fieldError.getDefaultMessage())
        .isEqualTo("Value of costs or damages recovered must be entered as a valid amount");
  }

  @Test
  void rejectsMalformedBigDecimalValueNamingADifferentField() {
    var errors = validate(CivilClaimDetailsViewField.COUNSELS_COST, "not-a-number");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[COUNSELS_COST]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.invalid");
    assertThat(fieldError.getDefaultMessage())
        .isEqualTo("Net cost of counsel must be entered as a valid amount");
  }

  @Test
  void rejectsBigDecimalWithMoreThanTwoDecimalPlaces() {
    var errors = validate(CivilClaimDetailsViewField.VALUE_OF_COSTS, "12.345");

    assertThat(errors.hasErrors()).isTrue();
    assertThat(errors.getFieldError("inputs[VALUE_OF_COSTS]").getCode())
        .isEqualTo("amendmentForm.bigDecimal.invalid");
  }

  @Test
  void acceptsBlankBigDecimalValue() {
    var errors = validate(CivilClaimDetailsViewField.VALUE_OF_COSTS, "");

    assertThat(errors.hasErrors()).isFalse();
  }

  private Errors validate(CivilClaimDetailsViewField field, String value) {
    var form = new AmendmentForm();
    form.setInputs(Map.of(field.name(), value));

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(field, form, errors);
    return errors;
  }
}
