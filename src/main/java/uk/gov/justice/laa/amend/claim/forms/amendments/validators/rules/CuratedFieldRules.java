package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
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

  // GENDER, ETHNICITY, DISABILITY (here) and CLIENT_TYPE (Civil) are ENUM fields not present in the
  // schema at all - they have no local syntactic rules beyond membership, which the generic
  // EnumAmendmentFieldValidator already enforces with a field-name-aware message. DATE_OF_BIRTH
  // (Civil/Mediation) and the boolean client fields are likewise fully covered by their generic
  // FieldType validators already; the journey's day/month/year date input has no schema-literal
  // format to reconcile against. None of these need a curated entry here.
  private static final Map<ClaimViewField<?>, List<FieldRuleSpec>> RULES =
      Map.of(
          ClaimDetailsViewField.INITIAL, NAME_RULES,
          ClaimDetailsViewField.FORENAME, NAME_RULES,
          ClaimDetailsViewField.SURNAME, NAME_RULES,
          CivilClaimDetailsViewField.POSTCODE, POSTCODE_RULES,
          MediationClaimDetailsViewField.POSTCODE, POSTCODE_RULES,
          CivilClaimDetailsViewField.UNIQUE_CLIENT_NUMBER, UNIQUE_CLIENT_NUMBER_RULES,
          MediationClaimDetailsViewField.UNIQUE_CLIENT_NUMBER, UNIQUE_CLIENT_NUMBER_RULES,
          CivilClaimDetailsViewField.HOME_OFFICE_CLIENT_NUMBER, HOME_OFFICE_CLIENT_NUMBER_RULES);

  private CuratedFieldRules() {}

  public static boolean hasRules(ClaimViewField<?> field) {
    return RULES.containsKey(field);
  }

  public static List<FieldRuleSpec> rulesFor(ClaimViewField<?> field) {
    return RULES.getOrDefault(field, List.of());
  }
}
