package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

/** These rules were generated from a validation schema from Data Claims API. */
public final class ClaimFieldRules {

  private static final int NAME_MAX_LENGTH = 30;

  private static final Pattern NAME_CHARACTERS = Pattern.compile("^[\\p{L}\\p{N}\\p{Zs}'&-]+$");

  private static final List<FieldRuleSpec> NAME_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.text.invalidFormat",
              value -> !NAME_CHARACTERS.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.tooLong",
              value -> value.length() > NAME_MAX_LENGTH,
              List.of(String.valueOf(NAME_MAX_LENGTH))));

  private static final Pattern POSTCODE_PATTERN =
      Pattern.compile(
          "^(NFA|[A-Za-z]{1,2}[0-9][0-9A-Za-z]?\\s?[0-9][A-Za-z]{2})$", Pattern.CASE_INSENSITIVE);

  private static final List<FieldRuleSpec> POSTCODE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.postcode.invalidFormat",
              value -> !POSTCODE_PATTERN.matcher(value).matches()));

  private static final Pattern UNIQUE_CLIENT_NUMBER_PATTERN =
      Pattern.compile(
          "^(0[1-9]|[12][0-9]|3[01])(0[1-9]|1[0-2])[0-9]{4}/[\\p{L}0-9 '&-]/[\\p{L}0-9 '&-]{1,4}$");

  private static final List<FieldRuleSpec> UNIQUE_CLIENT_NUMBER_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.uniqueClientNumber.invalidFormat",
              value -> !UNIQUE_CLIENT_NUMBER_PATTERN.matcher(value).matches()));

  private static final int HOME_OFFICE_CLIENT_NUMBER_MAX_LENGTH = 16;
  private static final Pattern ALPHANUMERIC = Pattern.compile("^[A-Za-z0-9]+$");

  private static final List<FieldRuleSpec> HOME_OFFICE_CLIENT_NUMBER_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.alphanumeric.invalidFormat",
              value -> !ALPHANUMERIC.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.tooLong",
              value -> value.length() > HOME_OFFICE_CLIENT_NUMBER_MAX_LENGTH,
              List.of(String.valueOf(HOME_OFFICE_CLIENT_NUMBER_MAX_LENGTH))));

  private static final Pattern CRIME_MATTER_TYPE_CODE_PATTERN =
      Pattern.compile("^(0[1-9]|[1-9]|1[0-3]|1[56]|1[89]|2[13]|3[3-8])$");

  private static final List<FieldRuleSpec> CRIME_MATTER_TYPE_CODE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.MEMBERSHIP,
              "amendmentForm.matterTypeCode.invalid",
              value -> !CRIME_MATTER_TYPE_CODE_PATTERN.matcher(value).matches()));

  private static final int MATTER_TYPE_CODE_LENGTH = 4;

  private static final List<FieldRuleSpec> CIVIL_MATTER_TYPE_CODE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.matterTypeCode.wrongLength",
              value -> value.length() != MATTER_TYPE_CODE_LENGTH,
              List.of(String.valueOf(MATTER_TYPE_CODE_LENGTH))));

  private static final Pattern MEDIATION_MATTER_TYPE_CODE_PATTERN =
      Pattern.compile("^[A-Z0-9]{4}$");

  private static final List<FieldRuleSpec> MEDIATION_MATTER_TYPE_CODE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.matterTypeCode.invalidUppercaseFormat",
              value -> !MEDIATION_MATTER_TYPE_CODE_PATTERN.matcher(value).matches()));

  private static final int FEE_CODE_MAX_LENGTH = 10;

  private static final List<FieldRuleSpec> FEE_CODE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.alphanumeric.invalidFormat",
              value -> !ALPHANUMERIC.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.tooLong",
              value -> value.length() > FEE_CODE_MAX_LENGTH,
              List.of(String.valueOf(FEE_CODE_MAX_LENGTH))));

  private static final int POLICE_STATION_COURT_PRISON_ID_MAX_LENGTH = 6;
  private static final Pattern ALPHANUMERIC_WITH_LETTER =
      Pattern.compile("^(?=.*[A-Za-z])[A-Za-z0-9]+$");

  private static final List<FieldRuleSpec> POLICE_STATION_COURT_PRISON_ID_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.alphanumericWithLetter.invalidFormat",
              value -> !ALPHANUMERIC_WITH_LETTER.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.tooLong",
              value -> value.length() > POLICE_STATION_COURT_PRISON_ID_MAX_LENGTH,
              List.of(String.valueOf(POLICE_STATION_COURT_PRISON_ID_MAX_LENGTH))));

  private static final int SCHEME_ID_LENGTH = 4;

  private static final List<FieldRuleSpec> SCHEME_ID_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.alphanumeric.invalidFormat",
              value -> !ALPHANUMERIC.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.wrongLength",
              value -> value.length() != SCHEME_ID_LENGTH,
              List.of(String.valueOf(SCHEME_ID_LENGTH))));

  private static final int DSCC_NUMBER_LENGTH = 10;

  private static final List<FieldRuleSpec> DSCC_NUMBER_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.alphanumeric.invalidFormat",
              value -> !ALPHANUMERIC.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.wrongLength",
              value -> value.length() != DSCC_NUMBER_LENGTH,
              List.of(String.valueOf(DSCC_NUMBER_LENGTH))));

  private static final int MAAT_ID_MAX_LENGTH = 10;

  private static final List<FieldRuleSpec> MAAT_ID_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.alphanumeric.invalidFormat",
              value -> !ALPHANUMERIC.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.tooLong",
              value -> value.length() > MAAT_ID_MAX_LENGTH,
              List.of(String.valueOf(MAAT_ID_MAX_LENGTH))));

  private static final int PRISON_LAW_PRIOR_APPROVAL_NUMBER_LENGTH = 10;

  private static final List<FieldRuleSpec> PRISON_LAW_PRIOR_APPROVAL_NUMBER_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.alphanumeric.invalidFormat",
              value -> !ALPHANUMERIC.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.wrongLength",
              value -> value.length() != PRISON_LAW_PRIOR_APPROVAL_NUMBER_LENGTH,
              List.of(String.valueOf(PRISON_LAW_PRIOR_APPROVAL_NUMBER_LENGTH))));

  private static final int COUNT_MIN = 0;
  private static final int COUNT_MAX = 99;

  private static final List<FieldRuleSpec> COUNT_RANGE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value -> isIntegerOutOfRange(value, COUNT_MIN, COUNT_MAX),
              List.of(String.valueOf(COUNT_MIN), String.valueOf(COUNT_MAX))));

  private static final BigDecimal TRAVEL_COSTS_MIN = new BigDecimal("0.00");
  private static final BigDecimal TRAVEL_COSTS_MAX = new BigDecimal("9999.99");

  private static final List<FieldRuleSpec> TRAVEL_COSTS_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.range",
              value -> isDecimalOutOfRange(value, TRAVEL_COSTS_MIN, TRAVEL_COSTS_MAX),
              List.of(TRAVEL_COSTS_MIN.toString(), TRAVEL_COSTS_MAX.toString())));

  private static final BigDecimal WAITING_COSTS_MIN = new BigDecimal("0.00");
  private static final BigDecimal WAITING_COSTS_MAX = new BigDecimal("999999.99");

  private static final List<FieldRuleSpec> WAITING_COSTS_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.range",
              value -> isDecimalOutOfRange(value, WAITING_COSTS_MIN, WAITING_COSTS_MAX),
              List.of(WAITING_COSTS_MIN.toString(), WAITING_COSTS_MAX.toString())));

  private static final Pattern CLAIM_ID_PATTERN = Pattern.compile("^[0-9]{3}$");

  private static final List<FieldRuleSpec> CLAIM_ID_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.claimId.invalidFormat",
              value -> !CLAIM_ID_PATTERN.matcher(value).matches()));

  private static final int CASE_REFERENCE_NUMBER_MAX_LENGTH = 30;
  private static final Pattern CASE_REFERENCE_NUMBER_CHARACTERS =
      Pattern.compile("^[A-Za-z0-9/.\\-\\s]+$");

  private static final List<FieldRuleSpec> CASE_REFERENCE_NUMBER_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.caseReferenceNumber.invalidFormat",
              value -> !CASE_REFERENCE_NUMBER_CHARACTERS.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.tooLong",
              value -> value.length() > CASE_REFERENCE_NUMBER_MAX_LENGTH,
              List.of(String.valueOf(CASE_REFERENCE_NUMBER_MAX_LENGTH))));

  private static final Pattern UNIQUE_FILE_NUMBER_PATTERN = Pattern.compile("^[0-9]{6}/[0-9]{3}$");

  private static final List<FieldRuleSpec> UNIQUE_FILE_NUMBER_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.uniqueFileNumber.invalidFormat",
              value -> !UNIQUE_FILE_NUMBER_PATTERN.matcher(value).matches()));

  private static final int SCHEDULE_REFERENCE_CIVIL_MAX_LENGTH = 20;
  private static final Pattern SCHEDULE_REFERENCE_CIVIL_CHARACTERS =
      Pattern.compile("^[A-Za-z0-9/.\\-]+$");

  private static final List<FieldRuleSpec> SCHEDULE_REFERENCE_CIVIL_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.scheduleReference.invalidFormat",
              value -> !SCHEDULE_REFERENCE_CIVIL_CHARACTERS.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.tooLong",
              value -> value.length() > SCHEDULE_REFERENCE_CIVIL_MAX_LENGTH,
              List.of(String.valueOf(SCHEDULE_REFERENCE_CIVIL_MAX_LENGTH))));

  private static final Pattern LOCATION_CODE_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{5}$");

  private static final List<FieldRuleSpec> LOCATION_CODE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.locationCode.invalidFormat",
              value -> !LOCATION_CODE_PATTERN.matcher(value).matches()));

  private static final Pattern ACCESS_POINT_PATTERN = Pattern.compile("^AP[0-9]{5}$");

  private static final List<FieldRuleSpec> ACCESS_POINT_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.accessPoint.invalidFormat",
              value -> !ACCESS_POINT_PATTERN.matcher(value).matches()));

  private static final int LOCAL_AUTHORITY_NUMBER_MAX_LENGTH = 30;

  private static final List<FieldRuleSpec> LOCAL_AUTHORITY_NUMBER_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.alphanumeric.invalidFormat",
              value -> !ALPHANUMERIC.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.tooLong",
              value -> value.length() > LOCAL_AUTHORITY_NUMBER_MAX_LENGTH,
              List.of(String.valueOf(LOCAL_AUTHORITY_NUMBER_MAX_LENGTH))));

  private static final int MEDIATION_SESSIONS_COUNT_MIN = 1;
  private static final int MEDIATION_SESSIONS_COUNT_MAX = 99;

  private static final List<FieldRuleSpec> MEDIATION_SESSIONS_COUNT_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value ->
                  isIntegerOutOfRange(
                      value, MEDIATION_SESSIONS_COUNT_MIN, MEDIATION_SESSIONS_COUNT_MAX),
              List.of(
                  String.valueOf(MEDIATION_SESSIONS_COUNT_MIN),
                  String.valueOf(MEDIATION_SESSIONS_COUNT_MAX))));

  private static final int MEDIATION_TIME_MINUTES_MIN = 0;
  private static final int MEDIATION_TIME_MINUTES_MAX = 99999;

  private static final List<FieldRuleSpec> MEDIATION_TIME_MINUTES_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value ->
                  isIntegerOutOfRange(
                      value, MEDIATION_TIME_MINUTES_MIN, MEDIATION_TIME_MINUTES_MAX),
              List.of(
                  String.valueOf(MEDIATION_TIME_MINUTES_MIN),
                  String.valueOf(MEDIATION_TIME_MINUTES_MAX))));

  private static final int OUTREACH_LOCATION_LENGTH = 3;

  private static final List<FieldRuleSpec> OUTREACH_LOCATION_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.alphanumeric.invalidFormat",
              value -> !ALPHANUMERIC.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.wrongLength",
              value -> value.length() != OUTREACH_LOCATION_LENGTH,
              List.of(String.valueOf(OUTREACH_LOCATION_LENGTH))));

  private static final BigDecimal MEDIUM_COST_MIN = new BigDecimal("0.00");
  private static final BigDecimal MEDIUM_COST_MAX = new BigDecimal("99999.99");

  private static final List<FieldRuleSpec> MEDIUM_COST_RANGE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.range",
              value -> isDecimalOutOfRange(value, MEDIUM_COST_MIN, MEDIUM_COST_MAX),
              List.of(MEDIUM_COST_MIN.toString(), MEDIUM_COST_MAX.toString())));

  private static final Pattern EXCEPTIONAL_CASE_FUNDING_PATTERN =
      Pattern.compile("^[0-9]{7}[A-Z]{2}$");

  private static final List<FieldRuleSpec> EXCEPTIONAL_CASE_FUNDING_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.exceptionalCaseFunding.invalidFormat",
              value -> !EXCEPTIONAL_CASE_FUNDING_PATTERN.matcher(value).matches()));

  private static final Pattern CIVIL_LEGAL_ADVICE_REFERENCE_PATTERN =
      Pattern.compile("^[0-9]{1,7}$");

  private static final List<FieldRuleSpec> CIVIL_LEGAL_ADVICE_REFERENCE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.civilLegalAdviceReference.invalidFormat",
              value -> !CIVIL_LEGAL_ADVICE_REFERENCE_PATTERN.matcher(value).matches()));

  private static final int CIVIL_LEGAL_ADVICE_EXEMPTION_LENGTH = 4;

  private static final List<FieldRuleSpec> CIVIL_LEGAL_ADVICE_EXEMPTION_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.wrongLength",
              value -> value.length() != CIVIL_LEGAL_ADVICE_EXEMPTION_LENGTH,
              List.of(String.valueOf(CIVIL_LEGAL_ADVICE_EXEMPTION_LENGTH))));

  private static final int TIME_MIN = 0;
  private static final int TIME_MAX = 99999;

  private static final List<FieldRuleSpec> TIME_RANGE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value -> isIntegerOutOfRange(value, TIME_MIN, TIME_MAX),
              List.of(String.valueOf(TIME_MIN), String.valueOf(TIME_MAX))));

  private static final int MEDICAL_REPORTS_CLAIMED_MIN = 0;
  private static final int MEDICAL_REPORTS_CLAIMED_MAX = 10;

  private static final List<FieldRuleSpec> MEDICAL_REPORTS_CLAIMED_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value ->
                  isIntegerOutOfRange(
                      value, MEDICAL_REPORTS_CLAIMED_MIN, MEDICAL_REPORTS_CLAIMED_MAX),
              List.of(
                  String.valueOf(MEDICAL_REPORTS_CLAIMED_MIN),
                  String.valueOf(MEDICAL_REPORTS_CLAIMED_MAX))));

  private static final int SURGERY_CLIENTS_COUNT_MIN = 1;
  private static final int SURGERY_CLIENTS_COUNT_MAX = 20;

  private static final List<FieldRuleSpec> SURGERY_CLIENTS_COUNT_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value ->
                  isIntegerOutOfRange(value, SURGERY_CLIENTS_COUNT_MIN, SURGERY_CLIENTS_COUNT_MAX),
              List.of(
                  String.valueOf(SURGERY_CLIENTS_COUNT_MIN),
                  String.valueOf(SURGERY_CLIENTS_COUNT_MAX))));

  private static final int SURGERY_MATTERS_COUNT_MIN = 0;
  private static final int SURGERY_MATTERS_COUNT_MAX = 99;

  private static final List<FieldRuleSpec> SURGERY_MATTERS_COUNT_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value ->
                  isIntegerOutOfRange(value, SURGERY_MATTERS_COUNT_MIN, SURGERY_MATTERS_COUNT_MAX),
              List.of(
                  String.valueOf(SURGERY_MATTERS_COUNT_MIN),
                  String.valueOf(SURGERY_MATTERS_COUNT_MAX))));

  private static final int HOME_OFFICE_MIN = 0;
  private static final int HOME_OFFICE_MAX = 9;

  private static final List<FieldRuleSpec> HOME_OFFICE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value -> isIntegerOutOfRange(value, HOME_OFFICE_MIN, HOME_OFFICE_MAX),
              List.of(String.valueOf(HOME_OFFICE_MIN), String.valueOf(HOME_OFFICE_MAX))));

  private static final Pattern MENTAL_HEALTH_TRIBUNAL_REFERENCE_PATTERN =
      Pattern.compile("^([A-Z]{2}/[0-9]{4}/[0-9]{5}|[A-Z]{2}[0-9]{5})$");

  private static final List<FieldRuleSpec> MENTAL_HEALTH_TRIBUNAL_REFERENCE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.mentalHealthTribunalReference.invalidFormat",
              value -> !MENTAL_HEALTH_TRIBUNAL_REFERENCE_PATTERN.matcher(value).matches()));

  private static final int PRIOR_AUTHORITY_REFERENCE_LENGTH = 7;

  private static final List<FieldRuleSpec> PRIOR_AUTHORITY_REFERENCE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.alphanumeric.invalidFormat",
              value -> !ALPHANUMERIC.matcher(value).matches()),
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.wrongLength",
              value -> value.length() != PRIOR_AUTHORITY_REFERENCE_LENGTH,
              List.of(String.valueOf(PRIOR_AUTHORITY_REFERENCE_LENGTH))));

  private static final BigDecimal LARGE_COST_MIN = new BigDecimal("0.00");
  private static final BigDecimal LARGE_COST_MAX = new BigDecimal("999999999.99");

  private static final List<FieldRuleSpec> LARGE_COST_RANGE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.range",
              value -> isDecimalOutOfRange(value, LARGE_COST_MIN, LARGE_COST_MAX),
              List.of(LARGE_COST_MIN.toString(), LARGE_COST_MAX.toString())));

  private static final BigDecimal DISBURSEMENTS_VAT_MIN = new BigDecimal("0.00");

  private static final List<FieldRuleSpec> DISBURSEMENTS_VAT_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.min",
              value -> isDecimalBelowMin(value, DISBURSEMENTS_VAT_MIN),
              List.of(DISBURSEMENTS_VAT_MIN.toString())));

  private static final BigDecimal DETENTION_TRAVEL_MIN = new BigDecimal("0.00");
  private static final BigDecimal DETENTION_TRAVEL_MAX = new BigDecimal("99999999.99");

  private static final List<FieldRuleSpec> DETENTION_TRAVEL_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.range",
              value -> isDecimalOutOfRange(value, DETENTION_TRAVEL_MIN, DETENTION_TRAVEL_MAX),
              List.of(DETENTION_TRAVEL_MIN.toString(), DETENTION_TRAVEL_MAX.toString())));

  private static final int SMALL_COUNT_MIN = 0;
  private static final int SMALL_COUNT_MAX = 9;

  private static final List<FieldRuleSpec> SMALL_COUNT_RANGE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value -> isIntegerOutOfRange(value, SMALL_COUNT_MIN, SMALL_COUNT_MAX),
              List.of(String.valueOf(SMALL_COUNT_MIN), String.valueOf(SMALL_COUNT_MAX))));

  private static final Map<ClaimViewField<?>, List<FieldRuleSpec>> RULES =
      Map.<ClaimViewField<?>, List<FieldRuleSpec>>ofEntries(
          Map.entry(ClaimDetailsViewField.INITIAL, NAME_RULES),
          Map.entry(ClaimDetailsViewField.FORENAME, NAME_RULES),
          Map.entry(ClaimDetailsViewField.SURNAME, NAME_RULES),
          Map.entry(CivilClaimDetailsViewField.POSTCODE, POSTCODE_RULES),
          Map.entry(MediationClaimDetailsViewField.POSTCODE, POSTCODE_RULES),
          Map.entry(CivilClaimDetailsViewField.UNIQUE_CLIENT_NUMBER, UNIQUE_CLIENT_NUMBER_RULES),
          Map.entry(
              MediationClaimDetailsViewField.UNIQUE_CLIENT_NUMBER, UNIQUE_CLIENT_NUMBER_RULES),
          Map.entry(
              CivilClaimDetailsViewField.HOME_OFFICE_CLIENT_NUMBER,
              HOME_OFFICE_CLIENT_NUMBER_RULES),
          Map.entry(MediationClaimDetailsViewField.CLIENT_2_FORENAME, NAME_RULES),
          Map.entry(MediationClaimDetailsViewField.CLIENT_2_SURNAME, NAME_RULES),
          Map.entry(MediationClaimDetailsViewField.CLIENT_2_POSTCODE, POSTCODE_RULES),
          Map.entry(MediationClaimDetailsViewField.CLIENT_2_UCN, UNIQUE_CLIENT_NUMBER_RULES),
          Map.entry(CrimeClaimDetailsViewField.MATTER_TYPE_CODE, CRIME_MATTER_TYPE_CODE_RULES),
          Map.entry(CivilClaimDetailsViewField.MATTER_TYPE_CODE_1, CIVIL_MATTER_TYPE_CODE_RULES),
          Map.entry(CivilClaimDetailsViewField.MATTER_TYPE_CODE_2, CIVIL_MATTER_TYPE_CODE_RULES),
          Map.entry(
              MediationClaimDetailsViewField.MATTER_TYPE_CODE_1, MEDIATION_MATTER_TYPE_CODE_RULES),
          Map.entry(
              MediationClaimDetailsViewField.MATTER_TYPE_CODE_2, MEDIATION_MATTER_TYPE_CODE_RULES),
          Map.entry(ClaimDetailsViewField.FEE_CODE, FEE_CODE_RULES),
          Map.entry(
              CrimeClaimDetailsViewField.POLICE_STATION_COURT_PRISON_ID,
              POLICE_STATION_COURT_PRISON_ID_RULES),
          Map.entry(CrimeClaimDetailsViewField.SCHEME_ID, SCHEME_ID_RULES),
          Map.entry(CrimeClaimDetailsViewField.DSCC_NUMBER, DSCC_NUMBER_RULES),
          Map.entry(CrimeClaimDetailsViewField.MAAT_ID, MAAT_ID_RULES),
          Map.entry(
              CrimeClaimDetailsViewField.PRISON_LAW_PRIOR_APPROVAL_NUMBER,
              PRISON_LAW_PRIOR_APPROVAL_NUMBER_RULES),
          Map.entry(CrimeClaimDetailsViewField.SUSPECTS_DEFENDANTS_COUNT, COUNT_RANGE_RULES),
          Map.entry(
              CrimeClaimDetailsViewField.POLICE_STATION_COURT_ATTENDANCES_COUNT, COUNT_RANGE_RULES),
          Map.entry(CrimeClaimDetailsViewField.TRAVEL_COSTS, TRAVEL_COSTS_RULES),
          Map.entry(CrimeClaimDetailsViewField.WAITING_COSTS, WAITING_COSTS_RULES),
          Map.entry(MediationClaimDetailsViewField.CLAIM_ID, CLAIM_ID_RULES),
          Map.entry(
              MediationClaimDetailsViewField.MEDIATION_SESSIONS_COUNT,
              MEDIATION_SESSIONS_COUNT_RULES),
          Map.entry(
              MediationClaimDetailsViewField.MEDIATION_TIME_MINUTES, MEDIATION_TIME_MINUTES_RULES),
          Map.entry(MediationClaimDetailsViewField.OUTREACH_LOCATION, OUTREACH_LOCATION_RULES),
          Map.entry(ClaimDetailsViewField.CASE_REFERENCE_NUMBER, CASE_REFERENCE_NUMBER_RULES),
          Map.entry(ClaimDetailsViewField.UNIQUE_FILE_NUMBER, UNIQUE_FILE_NUMBER_RULES),
          Map.entry(CivilClaimDetailsViewField.CASE_ID, CLAIM_ID_RULES),
          Map.entry(
              CivilClaimDetailsViewField.SCHEDULE_REFERENCE_CIVIL, SCHEDULE_REFERENCE_CIVIL_RULES),
          Map.entry(CivilClaimDetailsViewField.PROCUREMENT_AREA, LOCATION_CODE_RULES),
          Map.entry(CivilClaimDetailsViewField.DELIVERY_LOCATION, LOCATION_CODE_RULES),
          Map.entry(CivilClaimDetailsViewField.ACCESS_POINT, ACCESS_POINT_RULES),
          Map.entry(
              CivilClaimDetailsViewField.LOCAL_AUTHORITY_NUMBER, LOCAL_AUTHORITY_NUMBER_RULES),
          Map.entry(CivilClaimDetailsViewField.VALUE_OF_COSTS, MEDIUM_COST_RANGE_RULES),
          Map.entry(
              CivilClaimDetailsViewField.EXCEPTIONAL_CASE_FUNDING, EXCEPTIONAL_CASE_FUNDING_RULES),
          Map.entry(
              CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_REFERENCE,
              CIVIL_LEGAL_ADVICE_REFERENCE_RULES),
          Map.entry(
              CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_EXEMPTION,
              CIVIL_LEGAL_ADVICE_EXEMPTION_RULES),
          Map.entry(CivilClaimDetailsViewField.ADVICE_TIME, TIME_RANGE_RULES),
          Map.entry(CivilClaimDetailsViewField.TRAVEL_TIME, TIME_RANGE_RULES),
          Map.entry(CivilClaimDetailsViewField.WAITING_TIME, TIME_RANGE_RULES),
          Map.entry(
              CivilClaimDetailsViewField.MEDICAL_REPORTS_CLAIMED, MEDICAL_REPORTS_CLAIMED_RULES),
          Map.entry(CivilClaimDetailsViewField.SURGERY_CLIENTS_COUNT, SURGERY_CLIENTS_COUNT_RULES),
          Map.entry(CivilClaimDetailsViewField.SURGERY_MATTERS_COUNT, SURGERY_MATTERS_COUNT_RULES),
          Map.entry(CivilClaimDetailsViewField.HOME_OFFICE, HOME_OFFICE_RULES),
          Map.entry(
              CivilClaimDetailsViewField.MENTAL_HEALTH_TRIBUNAL_REFERENCE,
              MENTAL_HEALTH_TRIBUNAL_REFERENCE_RULES),
          Map.entry(
              CivilClaimDetailsViewField.PRIOR_AUTHORITY_REFERENCE,
              PRIOR_AUTHORITY_REFERENCE_RULES),
          Map.entry(ClaimDetailsViewField.PROFIT_COST, LARGE_COST_RANGE_RULES),
          Map.entry(ClaimDetailsViewField.DISBURSEMENTS, LARGE_COST_RANGE_RULES),
          Map.entry(ClaimDetailsViewField.DISBURSEMENTS_VAT, DISBURSEMENTS_VAT_RULES),
          Map.entry(CivilClaimDetailsViewField.COUNSELS_COST, MEDIUM_COST_RANGE_RULES),
          Map.entry(CivilClaimDetailsViewField.TRAVEL_AND_WAITING_COSTS, TRAVEL_COSTS_RULES),
          Map.entry(CivilClaimDetailsViewField.DETENTION_TRAVEL, DETENTION_TRAVEL_RULES),
          Map.entry(CivilClaimDetailsViewField.JR_FORM_FILLING, TRAVEL_COSTS_RULES),
          Map.entry(CivilClaimDetailsViewField.ADJOURNED_HEARING_FEE, SMALL_COUNT_RANGE_RULES),
          Map.entry(CivilClaimDetailsViewField.CMRH_TELEPHONE, SMALL_COUNT_RANGE_RULES),
          Map.entry(CivilClaimDetailsViewField.CMRH_ORAL, SMALL_COUNT_RANGE_RULES));

  private ClaimFieldRules() {}

  public static boolean hasRules(ClaimViewField<?> field) {
    return RULES.containsKey(field);
  }

  public static List<FieldRuleSpec> rulesFor(ClaimViewField<?> field) {
    return RULES.getOrDefault(field, List.of());
  }

  private static boolean isIntegerOutOfRange(String value, int min, int max) {
    try {
      var parsed = Integer.parseInt(value.trim());
      return parsed < min || parsed > max;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private static boolean isDecimalOutOfRange(String value, BigDecimal min, BigDecimal max) {
    try {
      var parsed = new BigDecimal(value.trim());
      return parsed.compareTo(min) < 0 || parsed.compareTo(max) > 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private static boolean isDecimalBelowMin(String value, BigDecimal min) {
    try {
      var parsed = new BigDecimal(value.trim());
      return parsed.compareTo(min) < 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
