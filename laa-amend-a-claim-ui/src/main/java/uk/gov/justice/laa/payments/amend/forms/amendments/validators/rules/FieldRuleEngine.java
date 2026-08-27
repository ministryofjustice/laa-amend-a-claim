package uk.gov.justice.laa.payments.amend.forms.amendments.validators.rules;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

/** Selects the single highest-priority failing rule for a field's submitted value, if any. */
@UtilityClass
public class FieldRuleEngine {

  public Optional<FieldRuleSpec> firstFailingRule(List<FieldRuleSpec> rules, String value) {
    return rules.stream()
        .filter(rule -> rule.isInvalid().test(value))
        .min(Comparator.comparing(FieldRuleSpec::category));
  }
}
