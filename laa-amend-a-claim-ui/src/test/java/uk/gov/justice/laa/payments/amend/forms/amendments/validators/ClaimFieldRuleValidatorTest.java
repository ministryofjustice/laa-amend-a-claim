package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

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
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.service.AvailableFeeCodesService;
import uk.gov.justice.laa.payments.amend.support.TestMessageSources;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField;

class ClaimFieldRuleValidatorTest {

  private final ClaimFieldRuleValidator validator =
      new ClaimFieldRuleValidator(TestMessageSources.real());

  @Test
  void appliesOnlyToCuratedFields() {
    assertThat(validator.appliesTo(ClaimDetailsViewField.INITIAL)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.FORENAME)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.SURNAME)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.FEE_CODE)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.PROFIT_COST)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.DISBURSEMENTS)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.DISBURSEMENTS_VAT)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.VAT)).isFalse();
    assertThat(validator.appliesTo(ClaimDetailsViewField.FIXED_FEE)).isFalse();
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
    assertThat(validator.appliesTo(ClaimDetailsViewField.CASE_REFERENCE_NUMBER)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.CASE_START_DATE)).isFalse();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER)).isTrue();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.CASE_CONCLUDED_DATE)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.CASE_ID)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.CASE_CONCLUDED_CLAIMED_DATE))
        .isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.CASE_STAGE)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.SCHEDULE_REFERENCE_CIVIL)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.PROCUREMENT_AREA)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.ACCESS_POINT)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.DELIVERY_LOCATION)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.COURT_LOCATION)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.AIT_HEARING_CENTRE)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.LOCAL_AUTHORITY_NUMBER)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.VALUE_OF_COSTS)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.OUTCOME_FOR_CLIENT)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.EXCEPTIONAL_CASE_FUNDING)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_REFERENCE))
        .isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_EXEMPTION))
        .isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.DESIGNATED_ACCREDITED_REPRESENTATIVE))
        .isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.ADVICE_TIME)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.TRAVEL_TIME)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.WAITING_TIME)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.ADDITIONAL_TRAVEL_PAYMENT)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.FOLLOW_ON_WORK)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.TOLERANCE_INDICATOR)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.LEGACY_CASE)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.MEETINGS_ATTENDED)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.ADVICE_TYPE)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.TRANSFER_DATE)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.MEDICAL_REPORTS_CLAIMED)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.EXEMPTION_CRITERIA_SATISFIED))
        .isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.IRC_SURGERY)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.SURGERY_DATE)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.SURGERY_CLIENTS_COUNT)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.SURGERY_MATTERS_COUNT)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.MENTAL_HEALTH_TRIBUNAL_REFERENCE))
        .isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.IS_NRM_ADVICE)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.HOME_OFFICE)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.SUBSTANTIVE_HEARING)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.IS_LONDON_RATE)).isFalse();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.PRIOR_AUTHORITY_REFERENCE)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.COUNSELS_COST)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.TRAVEL_AND_WAITING_COSTS)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.DETENTION_TRAVEL)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.JR_FORM_FILLING)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.ADJOURNED_HEARING_FEE)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.CMRH_TELEPHONE)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.CMRH_ORAL)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.OUTCOME_FOR_CLIENT)).isTrue();
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.STAGE_REACHED)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"SW1A 1AA", "EC1A1BB", "NFA", "M1 1AE"})
  void acceptsValidCivilPostcodeValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.POSTCODE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"SW1A 1AA", "EC1A1BB", "NFA", "M1 1AE"})
  void acceptsValidMediationPostcodeValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.POSTCODE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"NOTAPOSTCODE", "SW1A", "12345", "AB1 2C", "sw1a1aa", "nfa"})
  void rejectsInvalidCivilPostcodeValuesNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.POSTCODE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[POSTCODE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.postcode.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Postcode");
  }

  @ParameterizedTest
  @ValueSource(strings = {"NOTAPOSTCODE", "SW1A", "12345", "AB1 2C", "sw1a1aa", "nfa"})
  void rejectsInvalidMediationPostcodeValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.POSTCODE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[POSTCODE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.postcode.invalidFormat");
  }

  @ParameterizedTest
  @ValueSource(strings = {"SW1A 1AA", "EC1A1BB", "NFA", "M1 1AE"})
  void acceptsValidClient2PostcodeValues(String value) {
    var errors = validate(MediationClaimDetailsViewField.CLIENT_2_POSTCODE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"NOTAPOSTCODE", "SW1A", "12345", "AB1 2C", "sw1a1aa", "nfa"})
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

  @ParameterizedTest
  @ValueSource(strings = {"AB123/45.6-7 CD", "12345", "case/ref.1-2 3"})
  void acceptsValidCaseReferenceNumberValues(String value) {
    var errors = validate(ClaimDetailsViewField.CASE_REFERENCE_NUMBER, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsCaseReferenceNumberAtMaxLength() {
    var errors = validate(ClaimDetailsViewField.CASE_REFERENCE_NUMBER, "a".repeat(30));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsCaseReferenceNumberOverMaxLengthNamingTheFieldAndLimit() {
    var errors = validate(ClaimDetailsViewField.CASE_REFERENCE_NUMBER, "a".repeat(31));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CASE_REFERENCE_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.tooLong");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Case reference number (CRN)");
    assertThat(fieldError.getArguments()[1]).isEqualTo("30");
  }

  @ParameterizedTest
  @ValueSource(strings = {"CASE#1", "CASE_1", "CASE@1"})
  void rejectsCaseReferenceNumberValuesWithDisallowedCharactersNamingTheField(String value) {
    var errors = validate(ClaimDetailsViewField.CASE_REFERENCE_NUMBER, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CASE_REFERENCE_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.caseReferenceNumber.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Case reference number (CRN)");
  }

  @Test
  void caseReferenceNumberFormatFailureTakesPriorityOverLengthFailure() {
    var errors = validate(ClaimDetailsViewField.CASE_REFERENCE_NUMBER, "#".repeat(31));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CASE_REFERENCE_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.caseReferenceNumber.invalidFormat");
  }

  @ParameterizedTest
  @ValueSource(strings = {"120223/001", "010199/999"})
  void acceptsValidUniqueFileNumberValues(String value) {
    var errors = validate(CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"12022/001", "120223/01", "120223-001", "ABCDEF/001"})
  void rejectsInvalidUniqueFileNumberValuesNamingTheField(String value) {
    var errors = validate(CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[UNIQUE_FILE_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.uniqueFileNumber.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Unique file number (UFN)");
  }

  @ParameterizedTest
  @ValueSource(strings = {"000", "123", "999"})
  void acceptsValidCivilCaseIdValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.CASE_ID, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"12", "1234", "abc"})
  void rejectsInvalidCivilCaseIdValuesNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.CASE_ID, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CASE_ID]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.claimId.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Case ID");
  }

  @ParameterizedTest
  @ValueSource(strings = {"REF/1.2-3", "12345", "SCHEDULE-REF"})
  void acceptsValidScheduleReferenceCivilValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.SCHEDULE_REFERENCE_CIVIL, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsScheduleReferenceCivilAtMaxLength() {
    var errors = validate(CivilClaimDetailsViewField.SCHEDULE_REFERENCE_CIVIL, "a".repeat(20));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsScheduleReferenceCivilOverMaxLengthNamingTheFieldAndLimit() {
    var errors = validate(CivilClaimDetailsViewField.SCHEDULE_REFERENCE_CIVIL, "a".repeat(21));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[SCHEDULE_REFERENCE_CIVIL]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.tooLong");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Schedule reference");
    assertThat(fieldError.getArguments()[1]).isEqualTo("20");
  }

  @ParameterizedTest
  @ValueSource(strings = {"REF 1", "REF_1", "REF#1"})
  void rejectsScheduleReferenceCivilValuesWithDisallowedCharactersNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.SCHEDULE_REFERENCE_CIVIL, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[SCHEDULE_REFERENCE_CIVIL]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.scheduleReference.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Schedule reference");
  }

  @Test
  void scheduleReferenceCivilFormatFailureTakesPriorityOverLengthFailure() {
    var errors = validate(CivilClaimDetailsViewField.SCHEDULE_REFERENCE_CIVIL, "#".repeat(21));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[SCHEDULE_REFERENCE_CIVIL]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.scheduleReference.invalidFormat");
  }

  @ParameterizedTest
  @CsvSource({"PROCUREMENT_AREA, AB12345", "DELIVERY_LOCATION, ZZ00001"})
  void acceptsValidLocationCodeValues(CivilClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({
    "PROCUREMENT_AREA, ab12345",
    "PROCUREMENT_AREA, A12345",
    "PROCUREMENT_AREA, AB1234",
    "DELIVERY_LOCATION, delivery1",
    "DELIVERY_LOCATION, AB123456"
  })
  void rejectsInvalidLocationCodeValuesNamingTheField(
      CivilClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[%s]".formatted(field.name()));
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.locationCode.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo(field.label(TestMessageSources.real()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AP00001", "AP99999"})
  void acceptsValidAccessPointValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.ACCESS_POINT, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"ap00001", "AB00001", "AP0001", "AP000012"})
  void rejectsInvalidAccessPointValuesNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.ACCESS_POINT, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[ACCESS_POINT]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.accessPoint.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Access point");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB123", "abc123XYZ", "1"})
  void acceptsValidLocalAuthorityNumberValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.LOCAL_AUTHORITY_NUMBER, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsLocalAuthorityNumberAtMaxLength() {
    var errors = validate(CivilClaimDetailsViewField.LOCAL_AUTHORITY_NUMBER, "a".repeat(30));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsLocalAuthorityNumberOverMaxLengthNamingTheFieldAndLimit() {
    var errors = validate(CivilClaimDetailsViewField.LOCAL_AUTHORITY_NUMBER, "a".repeat(31));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[LOCAL_AUTHORITY_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.tooLong");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Local authority number");
    assertThat(fieldError.getArguments()[1]).isEqualTo("30");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB-123", "AB 123"})
  void rejectsLocalAuthorityNumberValuesWithDisallowedCharactersNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.LOCAL_AUTHORITY_NUMBER, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[LOCAL_AUTHORITY_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Local authority number");
  }

  @Test
  void localAuthorityNumberFormatFailureTakesPriorityOverLengthFailure() {
    var errors = validate(CivilClaimDetailsViewField.LOCAL_AUTHORITY_NUMBER, "-".repeat(31));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[LOCAL_AUTHORITY_NUMBER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "99999.99", "1234.56"})
  void acceptsValidValueOfCostsValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.VALUE_OF_COSTS, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "100000.00"})
  void rejectsOutOfRangeValueOfCostsValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.VALUE_OF_COSTS, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[VALUE_OF_COSTS]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.range");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0.00");
    assertThat(fieldError.getArguments()[2]).isEqualTo("99999.99");
  }

  @ParameterizedTest
  @ValueSource(strings = {"0000001AB", "1234567ZZ"})
  void acceptsValidExceptionalCaseFundingValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.EXCEPTIONAL_CASE_FUNDING, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"123456AB", "12345678AB", "0000001ab", "AAAAAAAAB"})
  void rejectsInvalidExceptionalCaseFundingValuesNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.EXCEPTIONAL_CASE_FUNDING, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[EXCEPTIONAL_CASE_FUNDING]");
    assertThat(fieldError.getCode())
        .isEqualTo("amendmentForm.exceptionalCaseFunding.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Exceptional case funding (ECF) reference");
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "1234567", "42"})
  void acceptsValidCivilLegalAdviceReferenceValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_REFERENCE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"12345678", "abc1234", "12A"})
  void rejectsInvalidCivilLegalAdviceReferenceValuesNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_REFERENCE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CIVIL_LEGAL_ADVICE_REFERENCE]");
    assertThat(fieldError.getCode())
        .isEqualTo("amendmentForm.civilLegalAdviceReference.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Civil Legal Advice (CLA) reference number");
  }

  @ParameterizedTest
  @ValueSource(strings = {"ABCD", "1234", "AB12"})
  void acceptsValidCivilLegalAdviceExemptionValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_EXEMPTION, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"ABC", "ABCDE"})
  void rejectsInvalidCivilLegalAdviceExemptionValuesNamingTheFieldAndLength(String value) {
    var errors = validate(CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_EXEMPTION, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CIVIL_LEGAL_ADVICE_EXEMPTION]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.wrongLength");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Civil Legal Advice (CLA) exemption code");
    assertThat(fieldError.getArguments()[1]).isEqualTo("4");
  }

  @ParameterizedTest
  @CsvSource({
    "ADVICE_TIME, 0",
    "ADVICE_TIME, 99999",
    "TRAVEL_TIME, 0",
    "TRAVEL_TIME, 99999",
    "WAITING_TIME, 0",
    "WAITING_TIME, 99999"
  })
  void acceptsValidTimeValues(CivilClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({
    "ADVICE_TIME, -1",
    "ADVICE_TIME, 100000",
    "TRAVEL_TIME, -1",
    "TRAVEL_TIME, 100000",
    "WAITING_TIME, -1",
    "WAITING_TIME, 100000"
  })
  void rejectsOutOfRangeTimeValuesNamingTheFieldAndRange(
      CivilClaimDetailsViewField field, String value) {
    var errors = validate(field, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[%s]".formatted(field.name()));
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.range");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0");
    assertThat(fieldError.getArguments()[2]).isEqualTo("99999");
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "10", "5"})
  void acceptsValidMedicalReportsClaimedValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.MEDICAL_REPORTS_CLAIMED, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "11"})
  void rejectsOutOfRangeMedicalReportsClaimedValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.MEDICAL_REPORTS_CLAIMED, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[MEDICAL_REPORTS_CLAIMED]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.range");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Medical reports claimed");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0");
    assertThat(fieldError.getArguments()[2]).isEqualTo("10");
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "20", "10"})
  void acceptsValidSurgeryClientsCountValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.SURGERY_CLIENTS_COUNT, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "21"})
  void rejectsOutOfRangeSurgeryClientsCountValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.SURGERY_CLIENTS_COUNT, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[SURGERY_CLIENTS_COUNT]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.range");
    assertThat(fieldError.getArguments()[1]).isEqualTo("1");
    assertThat(fieldError.getArguments()[2]).isEqualTo("20");
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "99", "50"})
  void acceptsValidSurgeryMattersCountValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.SURGERY_MATTERS_COUNT, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "100"})
  void rejectsOutOfRangeSurgeryMattersCountValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.SURGERY_MATTERS_COUNT, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[SURGERY_MATTERS_COUNT]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.range");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0");
    assertThat(fieldError.getArguments()[2]).isEqualTo("99");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB/1234/56789", "AB12345"})
  void acceptsValidMentalHealthTribunalReferenceValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.MENTAL_HEALTH_TRIBUNAL_REFERENCE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"ab/1234/56789", "AB123456", "AB/123/56789", "12/1234/56789"})
  void rejectsInvalidMentalHealthTribunalReferenceValuesNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.MENTAL_HEALTH_TRIBUNAL_REFERENCE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[MENTAL_HEALTH_TRIBUNAL_REFERENCE]");
    assertThat(fieldError.getCode())
        .isEqualTo("amendmentForm.mentalHealthTribunalReference.invalidFormat");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Mental health tribunal reference");
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "9", "5"})
  void acceptsValidHomeOfficeValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.HOME_OFFICE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "10"})
  void rejectsOutOfRangeHomeOfficeValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.HOME_OFFICE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[HOME_OFFICE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.range");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Home Office Interview");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0");
    assertThat(fieldError.getArguments()[2]).isEqualTo("9");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB1234C", "1234567", "ABCDEFG"})
  void acceptsValidPriorAuthorityReferenceValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.PRIOR_AUTHORITY_REFERENCE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsPriorAuthorityReferenceAtExactLength() {
    var errors = validate(CivilClaimDetailsViewField.PRIOR_AUTHORITY_REFERENCE, "a".repeat(7));

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB1234", "AB1234CD"})
  void rejectsPriorAuthorityReferenceWrongLengthNamingTheFieldAndLength(String value) {
    var errors = validate(CivilClaimDetailsViewField.PRIOR_AUTHORITY_REFERENCE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[PRIOR_AUTHORITY_REFERENCE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.text.wrongLength");
    assertThat(fieldError.getArguments()[0])
        .isEqualTo("National Immigration Asylum Team Disbursement prior authority number");
    assertThat(fieldError.getArguments()[1]).isEqualTo("7");
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB-123", "AB 123"})
  void rejectsPriorAuthorityReferenceValuesWithDisallowedCharactersNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.PRIOR_AUTHORITY_REFERENCE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[PRIOR_AUTHORITY_REFERENCE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  @Test
  void priorAuthorityReferenceFormatFailureTakesPriorityOverLengthFailure() {
    var errors = validate(CivilClaimDetailsViewField.PRIOR_AUTHORITY_REFERENCE, "-".repeat(8));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[PRIOR_AUTHORITY_REFERENCE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.alphanumeric.invalidFormat");
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "999999999.99", "1234.56"})
  void acceptsValidProfitCostValues(String value) {
    var errors = validate(ClaimDetailsViewField.PROFIT_COST, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "1000000000.00"})
  void rejectsOutOfRangeProfitCostValuesNamingTheFieldAndRange(String value) {
    var errors = validate(ClaimDetailsViewField.PROFIT_COST, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[PROFIT_COST]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.range");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Net profit costs");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0.00");
    assertThat(fieldError.getArguments()[2]).isEqualTo("999999999.99");
  }

  @Test
  void doesNotAddSecondErrorForUnparseableProfitCostValue() {
    var errors = validate(ClaimDetailsViewField.PROFIT_COST, "notanumber");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void doesNotAddSecondErrorForOverPrecisionInRangeProfitCostValue() {
    var errors = validate(ClaimDetailsViewField.PROFIT_COST, "1234.567");

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "999999999.99", "1234.56"})
  void acceptsValidDisbursementsValues(String value) {
    var errors = validate(ClaimDetailsViewField.DISBURSEMENTS, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "1000000000.00"})
  void rejectsOutOfRangeDisbursementsValuesNamingTheFieldAndRange(String value) {
    var errors = validate(ClaimDetailsViewField.DISBURSEMENTS, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[DISBURSEMENTS]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.range");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Net disbursements");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0.00");
    assertThat(fieldError.getArguments()[2]).isEqualTo("999999999.99");
  }

  @Test
  void doesNotAddSecondErrorForOverPrecisionInRangeDisbursementsValue() {
    var errors = validate(ClaimDetailsViewField.DISBURSEMENTS, "1234.567");

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "1234.56", "999999999.99"})
  void acceptsValidDisbursementsVatValues(String value) {
    var errors = validate(ClaimDetailsViewField.DISBURSEMENTS_VAT, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "-100"})
  void rejectsBelowMinimumDisbursementsVatValuesNamingTheFieldAndMinimum(String value) {
    var errors = validate(ClaimDetailsViewField.DISBURSEMENTS_VAT, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[DISBURSEMENTS_VAT]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.min");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Disbursements VAT");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0.00");
  }

  @Test
  void doesNotAddSecondErrorForUnparseableDisbursementsVatValue() {
    var errors = validate(ClaimDetailsViewField.DISBURSEMENTS_VAT, "notanumber");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void doesNotAddSecondErrorForOverPrecisionInRangeDisbursementsVatValue() {
    var errors = validate(ClaimDetailsViewField.DISBURSEMENTS_VAT, "1234.567");

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "99999.99", "1234.56"})
  void acceptsValidCounselsCostValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.COUNSELS_COST, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "100000.00"})
  void rejectsOutOfRangeCounselsCostValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.COUNSELS_COST, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[COUNSELS_COST]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.range");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Net cost of counsel");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0.00");
    assertThat(fieldError.getArguments()[2]).isEqualTo("99999.99");
  }

  @Test
  void doesNotAddSecondErrorForOverPrecisionInRangeCounselsCostValue() {
    var errors = validate(CivilClaimDetailsViewField.COUNSELS_COST, "1234.567");

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "9999.99", "1234.56"})
  void acceptsValidTravelAndWaitingCostsValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.TRAVEL_AND_WAITING_COSTS, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "10000.00"})
  void rejectsOutOfRangeTravelAndWaitingCostsValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.TRAVEL_AND_WAITING_COSTS, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[TRAVEL_AND_WAITING_COSTS]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.range");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Travel and waiting costs");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0.00");
    assertThat(fieldError.getArguments()[2]).isEqualTo("9999.99");
  }

  @Test
  void doesNotAddSecondErrorForOverPrecisionInRangeTravelAndWaitingCostsValue() {
    var errors = validate(CivilClaimDetailsViewField.TRAVEL_AND_WAITING_COSTS, "1234.567");

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "99999999.99", "1234.56"})
  void acceptsValidDetentionTravelValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.DETENTION_TRAVEL, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "100000000.00"})
  void rejectsOutOfRangeDetentionTravelValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.DETENTION_TRAVEL, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[DETENTION_TRAVEL]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.range");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Detention, travel and waiting (DTW) costs");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0.00");
    assertThat(fieldError.getArguments()[2]).isEqualTo("99999999.99");
  }

  @Test
  void doesNotAddSecondErrorForOverPrecisionInRangeDetentionTravelValue() {
    var errors = validate(CivilClaimDetailsViewField.DETENTION_TRAVEL, "1234.567");

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "9999.99", "1234.56"})
  void acceptsValidJrFormFillingValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.JR_FORM_FILLING, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "10000.00"})
  void rejectsOutOfRangeJrFormFillingValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.JR_FORM_FILLING, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[JR_FORM_FILLING]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.bigDecimal.range");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Judicial review or form filling");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0.00");
    assertThat(fieldError.getArguments()[2]).isEqualTo("9999.99");
  }

  @Test
  void doesNotAddSecondErrorForOverPrecisionInRangeJrFormFillingValue() {
    var errors = validate(CivilClaimDetailsViewField.JR_FORM_FILLING, "1234.567");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void doesNotAddSecondErrorForUnparseableCivilCostsValue() {
    var errors = validate(CivilClaimDetailsViewField.COUNSELS_COST, "notanumber");

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "9", "5"})
  void acceptsValidAdjournedHearingFeeValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.ADJOURNED_HEARING_FEE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "10"})
  void rejectsOutOfRangeAdjournedHearingFeeValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.ADJOURNED_HEARING_FEE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[ADJOURNED_HEARING_FEE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.range");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Adjourned hearing fee");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0");
    assertThat(fieldError.getArguments()[2]).isEqualTo("9");
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "9", "5"})
  void acceptsValidCmrhTelephoneValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.CMRH_TELEPHONE, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "10"})
  void rejectsOutOfRangeCmrhTelephoneValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.CMRH_TELEPHONE, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CMRH_TELEPHONE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.range");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0");
    assertThat(fieldError.getArguments()[2]).isEqualTo("9");
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "9", "5"})
  void acceptsValidCmrhOralValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.CMRH_ORAL, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "10"})
  void rejectsOutOfRangeCmrhOralValuesNamingTheFieldAndRange(String value) {
    var errors = validate(CivilClaimDetailsViewField.CMRH_ORAL, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CMRH_ORAL]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.number.range");
    assertThat(fieldError.getArguments()[1]).isEqualTo("0");
    assertThat(fieldError.getArguments()[2]).isEqualTo("9");
  }

  @Test
  void doesNotAddSecondErrorForUnparseableSmallCountValue() {
    var errors = validate(CivilClaimDetailsViewField.CMRH_ORAL, "notanumber");

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB", "a-", "1A"})
  void acceptsValidCivilOutcomeCodeValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.OUTCOME_FOR_CLIENT, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"ABC", "A", "A*"})
  void rejectsInvalidCivilOutcomeCodeValuesNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.OUTCOME_FOR_CLIENT, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[OUTCOME_FOR_CLIENT]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.civilOutcomeCode.invalidFormat");
    assertThat(fieldError.getArguments()[0])
        .isEqualTo(CivilClaimDetailsViewField.OUTCOME_FOR_CLIENT.label(TestMessageSources.real()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB", "a1", "1A"})
  void acceptsValidCivilStageReachedValues(String value) {
    var errors = validate(CivilClaimDetailsViewField.STAGE_REACHED, value);

    assertThat(errors.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"ABC", "A", "A*"})
  void rejectsInvalidCivilStageReachedValuesNamingTheField(String value) {
    var errors = validate(CivilClaimDetailsViewField.STAGE_REACHED, value);

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[STAGE_REACHED]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.civilOutcomeCode.invalidFormat");
    assertThat(fieldError.getArguments()[0])
        .isEqualTo(CivilClaimDetailsViewField.STAGE_REACHED.label(TestMessageSources.real()));
  }

  private Errors validate(ClaimViewField<?> field, String value) {
    var form = new AmendmentForm();
    form.setInputs(Map.of(field.name(), value));

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(MockClaimsFunctions.createMockCrimeClaim(), field, form, errors);
    return errors;
  }
}
