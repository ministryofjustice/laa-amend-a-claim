package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

/** Selects the single highest-priority failing rule for a field's submitted value, if any. */
@UtilityClass
public class FieldRuleEngine {

  public Optional<FieldRuleSpec> firstFailingRule(List<FieldRuleSpec> rules, String value) {
    return rules.stream()
        .sorted(Comparator.comparing(FieldRuleSpec::category))
        .filter(rule -> rule.isInvalid().test(value))
        .findFirst();
  }
}
