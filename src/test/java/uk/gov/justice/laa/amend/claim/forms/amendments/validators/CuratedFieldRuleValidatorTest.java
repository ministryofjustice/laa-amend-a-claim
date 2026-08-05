package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

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
    assertThat(validator.appliesTo(ClaimDetailsViewField.DISABILITY)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.POSTCODE)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.POSTCODE)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.UNIQUE_CLIENT_NUMBER)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.UNIQUE_CLIENT_NUMBER)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.HOME_OFFICE_CLIENT_NUMBER)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.DATE_OF_BIRTH)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.CLIENT_TYPE)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT)).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"SW1A 1AA", "sw1a1aa", "EC1A1BB", "NFA", "nfa", "M1 1AE"})
  void acceptsValidCivilPostcodeValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.POSTCODE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"SW1A 1AA", "sw1a1aa", "EC1A1BB", "NFA", "nfa", "M1 1AE"})
  void acceptsValidMediationPostcodeValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.POSTCODE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"NOTAPOSTCODE", "SW1A", "12345", "AB1 2C"})
  void rejectsInvalidCivilPostcodeValuesNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.POSTCODE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[POSTCODE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.postcode.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Postcode");
  }

  @ParameterizedTest
  @ValueSource(strings = {"NOTAPOSTCODE", "SW1A", "12345", "AB1 2C"})
  void rejectsInvalidMediationPostcodeValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.POSTCODE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[POSTCODE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.postcode.invalidFormat");
  }

  @ParameterizedTest
  @ValueSource(strings = {"01011999/A/BCD1", "31129999/X/1", "15061985/&/A-1"})
  void acceptsValidCivilUniqueClientNumberValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.UNIQUE_CLIENT_NUMBER, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"01011999/A/BCD1", "31129999/X/1", "15061985/&/A-1"})
  void acceptsValidMediationUniqueClientNumberValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.UNIQUE_CLIENT_NUMBER, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"3213 1999/A/BCD1", "01011999-A-BCD1", "abcd1999/A/BCD1", "01011999/AB/BCD1"})
  void rejectsInvalidCivilUniqueClientNumberValuesNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.UNIQUE_CLIENT_NUMBER, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[UNIQUE_CLIENT_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.uniqueClientNumber.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Unique client number (UCN)");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"3213 1999/A/BCD1", "01011999-A-BCD1", "abcd1999/A/BCD1", "01011999/AB/BCD1"})
  void rejectsInvalidMediationUniqueClientNumberValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.UNIQUE_CLIENT_NUMBER, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[UNIQUE_CLIENT_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.uniqueClientNumber.invalidFormat");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB123456", "12345678901234", "a1b2c3"})
  void acceptsValidHomeOfficeClientNumberValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.HOME_OFFICE_CLIENT_NUMBER, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsHomeOfficeClientNumberAtMaxLength() {
    var errors = validate(CivilClaimDetailsViewField.HOME_OFFICE_CLIENT_NUMBER, "a".repeat(16));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsHomeOfficeClientNumberOverMaxLengthNamingTheFieldAndLimit() {
    var errors = validate(CivilClaimDetailsViewField.HOME_OFFICE_CLIENT_NUMBER, "a".repeat(17));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[HOME_OFFICE_CLIENT_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.tooLong");
    assertThat(fieldError.getArguments()[1]).isEqualTo("16");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB-1234", "AB 1234", "AB_1234"})
  void rejectsHomeOfficeClientNumberWithDisallowedCharactersNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.HOME_OFFICE_CLIENT_NUMBER, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[HOME_OFFICE_CLIENT_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Home Office unique client number (HO UCN)");
  }

  @Test
  void homeOfficeClientNumberFormatFailureTakesPriorityOverLengthFailure() {
    // Over the 16 char limit AND contains a disallowed character - FORMAT outranks LENGTH.
    var errors = validate(CivilClaimDetailsViewField.HOME_OFFICE_CLIENT_NUMBER, "-".repeat(17));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[HOME_OFFICE_CLIENT_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
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

  private Errors validate(ClaimViewField<?> field, String value) {
    var form = new AmendmentForm();
    form.setInputs(Map.of(field.name(), value));

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(MockClaimsFunctions.createMockCrimeClaim(), field, form, errors);
    return errors;
  }
}
