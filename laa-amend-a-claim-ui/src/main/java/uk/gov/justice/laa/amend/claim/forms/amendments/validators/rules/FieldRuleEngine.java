package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

/** Selects the single highest-priority failing rule for a field's submitted value, if any. */
@UtilityClass
public class FieldRuleEngine {

  public Optional<FieldRuleSpec> firstFailingRule(
      List<FieldRuleSpec> rules, String value, ClaimDetails claimDetails) {
    return rules.stream()
        .filter(rule -> rule.isInvalid().test(claimDetails, value))
        .min(Comparator.comparing(FieldRuleSpec::category));
  }
}
