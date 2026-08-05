package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AvailableFeeCodesService;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

class CuratedFieldRuleValidatorTest {

  private final CuratedFieldRuleValidator validator =
      new CuratedFieldRuleValidator(TestMessageSources.real());

  @Test
  void appliesOnlyToCuratedFields() {
    assertThat(validator.appliesTo(ClaimDetailsViewField.INITIAL)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.FORENAME)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.SURNAME)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.FEE_CODE)).isTrue();
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
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.CLIENT_2_FORENAME)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.CLIENT_2_SURNAME)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.CLIENT_2_POSTCODE)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.CLIENT_2_UCN)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.CLIENT_2_DATE_OF_BIRTH))
        .isFalse();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.CLIENT_2_GENDER)).isFalse();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.CLIENT_2_ETHNICITY)).isFalse();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.CLIENT_2_DISABILITY)).isFalse();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.IS_CLIENT_2_LEGALLY_AIDED))
        .isFalse();
    assertThat(
            validator.appliesTo(
                MediationClaimDetailsViewField.IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED))
        .isFalse();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.MATTER_TYPE_CODE)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.MATTER_TYPE_CODE_1)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.MATTER_TYPE_CODE_2)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.MATTER_TYPE_CODE_1)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.MATTER_TYPE_CODE_2)).isTrue();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.STAGE_REACHED)).isFalse();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.REPRESENTATION_ORDER_DATE)).isFalse();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.STANDARD_FEE_CATEGORY)).isFalse();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.OUTCOME_FOR_CLIENT)).isFalse();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.IS_DUTY_SOLICITOR)).isFalse();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.IS_YOUTH_COURT)).isFalse();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.SUSPECTS_DEFENDANTS_COUNT)).isTrue();
    assertThat(
            validator.appliesTo(CrimeClaimDetailsViewField.POLICE_STATION_COURT_ATTENDANCES_COUNT))
        .isTrue();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.POLICE_STATION_COURT_PRISON_ID))
        .isTrue();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.SCHEME_ID)).isTrue();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.DSCC_NUMBER)).isTrue();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.MAAT_ID)).isTrue();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.PRISON_LAW_PRIOR_APPROVAL_NUMBER))
        .isTrue();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.TRAVEL_COSTS)).isTrue();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.WAITING_COSTS)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.CLAIM_ID)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.UNIQUE_CASE_ID)).isFalse();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.MEDIATION_SESSIONS_COUNT))
        .isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.MEDIATION_TIME_MINUTES)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.OUTCOME)).isFalse();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.OUTREACH_LOCATION)).isTrue();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.REFERRAL_SOURCE)).isFalse();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.SCHEDULE_REFERENCE)).isFalse();
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
  @ValueSource(strings = {"SW1A 1AA", "sw1a1aa", "EC1A1BB", "NFA", "nfa", "M1 1AE"})
  void acceptsValidClient2PostcodeValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.CLIENT_2_POSTCODE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"NOTAPOSTCODE", "SW1A", "12345", "AB1 2C"})
  void rejectsInvalidClient2PostcodeValuesNamingTheField(String value) {
    var errors = validate(MediationClaimDetailsViewField.CLIENT_2_POSTCODE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CLIENT_2_POSTCODE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.postcode.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Postcode");
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
  @ValueSource(strings = {"01011999/A/BCD1", "31129999/X/1", "15061985/&/A-1"})
  void acceptsValidClient2UniqueClientNumberValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.CLIENT_2_UCN, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"3213 1999/A/BCD1", "01011999-A-BCD1", "abcd1999/A/BCD1", "01011999/AB/BCD1"})
  void rejectsInvalidClient2UniqueClientNumberValuesNamingTheField(String value) {
    var errors = validate(MediationClaimDetailsViewField.CLIENT_2_UCN, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CLIENT_2_UCN]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.uniqueClientNumber.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Unique client number (UCN)");
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

  @ParameterizedTest
  @CsvSource({
    "CLIENT_2_FORENAME, Jean-Paul",
    "CLIENT_2_SURNAME, O'Brien & Sons",
    "CLIENT_2_SURNAME, Smith"
  })
  void acceptsRepresentativeValidClient2Values(MediationClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({"CLIENT_2_FORENAME", "CLIENT_2_SURNAME"})
  void acceptsClient2ValueAtMaxLength(MediationClaimDetailsViewField field) {
    var errors = validate(field, "a".repeat(30));

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({"CLIENT_2_FORENAME", "CLIENT_2_SURNAME"})
  void rejectsClient2ValueOverMaxLengthNamingTheFieldAndLimit(
      MediationClaimDetailsViewField field) {
    var errors = validate(field, "a".repeat(31));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[%s]".formatted(field.name()));
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.tooLong");
    assertThat(fieldError.getArguments()[1]).isEqualTo("30");
  }

  @ParameterizedTest
  @CsvSource({"CLIENT_2_FORENAME, 'Jean_Paul'", "CLIENT_2_SURNAME, 'Smith#'"})
  void rejectsClient2ValuesWithDisallowedCharacters(
      MediationClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[%s]".formatted(field.name()));
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.invalidFormat");
  }

  @Test
  void client2FormatFailureTakesPriorityOverLengthFailure() {
    var errors = validate(MediationClaimDetailsViewField.CLIENT_2_SURNAME, "#".repeat(31));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CLIENT_2_SURNAME]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.invalidFormat");
  }

  @Test
  void client2ValuesCarryTheClient2FieldLabelAsTheFirstArgument() {
    var errors = validate(MediationClaimDetailsViewField.CLIENT_2_SURNAME, "#".repeat(31));

    var fieldError = errors.getFieldError("inputs[CLIENT_2_SURNAME]");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Last name");
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

  @ParameterizedTest
  @ValueSource(
      strings = {"1", "9", "01", "09", "10", "13", "15", "16", "18", "19", "21", "23", "38"})
  void acceptsValidCrimeMatterTypeCodeValues(String value) {
    var errors = validate(CrimeClaimDetailsViewField.MATTER_TYPE_CODE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"00", "14", "17", "20", "39", "abc"})
  void rejectsInvalidCrimeMatterTypeCodeValuesNamingTheField(String value) {
    var errors = validate(CrimeClaimDetailsViewField.MATTER_TYPE_CODE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[MATTER_TYPE_CODE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.matterTypeCode.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Matter type");
  }

  @ParameterizedTest
  @CsvSource({"MATTER_TYPE_CODE_1, FAM1", "MATTER_TYPE_CODE_2, HOU2"})
  void acceptsValidCivilMatterTypeCodeValues(CivilClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({"MATTER_TYPE_CODE_1, FAM", "MATTER_TYPE_CODE_2, FAMILY1"})
  void rejectsWrongLengthCivilMatterTypeCodeValuesNamingTheFieldAndLength(
      CivilClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[%s]".formatted(field.name()));
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.matterTypeCode.wrongLength");
    assertThat(fieldError.getArguments()[1]).isEqualTo("4");
  }

  @ParameterizedTest
  @CsvSource({"MATTER_TYPE_CODE_1, FAM1", "MATTER_TYPE_CODE_2, HOU2"})
  void acceptsValidMediationMatterTypeCodeValues(
      MediationClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({"MATTER_TYPE_CODE_1, fam1", "MATTER_TYPE_CODE_2, FAM", "MATTER_TYPE_CODE_1, FAMILY1"})
  void rejectsInvalidMediationMatterTypeCodeValuesNamingTheField(
      MediationClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[%s]".formatted(field.name()));
    assertThat(fieldError.getCode())
        .isEqualTo("amendmentForm.matterTypeCode.invalidUppercaseFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo(field.label(TestMessageSources.real()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ABC123", "a1b2c3d4e5", "FN10"})
  void acceptsValidFeeCodeValues(String value) {
    var errors = validate(ClaimDetailsViewField.FEE_CODE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"FEE-CODE", "FEE CODE", "FEE_CODE"})
  void rejectsFeeCodeValuesWithDisallowedCharactersNamingTheField(String value) {
    var errors = validate(ClaimDetailsViewField.FEE_CODE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[FEE_CODE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Fee code");
  }

  @Test
  void rejectsFeeCodeValuesOverMaxLengthNamingTheFieldAndLimit() {
    var errors = validate(ClaimDetailsViewField.FEE_CODE, "a".repeat(11));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[FEE_CODE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.tooLong");
    assertThat(fieldError.getArguments()[1]).isEqualTo("10");
  }

  @Test
  void feeCodeFormatFailureTakesPriorityOverLengthFailure() {
    var errors = validate(ClaimDetailsViewField.FEE_CODE, "-".repeat(11));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[FEE_CODE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  @Test
  void feeCodeLocalRuleAndFeeSchemeMembershipRuleCanEachIndependentlyReject() {
    var localRuleErrors = validate(ClaimDetailsViewField.FEE_CODE, "a".repeat(11));
    assertThat(localRuleErrors.getFieldError("inputs[FEE_CODE]").getCode())
        .isEqualTo("amendmentForm.text.tooLong");

    var availableFeeCodesService = mock(AvailableFeeCodesService.class);
    when(availableFeeCodesService.getAvailableFeeCodes(any())).thenReturn(Map.of());
    var fspValidator = new FeeCodeAmendmentFieldValidator(availableFeeCodesService);
    var form = new AmendmentForm();
    form.setInputs(Map.of("FEE_CODE", "ABC123"));
    var fspErrors = new BeanPropertyBindingResult(form, "amendmentForm");
    fspValidator.validate(
        MockClaimsFunctions.createMockCrimeClaim(),
        ClaimDetailsViewField.FEE_CODE,
        form,
        fspErrors);

    assertThat(fspErrors.getFieldError("inputs[FEE_CODE]").getCode())
        .isEqualTo("amendmentForm.feeCode.invalid");
  }

  @ParameterizedTest
  @ValueSource(strings = {"A1", "abcdef", "A12345", "1A"})
  void acceptsValidPoliceStationCourtPrisonIdValues(String value) {
    var errors = validate(CrimeClaimDetailsViewField.POLICE_STATION_COURT_PRISON_ID, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"123456", "AB-123", "AB 123"})
  void rejectsPoliceStationCourtPrisonIdValuesLackingLetterOrWithDisallowedCharsNamingTheField(
      String value) {
    var errors = validate(CrimeClaimDetailsViewField.POLICE_STATION_COURT_PRISON_ID, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[POLICE_STATION_COURT_PRISON_ID]");
    assertThat(fieldError.getCode())
        .isEqualTo("amendmentForm.alphanumericWithLetter.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Police station/Court ID/Prison ID");
  }

  @Test
  void rejectsPoliceStationCourtPrisonIdValuesOverMaxLengthNamingTheFieldAndLimit() {
    var errors = validate(CrimeClaimDetailsViewField.POLICE_STATION_COURT_PRISON_ID, "a1b2c3d4");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[POLICE_STATION_COURT_PRISON_ID]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.tooLong");
    assertThat(fieldError.getArguments()[1]).isEqualTo("6");
  }

  @Test
  void policeStationCourtPrisonIdFormatFailureTakesPriorityOverLengthFailure() {
    // Over the 6 char limit AND contains no letter - FORMAT outranks LENGTH.
    var errors = validate(CrimeClaimDetailsViewField.POLICE_STATION_COURT_PRISON_ID, "1234567");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[POLICE_STATION_COURT_PRISON_ID]");
    assertThat(fieldError.getCode())
        .isEqualTo("amendmentForm.alphanumericWithLetter.invalidFormat");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB12", "ab12", "1234"})
  void acceptsValidSchemeIdValues(String value) {
    var errors = validate(CrimeClaimDetailsViewField.SCHEME_ID, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"ABC", "ABCDE"})
  void rejectsWrongLengthSchemeIdValuesNamingTheFieldAndLength(String value) {
    var errors = validate(CrimeClaimDetailsViewField.SCHEME_ID, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[SCHEME_ID]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.wrongLength");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Scheme ID");
    assertThat(fieldError.getArguments()[1]).isEqualTo("4");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB-C", "AB C"})
  void rejectsSchemeIdValuesWithDisallowedCharactersNamingTheField(String value) {
    var errors = validate(CrimeClaimDetailsViewField.SCHEME_ID, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[SCHEME_ID]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  @Test
  void schemeIdFormatFailureTakesPriorityOverLengthFailure() {
    var errors = validate(CrimeClaimDetailsViewField.SCHEME_ID, "-".repeat(5));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[SCHEME_ID]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB1234567C", "1234567890"})
  void acceptsValidDsccNumberValues(String value) {
    var errors = validate(CrimeClaimDetailsViewField.DSCC_NUMBER, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB1234567", "AB1234567CD"})
  void rejectsWrongLengthDsccNumberValuesNamingTheFieldAndLength(String value) {
    var errors = validate(CrimeClaimDetailsViewField.DSCC_NUMBER, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[DSCC_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.wrongLength");
    assertThat(fieldError.getArguments()[1]).isEqualTo("10");
  }

  @Test
  void rejectsDsccNumberValuesWithDisallowedCharactersNamingTheField() {
    var errors = validate(CrimeClaimDetailsViewField.DSCC_NUMBER, "AB-1234567");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[DSCC_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  @ParameterizedTest
  @ValueSource(strings = {"A", "AB1234567C", "1234567890"})
  void acceptsValidMaatIdValues(String value) {
    var errors = validate(CrimeClaimDetailsViewField.MAAT_ID, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMaatIdValuesOverMaxLengthNamingTheFieldAndLimit() {
    var errors = validate(CrimeClaimDetailsViewField.MAAT_ID, "a".repeat(11));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[MAAT_ID]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.tooLong");
    assertThat(fieldError.getArguments()[1]).isEqualTo("10");
  }

  @Test
  void rejectsMaatIdValuesWithDisallowedCharactersNamingTheField() {
    var errors = validate(CrimeClaimDetailsViewField.MAAT_ID, "AB-1234");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[MAAT_ID]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  @Test
  void maatIdFormatFailureTakesPriorityOverLengthFailure() {
    var errors = validate(CrimeClaimDetailsViewField.MAAT_ID, "-".repeat(11));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[MAAT_ID]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB1234567C", "1234567890"})
  void acceptsValidPrisonLawPriorApprovalNumberValues(String value) {
    var errors = validate(CrimeClaimDetailsViewField.PRISON_LAW_PRIOR_APPROVAL_NUMBER, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB1234567", "AB1234567CD"})
  void rejectsWrongLengthPrisonLawPriorApprovalNumberValuesNamingTheFieldAndLength(String value) {
    var errors = validate(CrimeClaimDetailsViewField.PRISON_LAW_PRIOR_APPROVAL_NUMBER, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[PRISON_LAW_PRIOR_APPROVAL_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.wrongLength");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Prison Law Prior Approval number");
    assertThat(fieldError.getArguments()[1]).isEqualTo("10");
  }

  @Test
  void rejectsPrisonLawPriorApprovalNumberValuesWithDisallowedCharactersNamingTheField() {
    var errors =
        validate(CrimeClaimDetailsViewField.PRISON_LAW_PRIOR_APPROVAL_NUMBER, "AB-1234567");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[PRISON_LAW_PRIOR_APPROVAL_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  @ParameterizedTest
  @CsvSource({
    "SUSPECTS_DEFENDANTS_COUNT, 0",
    "SUSPECTS_DEFENDANTS_COUNT, 99",
    "SUSPECTS_DEFENDANTS_COUNT, 42",
    "POLICE_STATION_COURT_ATTENDANCES_COUNT, 0",
    "POLICE_STATION_COURT_ATTENDANCES_COUNT, 99",
    "POLICE_STATION_COURT_ATTENDANCES_COUNT, 42"
  })
  void acceptsValidCountValues(CrimeClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({
    "SUSPECTS_DEFENDANTS_COUNT, -1",
    "SUSPECTS_DEFENDANTS_COUNT, 100",
    "POLICE_STATION_COURT_ATTENDANCES_COUNT, -1",
    "POLICE_STATION_COURT_ATTENDANCES_COUNT, 100"
  })
  void rejectsOutOfRangeCountValuesNamingTheFieldAndRange(
      CrimeClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[%s]".formatted(field.name()));
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.range");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0");
    assertThat(fieldError.getArguments()[2]).isEqualTo("99");
  }

  @Test
  void doesNotAddSecondErrorForUnparseableCountValue() {
    var errors = validate(CrimeClaimDetailsViewField.SUSPECTS_DEFENDANTS_COUNT, "notanumber");

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "9999.99", "1234.56"})
  void acceptsValidTravelCostsValues(String value) {
    var errors = validate(CrimeClaimDetailsViewField.TRAVEL_COSTS, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "10000.00"})
  void rejectsOutOfRangeTravelCostsValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CrimeClaimDetailsViewField.TRAVEL_COSTS, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[TRAVEL_COSTS]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.range");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Net travel costs");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0.00");
    assertThat(fieldError.getArguments()[2]).isEqualTo("9999.99");
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "999999.99", "1234.56"})
  void acceptsValidWaitingCostsValues(String value) {
    var errors = validate(CrimeClaimDetailsViewField.WAITING_COSTS, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "1000000.00"})
  void rejectsOutOfRangeWaitingCostsValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CrimeClaimDetailsViewField.WAITING_COSTS, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[WAITING_COSTS]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.range");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0.00");
    assertThat(fieldError.getArguments()[2]).isEqualTo("999999.99");
  }

  @Test
  void doesNotAddSecondErrorForUnparseableCostsValue() {
    var errors = validate(CrimeClaimDetailsViewField.TRAVEL_COSTS, "notanumber");

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"000", "123", "999"})
  void acceptsValidClaimIdValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.CLAIM_ID, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"12", "1234", "abc", "12a"})
  void rejectsInvalidClaimIdValuesNamingTheField(String value) {
    var errors = validate(MediationClaimDetailsViewField.CLAIM_ID, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CLAIM_ID]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.claimId.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Claim ID");
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "50", "99"})
  void acceptsValidMediationSessionsCountValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.MEDIATION_SESSIONS_COUNT, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "100"})
  void rejectsOutOfRangeMediationSessionsCountValuesNamingTheFieldAndRange(String value) {
    var errors = validate(MediationClaimDetailsViewField.MEDIATION_SESSIONS_COUNT, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[MEDIATION_SESSIONS_COUNT]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.range");
    assertThat(fieldError.getArguments()[1]).isEqualTo("1");
    assertThat(fieldError.getArguments()[2]).isEqualTo("99");
  }

  @Test
  void doesNotAddSecondErrorForUnparseableMediationSessionsCountValue() {
    var errors = validate(MediationClaimDetailsViewField.MEDIATION_SESSIONS_COUNT, "notanumber");

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "50000", "99999"})
  void acceptsValidMediationTimeMinutesValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.MEDIATION_TIME_MINUTES, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "100000"})
  void rejectsOutOfRangeMediationTimeMinutesValuesNamingTheFieldAndRange(String value) {
    var errors = validate(MediationClaimDetailsViewField.MEDIATION_TIME_MINUTES, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[MEDIATION_TIME_MINUTES]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.range");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0");
    assertThat(fieldError.getArguments()[2]).isEqualTo("99999");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB1", "abc", "123"})
  void acceptsValidOutreachLocationValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.OUTREACH_LOCATION, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB", "ABCD"})
  void rejectsWrongLengthOutreachLocationValuesNamingTheFieldAndLength(String value) {
    var errors = validate(MediationClaimDetailsViewField.OUTREACH_LOCATION, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[OUTREACH_LOCATION]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.wrongLength");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Outreach location");
    assertThat(fieldError.getArguments()[1]).isEqualTo("3");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB-", "A B"})
  void rejectsOutreachLocationValuesWithDisallowedCharactersNamingTheField(String value) {
    var errors = validate(MediationClaimDetailsViewField.OUTREACH_LOCATION, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[OUTREACH_LOCATION]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  @Test
  void outreachLocationFormatFailureTakesPriorityOverLengthFailure() {
    // Over the 3 char length AND contains a disallowed character - FORMAT outranks LENGTH.
    var errors = validate(MediationClaimDetailsViewField.OUTREACH_LOCATION, "-".repeat(4));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[OUTREACH_LOCATION]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  private Errors validate(ClaimViewField<?> field, String value) {
    var form = new AmendmentForm();
    form.setInputs(Map.of(field.name(), value));

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(MockClaimsFunctions.createMockCrimeClaim(), field, form, errors);
    return errors;
  }
}
