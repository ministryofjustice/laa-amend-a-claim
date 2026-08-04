package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;

class CuratedFieldRuleValidatorTest {

  private final CuratedFieldRuleValidator validator =
      new CuratedFieldRuleValidator(TestMessageSources.real());

  @Test
  void appliesOnlyToCuratedFields() {
    assertThat(validator.appliesTo(ClaimDetailsViewField.INITIAL)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.FORENAME)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.SURNAME)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.FEE_CODE)).isFalse();
    assertThat(validator.appliesTo(ClaimDetailsViewField.GENDER)).isFalse();
  }

  @ParameterizedTest
  @CsvSource({
    "INITIAL, J",
    "FORENAME, Jean-Paul",
    "SURNAME, O'Brien & Sons",
    "SURNAME, Smith",
    "INITIAL, 'a'"
  })
  void acceptsRepresentativeValidValues(ClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({"INITIAL", "FORENAME", "SURNAME"})
  void acceptsValueAtMaxLength(ClaimDetailsViewField field) {
    var errors = validate(field, "a".repeat(30));

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({"INITIAL", "FORENAME", "SURNAME"})
  void rejectsValueOverMaxLengthNamingTheFieldAndLimit(ClaimDetailsViewField field) {
    var errors = validate(field, "a".repeat(31));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[%s]".formatted(field.name()));
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.tooLong");
    assertThat(fieldError.getArguments()[1]).isEqualTo("30");
  }

  @ParameterizedTest
  @CsvSource({"INITIAL, J@ne", "FORENAME, 'Jean_Paul'", "SURNAME, 'Smith#'", "SURNAME, 'Smith%'"})
  void rejectsValuesWithDisallowedCharacters(ClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[%s]".formatted(field.name()));
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.invalidFormat");
  }

  @Test
  void formatFailureTakesPriorityOverLengthFailure() {
    // Over the 30 char limit AND contains a disallowed character - FORMAT outranks LENGTH.
    var errors = validate(ClaimDetailsViewField.SURNAME, "#".repeat(31));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[SURNAME]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.invalidFormat");
  }

  @Test
  void rejectedValuesCarryTheFieldLabelAsTheFirstArgument() {
    var errors = validate(ClaimDetailsViewField.SURNAME, "#".repeat(31));

    var fieldError = errors.getFieldError("inputs[SURNAME]");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Last name");
  }

  @Test
  void skipsBlankValuesLeavingRequirednessToTheGenericLayer() {
    var errors = validate(ClaimDetailsViewField.SURNAME, "");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void doesNotAddSecondErrorWhenOneAlreadyExists() {
    var form = new AmendmentForm();
    form.setInputs(Map.of("SURNAME", "#".repeat(31)));

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    errors.rejectValue("inputs[SURNAME]", "some.other.code");

    validator.validate(
        MockClaimsFunctions.createMockCrimeClaim(), ClaimDetailsViewField.SURNAME, form, errors);

    assertThat(errors.getFieldErrors("inputs[SURNAME]")).hasSize(1);
    assertThat(errors.getFieldError("inputs[SURNAME]").getCode()).isEqualTo("some.other.code");
  }

  private Errors validate(ClaimDetailsViewField field, String value) {
    var form = new AmendmentForm();
    form.setInputs(Map.of(field.name(), value));

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(MockClaimsFunctions.createMockCrimeClaim(), field, form, errors);
    return errors;
  }
}
