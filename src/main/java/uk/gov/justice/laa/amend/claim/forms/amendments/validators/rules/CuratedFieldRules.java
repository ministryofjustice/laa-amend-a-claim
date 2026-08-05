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
  // values with more than 2 decimal places - so only the range is curated here. Crime-only field.
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

  // Schema seed: case_id (Mediation's CLAIM_ID) pattern `^[0-9]{3}$`, reconciled as a single
  // combined FORMAT check (length and digit-only shape are inseparable for a fixed 3-digit code),
  // mirroring MEDIATION_MATTER_TYPE_CODE_RULES. Mediation-only field.
  private static final Pattern CLAIM_ID_PATTERN = Pattern.compile("^[0-9]{3}$");

  private static final List<FieldRuleSpec> CLAIM_ID_RULES =
      List.of(
          new FieldRuleSpec(
              RuleCategory.FORMAT,
              "amendmentForm.claimId.invalidFormat",
              value -> !CLAIM_ID_PATTERN.matcher(value).matches()));

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
  // FieldType check until/unless a Mediation-specific rule is identified.
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
          Map.entry(MediationClaimDetailsViewField.OUTREACH_LOCATION, OUTREACH_LOCATION_RULES));

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
}
