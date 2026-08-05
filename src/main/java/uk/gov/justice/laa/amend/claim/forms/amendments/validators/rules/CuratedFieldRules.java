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

/**
 * Curated per-field rule data, seeded from {@code claim-fields.schema.json} but hand-reconciled to
 * this app's UI/domain semantics rather than copied literally (see
 * docs/adr/0006-per-field-amendment-validation-curation-spec.md). Only amendable fields curated so
 * far appear here; an absent field simply has no curated rules.
 */
public final class CuratedFieldRules {

  private static final int NAME_MAX_LENGTH = 30;

  // Schema seed: client_forename/client_surname pattern `^[\p{L}\p{N}\p{Zs}\-''&]{1,30}$`,
  // reconciled here as a charset-only check (length is enforced as its own, lower-priority rule).
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

  // Schema seed: client_postcode pattern `^NFA|[A-Z]{1,2}[0-9][0-9A-Z]?\s?[0-9][A-Z]{2}$`
  // (its unanchored "NFA" alternative is a schema bug), reconciled here as a fully-anchored,
  // case-insensitive match against "NFA" or a standard UK postcode shape. Identical for Civil and
  // Mediation, so one rule definition is shared by both declarations.
  private static final Pattern POSTCODE_PATTERN =
      Pattern.compile(
          "^(NFA|[A-Za-z]{1,2}[0-9][0-9A-Za-z]?\\s?[0-9][A-Za-z]{2})$", Pattern.CASE_INSENSITIVE);

  private static final List<FieldRuleSpec> POSTCODE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.postcode.invalidFormat",
              value -> !POSTCODE_PATTERN.matcher(value).matches()));

  // Schema seed: unique_client_number pattern
  // `^(0[1-9]|[12][0-9]|3[01])(0[1-9]|1[0-2])([0-9]{4})/[\p{L}0-9 \-''&]/[\p{L}0-9 \-''&]{1,4}$`,
  // maxLength 15 - the pattern's own length bounds (12-15 chars) already cover that maxLength, so a
  // separate LENGTH rule isn't needed. Identical for Civil and Mediation, so one rule definition is
  // shared by both declarations.
  private static final Pattern UNIQUE_CLIENT_NUMBER_PATTERN =
      Pattern.compile(
          "^(0[1-9]|[12][0-9]|3[01])(0[1-9]|1[0-2])[0-9]{4}/[\\p{L}0-9 '&-]/[\\p{L}0-9 '&-]{1,4}$");

  private static final List<FieldRuleSpec> UNIQUE_CLIENT_NUMBER_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.uniqueClientNumber.invalidFormat",
              value -> !UNIQUE_CLIENT_NUMBER_PATTERN.matcher(value).matches()));

  // Schema seed: home_office_client_number pattern `^[a-zA-Z0-9]+$`, maxLength 16. Civil-only
  // field.
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

  // Schema seed: crime_matter_type_code pattern
  // `^(?:$|0[1-9]|[1-9]|1[0-3]|1[56]|1[89]|2[13]|3[3-8])$`, reconciled here dropping the schema's
  // redundant blank alternative (blank values never reach curated rules - see
  // CuratedFieldRuleValidator) and treated as MEMBERSHIP, since the schema encodes a fixed
  // permitted
  // set of matter type codes rather than a general format shape. Crime-only field.
  private static final Pattern CRIME_MATTER_TYPE_CODE_PATTERN =
      Pattern.compile("^(0[1-9]|[1-9]|1[0-3]|1[56]|1[89]|2[13]|3[3-8])$");

  private static final List<FieldRuleSpec> CRIME_MATTER_TYPE_CODE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.MEMBERSHIP,
              "amendmentForm.matterTypeCode.invalid",
              value -> !CRIME_MATTER_TYPE_CODE_PATTERN.matcher(value).matches()));

  // Schema seed: matter_type_code has no pattern, only area-of-law-specific prose messages -
  // "Each Matter Type Code 1 and 2 must be 4 characters" for LEGAL HELP (Civil), reconciled here as
  // a length-only check (no character-set constraint called out for Civil).
  private static final int MATTER_TYPE_CODE_LENGTH = 4;

  private static final List<FieldRuleSpec> CIVIL_MATTER_TYPE_CODE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.matterTypeCode.wrongLength",
              value -> value.length() != MATTER_TYPE_CODE_LENGTH,
              List.of(String.valueOf(MATTER_TYPE_CODE_LENGTH))));

  // Schema seed: matter_type_code prose message "Each Matter Type Code 1 and 2 must be 4 uppercase
  // characters" for MEDIATION, reconciled here as a single FORMAT check combining both the length
  // and the uppercase-alphanumeric shape, mirroring the schema's own single combined message
  // (compare POSTCODE_RULES). Mediation-only.
  private static final Pattern MEDIATION_MATTER_TYPE_CODE_PATTERN =
      Pattern.compile("^[A-Z0-9]{4}$");

  private static final List<FieldRuleSpec> MEDIATION_MATTER_TYPE_CODE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.matterTypeCode.invalidUppercaseFormat",
              value -> !MEDIATION_MATTER_TYPE_CODE_PATTERN.matcher(value).matches()));

  // Schema seed: fee_code pattern `^[a-zA-Z0-9]+$`, maxLength 10 - tighter than the generic 50-char
  // FieldType.TEXT check, so curated here as additive local rules alongside
  // FeeCodeAmendmentFieldValidator's separate FSP-membership check. Shared area of law.
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

  // Schema seed: police_station_court_prison_id pattern `^(?=.*[A-Za-z])[A-Za-z0-9]{1,6}$`,
  // reconciled here as two separate rules - an alphanumeric-with-at-least-one-letter format check,
  // then a 6-char length cap - mirroring the FORMAT/LENGTH split used for
  // HOME_OFFICE_CLIENT_NUMBER_RULES rather than one combined pattern. Crime-only field.
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

  // Schema seed: scheme_id pattern `^[a-zA-Z0-9]{4}$`, reconciled as separate FORMAT
  // (alphanumeric) and LENGTH (must be exactly 4) rules rather than one combined pattern, so an
  // over-length non-alphanumeric value deterministically surfaces the FORMAT failure first.
  // Crime-only field.
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

  // Schema seed: dscc_number pattern `^[a-zA-Z0-9]{10}$`, reconciled as separate FORMAT/LENGTH
  // rules for the same reason as SCHEME_ID_RULES. Crime-only field.
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

  // Schema seed: maat_id pattern `^[a-zA-Z0-9]{1,10}$`, reconciled as FORMAT (alphanumeric) plus
  // a LENGTH cap (unlike SCHEME_ID/DSCC_NUMBER, the schema's lower bound of 1 is redundant with
  // blank values never reaching curated rules, so only a max-length check is needed). Crime-only
  // field.
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

  // Schema seed: prison_law_prior_approval_number pattern `^[a-zA-Z0-9]{10}$`, reconciled as
  // separate FORMAT/LENGTH rules for the same reason as SCHEME_ID_RULES. Crime-only field.
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

  // Schema seed: suspects_defendants_count and police_station_court_attendances_count are both
  // `"type": "integer", "minimum": 0, "maximum": 99`, so share one NUMERIC_RANGE rule. Parseability
  // is already owned by the generic NumberAmendmentFieldValidator, so the range predicate treats an
  // unparseable value as not-out-of-range and leaves that failure to the generic layer. Crime-only
  // fields.
  private static final int COUNT_MIN = 0;
  private static final int COUNT_MAX = 99;

  private static final List<FieldRuleSpec> COUNT_RANGE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value -> isIntegerOutOfRange(value, COUNT_MIN, COUNT_MAX),
              List.of(String.valueOf(COUNT_MIN), String.valueOf(COUNT_MAX))));

  // Schema seed: travel_waiting_costs_amount `"minimum": 0.0, "maximum": 9999.99`. Precision
  // (multipleOf 0.01) is already enforced generically - AmendmentForm#getBigDecimalValue rejects
  // values with more than 2 decimal places - so only the range is curated here. Shared with
  // Civil's TRAVEL_AND_WAITING_COSTS (same schema field, ticket 09) and Civil's JR_FORM_FILLING
  // (jr_form_filling_amount carries the identical `"minimum": 0.0, "maximum": 9999.99` bound,
  // ticket 09), so all three reuse this one rule definition.
  private static final BigDecimal TRAVEL_COSTS_MIN = new BigDecimal("0.00");
  private static final BigDecimal TRAVEL_COSTS_MAX = new BigDecimal("9999.99");

  private static final List<FieldRuleSpec> TRAVEL_COSTS_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.range",
              value -> isDecimalOutOfRange(value, TRAVEL_COSTS_MIN, TRAVEL_COSTS_MAX),
              List.of(TRAVEL_COSTS_MIN.toString(), TRAVEL_COSTS_MAX.toString())));

  // Schema seed: net_waiting_costs_amount `"minimum": 0.0, "maximum": 999999.99`. Same precision
  // reasoning as TRAVEL_COSTS_RULES. Crime-only field.
  private static final BigDecimal WAITING_COSTS_MIN = new BigDecimal("0.00");
  private static final BigDecimal WAITING_COSTS_MAX = new BigDecimal("999999.99");

  private static final List<FieldRuleSpec> WAITING_COSTS_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.range",
              value -> isDecimalOutOfRange(value, WAITING_COSTS_MIN, WAITING_COSTS_MAX),
              List.of(WAITING_COSTS_MIN.toString(), WAITING_COSTS_MAX.toString())));

  // Schema seed: case_id pattern `^[0-9]{3}$`, reconciled as a single combined FORMAT check
  // (length and digit-only shape are inseparable for a fixed 3-digit code), mirroring
  // MEDIATION_MATTER_TYPE_CODE_RULES. Shared by Mediation's CLAIM_ID and Civil's CASE_ID - both
  // back onto the same schema seed with identical domain semantics.
  private static final Pattern CLAIM_ID_PATTERN = Pattern.compile("^[0-9]{3}$");

  private static final List<FieldRuleSpec> CLAIM_ID_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.claimId.invalidFormat",
              value -> !CLAIM_ID_PATTERN.matcher(value).matches()));

  // Schema seed: case_reference_number pattern `^[a-zA-Z0-9/.\-\s]+$`, maxLength 30, reconciled as
  // separate FORMAT (charset) and LENGTH rules, mirroring HOME_OFFICE_CLIENT_NUMBER_RULES. Shared
  // ClaimDetailsViewField, used across Civil, Crime and Mediation.
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

  // Schema seed: unique_file_number pattern `^[0-9]{6}/[0-9]{3}$`, reconciled here as a single
  // combined FORMAT check (a fixed DDMMYY/NNN shape, length inseparable from the pattern),
  // mirroring CLAIM_ID_RULES. The schema's "date in the past" prose is a semantic check beyond
  // this local syntactic rule and is not curated here. Shared ClaimDetailsViewField.
  private static final Pattern UNIQUE_FILE_NUMBER_PATTERN = Pattern.compile("^[0-9]{6}/[0-9]{3}$");

  private static final List<FieldRuleSpec> UNIQUE_FILE_NUMBER_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.uniqueFileNumber.invalidFormat",
              value -> !UNIQUE_FILE_NUMBER_PATTERN.matcher(value).matches()));

  // Schema seed: schedule_reference prose message (keyed "LEGAL HELP", i.e. Civil) "must be a
  // maximum of 20 characters and contain only letters, numbers, forward slashes, periods, and
  // hyphens", reconciled as separate FORMAT (charset) and LENGTH rules, same split as
  // CASE_REFERENCE_NUMBER_RULES. Civil-only field (see the Mediation SCHEDULE_REFERENCE exclusion
  // note below).
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

  // Schema seed: procurement_area_code and delivery_location share the identical pattern
  // `^[A-Z]{2}[0-9]{5}$`, reconciled as one combined FORMAT check (fixed shape, no separately
  // stated length bound), mirroring MEDIATION_MATTER_TYPE_CODE_RULES. Shared by both Civil fields.
  private static final Pattern LOCATION_CODE_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{5}$");

  private static final List<FieldRuleSpec> LOCATION_CODE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.locationCode.invalidFormat",
              value -> !LOCATION_CODE_PATTERN.matcher(value).matches()));

  // Schema seed: access_point_code pattern `^AP[0-9]{5}$`, reconciled as one combined FORMAT
  // check, same reasoning as LOCATION_CODE_RULES. Civil-only field.
  private static final Pattern ACCESS_POINT_PATTERN = Pattern.compile("^AP[0-9]{5}$");

  private static final List<FieldRuleSpec> ACCESS_POINT_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.accessPoint.invalidFormat",
              value -> !ACCESS_POINT_PATTERN.matcher(value).matches()));

  // Schema seed: local_authority_number pattern `^[a-zA-Z0-9]+$`, maxLength 30, reconciled as
  // separate FORMAT (alphanumeric, reusing ALPHANUMERIC) and LENGTH rules, same split as
  // MAAT_ID_RULES. Civil-only field.
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

  // Schema seed: mediation_sessions_count `"type": "integer", "minimum": 1, "maximum": 99`.
  // Mediation-only field.
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

  // Schema seed: mediation_time_minutes `"type": "integer", "minimum": 0, "maximum": 99999`.
  // Mediation-only field.
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

  // Schema seed: outreach_location pattern `^[a-zA-Z0-9]{3}$`, reconciled as separate FORMAT
  // (alphanumeric) and LENGTH (must be exactly 3) rules for the same reason as SCHEME_ID_RULES, so
  // an over-length non-alphanumeric value deterministically surfaces the FORMAT failure first.
  // Mediation-only field.
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

  // Schema seed: costs_damages_recovered_amount and net_counsel_costs_amount (ticket 09) both
  // `"minimum": 0.0, "maximum": 99999.99`, so share one NUMERIC_RANGE rule, same reasoning as
  // COUNT_RANGE_RULES. Same precision reasoning as TRAVEL_COSTS_RULES (multipleOf 0.01 already
  // enforced generically). Civil-only fields.
  private static final BigDecimal MEDIUM_COST_MIN = new BigDecimal("0.00");
  private static final BigDecimal MEDIUM_COST_MAX = new BigDecimal("99999.99");

  private static final List<FieldRuleSpec> MEDIUM_COST_RANGE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.range",
              value -> isDecimalOutOfRange(value, MEDIUM_COST_MIN, MEDIUM_COST_MAX),
              List.of(MEDIUM_COST_MIN.toString(), MEDIUM_COST_MAX.toString())));

  // Schema seed: exceptional_case_funding_reference pattern `^[0-9]{7}[A-Z]{2}$`, reconciled here
  // as a single combined FORMAT check (fixed shape, length inseparable from the pattern), mirroring
  // CLAIM_ID_RULES. Civil-only field.
  private static final Pattern EXCEPTIONAL_CASE_FUNDING_PATTERN =
      Pattern.compile("^[0-9]{7}[A-Z]{2}$");

  private static final List<FieldRuleSpec> EXCEPTIONAL_CASE_FUNDING_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.exceptionalCaseFunding.invalidFormat",
              value -> !EXCEPTIONAL_CASE_FUNDING_PATTERN.matcher(value).matches()));

  // Schema seed: cla_reference_number pattern `^[0-9]{1,7}$`, reconciled here as a single combined
  // FORMAT check (the pattern's own {1,7} bound already covers length), mirroring CLAIM_ID_RULES.
  // Civil-only field.
  private static final Pattern CIVIL_LEGAL_ADVICE_REFERENCE_PATTERN =
      Pattern.compile("^[0-9]{1,7}$");

  private static final List<FieldRuleSpec> CIVIL_LEGAL_ADVICE_REFERENCE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.civilLegalAdviceReference.invalidFormat",
              value -> !CIVIL_LEGAL_ADVICE_REFERENCE_PATTERN.matcher(value).matches()));

  // Schema seed: cla_exemption_code `minLength: 4, maxLength: 4` with no pattern, reconciled here
  // as a LENGTH-only check (no character-set constraint called out). Civil-only field.
  private static final int CIVIL_LEGAL_ADVICE_EXEMPTION_LENGTH = 4;

  private static final List<FieldRuleSpec> CIVIL_LEGAL_ADVICE_EXEMPTION_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.LENGTH,
              "amendmentForm.text.wrongLength",
              value -> value.length() != CIVIL_LEGAL_ADVICE_EXEMPTION_LENGTH,
              List.of(String.valueOf(CIVIL_LEGAL_ADVICE_EXEMPTION_LENGTH))));

  // Schema seed: advice_time and travel_time and waiting_time are all
  // `"type": "integer", "minimum": 0, "maximum": 99999`, so share one NUMERIC_RANGE rule, same
  // reasoning as COUNT_RANGE_RULES. Civil-only fields.
  private static final int TIME_MIN = 0;
  private static final int TIME_MAX = 99999;

  private static final List<FieldRuleSpec> TIME_RANGE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value -> isIntegerOutOfRange(value, TIME_MIN, TIME_MAX),
              List.of(String.valueOf(TIME_MIN), String.valueOf(TIME_MAX))));

  // Schema seed: medical_reports_count `"type": "integer", "minimum": 0, "maximum": 10` - the
  // schema's own prose message ("must be 20 or less") doesn't match its own minimum/maximum, so
  // this rule is curated against the actual numeric bounds rather than the mismatched prose.
  // Civil-only field.
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

  // Schema seed: surgery_clients_count `"type": "integer", "minimum": 1, "maximum": 20`.
  // Civil-only field.
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

  // Schema seed: surgery_matters_count `"type": "integer", "minimum": 0, "maximum": 99}.
  // Civil-only field.
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

  // Schema seed: ho_interview `"type": "integer", "minimum": 0, "maximum": 9}. Civil-only field.
  private static final int HOME_OFFICE_MIN = 0;
  private static final int HOME_OFFICE_MAX = 9;

  private static final List<FieldRuleSpec> HOME_OFFICE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value -> isIntegerOutOfRange(value, HOME_OFFICE_MIN, HOME_OFFICE_MAX),
              List.of(String.valueOf(HOME_OFFICE_MIN), String.valueOf(HOME_OFFICE_MAX))));

  // Schema seed: mental_health_tribunal_reference pattern
  // `^([A-Z]{2}/[0-9]{4}/[0-9]{5}|[A-Z]{2}[0-9]{5})$` (English AA/NNNN/NNNNN or Welsh AANNNNN),
  // reconciled here as a single combined FORMAT check. Civil-only field.
  private static final Pattern MENTAL_HEALTH_TRIBUNAL_REFERENCE_PATTERN =
      Pattern.compile("^([A-Z]{2}/[0-9]{4}/[0-9]{5}|[A-Z]{2}[0-9]{5})$");

  private static final List<FieldRuleSpec> MENTAL_HEALTH_TRIBUNAL_REFERENCE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.mentalHealthTribunalReference.invalidFormat",
              value -> !MENTAL_HEALTH_TRIBUNAL_REFERENCE_PATTERN.matcher(value).matches()));

  // Schema seed: prior_authority_reference pattern `^[a-zA-Z0-9]+$`, minLength 7, maxLength 7,
  // reconciled as separate FORMAT (alphanumeric) and LENGTH (must be exactly 7) rules, same
  // reasoning as SCHEME_ID_RULES. Civil-only field.
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

  // Schema seed: net_profit_costs_amount and net_disbursement_amount (ticket 09) both
  // `"minimum": 0.0, "maximum": 999999999.99`, so share one NUMERIC_RANGE rule, same reasoning as
  // MEDIUM_COST_RANGE_RULES. Same precision reasoning as TRAVEL_COSTS_RULES. Shared
  // ClaimDetailsViewField, used across Civil, Crime and Mediation.
  private static final BigDecimal LARGE_COST_MIN = new BigDecimal("0.00");
  private static final BigDecimal LARGE_COST_MAX = new BigDecimal("999999999.99");

  private static final List<FieldRuleSpec> LARGE_COST_RANGE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.range",
              value -> isDecimalOutOfRange(value, LARGE_COST_MIN, LARGE_COST_MAX),
              List.of(LARGE_COST_MIN.toString(), LARGE_COST_MAX.toString())));

  // Schema seed: disbursements_vat_amount `"minimum": 0.0`, no maximum stated - reconciled as a
  // min-only NUMERIC_RANGE rule (unlike the other cost fields, which all carry an explicit
  // maximum). Same precision reasoning as TRAVEL_COSTS_RULES. Shared ClaimDetailsViewField.
  private static final BigDecimal DISBURSEMENTS_VAT_MIN = new BigDecimal("0.00");

  private static final List<FieldRuleSpec> DISBURSEMENTS_VAT_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.min",
              value -> isDecimalBelowMin(value, DISBURSEMENTS_VAT_MIN),
              List.of(DISBURSEMENTS_VAT_MIN.toString())));

  // Schema seed: detention_travel_waiting_costs_amount `"minimum": 0.0, "maximum": 99999999.99`.
  // Same precision reasoning as TRAVEL_COSTS_RULES. Civil-only field.
  private static final BigDecimal DETENTION_TRAVEL_MIN = new BigDecimal("0.00");
  private static final BigDecimal DETENTION_TRAVEL_MAX = new BigDecimal("99999999.99");

  private static final List<FieldRuleSpec> DETENTION_TRAVEL_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.bigDecimal.range",
              value -> isDecimalOutOfRange(value, DETENTION_TRAVEL_MIN, DETENTION_TRAVEL_MAX),
              List.of(DETENTION_TRAVEL_MIN.toString(), DETENTION_TRAVEL_MAX.toString())));

  // Schema seed: adjourned_hearing_fee_amount, cmrh_oral_count and cmrh_telephone_count are all
  // `"type": "integer", "minimum": 0, "maximum": 9`, so share one NUMERIC_RANGE rule, same
  // reasoning as COUNT_RANGE_RULES. Civil-only fields.
  private static final int SMALL_COUNT_MIN = 0;
  private static final int SMALL_COUNT_MAX = 9;

  private static final List<FieldRuleSpec> SMALL_COUNT_RANGE_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.NUMERIC_RANGE,
              "amendmentForm.number.range",
              value -> isIntegerOutOfRange(value, SMALL_COUNT_MIN, SMALL_COUNT_MAX),
              List.of(String.valueOf(SMALL_COUNT_MIN), String.valueOf(SMALL_COUNT_MAX))));

  // VAT (ClaimDetailsViewField) is a BOOLEAN field fully covered by the generic FieldType
  // validator, same reasoning as the other boolean fields above - the schema's is_vat_applicable
  // entry is just `"type": "boolean"` with no shape beyond Y/N to reconcile. FIXED_FEE
  // (ClaimDetailsViewField) is declared non-editable (see its false `editable` flag), so it never
  // reaches amendment validation at all; there is no curated rule to add for it.
  //
  // OUTCOME_FOR_CLIENT, DESIGNATED_ACCREDITED_REPRESENTATIVE, MEETINGS_ATTENDED, ADVICE_TYPE and
  // EXEMPTION_CRITERIA_SATISFIED (Civil) are ENUM fields backed by a fixed FieldOptions list
  // (OutcomeCode, DesignatedAccreditedRepresentative, MeetingsAttended, AdviceType,
  // ExemptionCriteriaSatisfied) whose values already conform to their respective schema patterns
  // (`^[1-5]$`, `^MTGA(0[1-9]|1[0-9]|2[0-4])$`, `^(FTF|REM)$`, `^[A-Z]{2}[0-9]{3}$`), so membership
  // is already enforced generically by EnumAmendmentFieldValidator, same reasoning as the other
  // ENUM exclusions above. ADDITIONAL_TRAVEL_PAYMENT, TOLERANCE_INDICATOR, LEGACY_CASE,
  // IRC_SURGERY, IS_NRM_ADVICE, SUBSTANTIVE_HEARING and IS_LONDON_RATE (Civil) are BOOLEAN fields
  // fully covered by the generic FieldType validator, same as the other boolean fields above.
  // TRANSFER_DATE and SURGERY_DATE (Civil) are DATE fields fully covered by the generic
  // DateAmendmentFieldValidator, same reasoning as the other date fields above - surgery_date's
  // schema entry doesn't even carry a real date pattern (just a maxLength and a generic "invalid"
  // message), so there is no shape to reconcile beyond what date parsing already rejects.
  // FOLLOW_ON_WORK (Civil) has no pattern or length bound in the schema (just `"type": "string"`),
  // so there is no schema-backed rule to curate here; it stays on the generic TEXT FieldType check,
  // same reasoning as the COURT_LOCATION and Mediation SCHEDULE_REFERENCE exclusions above.
  // GENDER, ETHNICITY, DISABILITY (here) and CLIENT_TYPE (Civil) are ENUM fields not present in the
  // schema at all - they have no local syntactic rules beyond membership, which the generic
  // EnumAmendmentFieldValidator already enforces with a field-name-aware message. DATE_OF_BIRTH
  // (Civil/Mediation) and the boolean client fields are likewise fully covered by their generic
  // FieldType validators already; the journey's day/month/year date input has no schema-literal
  // format to reconcile against. None of these need a curated entry here. The same reasoning
  // applies to their Mediation client-2 counterparts below. STAGE_REACHED, STANDARD_FEE_CATEGORY
  // and OUTCOME_FOR_CLIENT (Crime) are likewise ENUM fields backed by a fixed FieldOptions list, so
  // membership is already enforced generically. REPRESENTATION_ORDER_DATE (Crime) is a DATE field
  // fully covered by the generic DateAmendmentFieldValidator, same as the other date fields above.
  // IS_DUTY_SOLICITOR and IS_YOUTH_COURT (Crime) are BOOLEAN fields fully covered generically, same
  // as the other boolean fields above. OUTCOME and REFERRAL_SOURCE (Mediation) are likewise ENUM
  // fields backed by a fixed FieldOptions list (OutcomeCode, ReferralSource), so membership is
  // already enforced generically, same reasoning as the Crime enum fields above. UNIQUE_CASE_ID
  // (Mediation)'s schema pattern only requires at least one non-whitespace character - i.e. "not
  // blank" - which is already fully enforced by CuratedFieldRuleValidator's isBlank(...) guard
  // (commons-lang3 isBlank treats whitespace-only strings as blank) plus the generic required
  // layer, so no separate curated rule is needed. SCHEDULE_REFERENCE (Mediation) has no
  // Mediation-specific entry in the schema (only a "LEGAL HELP" - i.e. Civil - constraint), so
  // there is no schema-backed Mediation rule to curate here; it stays on the generic TEXT
  // FieldType check until/unless a Mediation-specific rule is identified. CASE_START_DATE,
  // CASE_CONCLUDED_DATE and CASE_CONCLUDED_CLAIMED_DATE (Civil) are DATE fields fully covered by
  // the generic DateAmendmentFieldValidator, same reasoning as the other date fields above - the
  // schema's calendar-date pattern has no shape left to reconcile once actual date parsing already
  // rejects impossible dates. CASE_STAGE and AIT_HEARING_CENTRE (Civil) are ENUM fields whose
  // FieldOptions (CaseStage, AitHearingCentre) already enumerate exactly the schema's permitted
  // code sets, so membership is already enforced generically, same reasoning as the other ENUM
  // exclusions above. COURT_LOCATION (Civil)'s schema entry has no pattern and its
  // validationErrorMessages text ("Eligible Client must be Y or N") is an unrelated copy-paste
  // bug, so there is no reliable schema signal to curate against; it stays on the generic TEXT
  // FieldType check.
  private static final Map<ClaimViewField<?>, List<FieldRuleSpec>> RULES =
      Map.ofEntries(
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

  private CuratedFieldRules() {}

  public static boolean hasRules(ClaimViewField<?> field) {
    return RULES.containsKey(field);
  }

  public static List<FieldRuleSpec> rulesFor(ClaimViewField<?> field) {
    return RULES.getOrDefault(field, List.of());
  }

  // Parseability is owned by the generic Number/BigDecimal field validators, so an unparseable
  // value is treated as not-out-of-range here, leaving that failure to the generic layer rather
  // than double-reporting it.
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
