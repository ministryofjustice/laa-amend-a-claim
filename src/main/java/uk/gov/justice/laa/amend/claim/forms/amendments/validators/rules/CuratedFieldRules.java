package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

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

  private static final Map<ClaimViewField<?>, List<FieldRuleSpec>> RULES =
      Map.of(
          ClaimDetailsViewField.INITIAL, NAME_RULES,
          ClaimDetailsViewField.FORENAME, NAME_RULES,
          ClaimDetailsViewField.SURNAME, NAME_RULES);

  private CuratedFieldRules() {}

  public static boolean hasRules(ClaimViewField<?> field) {
    return RULES.containsKey(field);
  }

  public static List<FieldRuleSpec> rulesFor(ClaimViewField<?> field) {
    return RULES.getOrDefault(field, List.of());
  }
}
