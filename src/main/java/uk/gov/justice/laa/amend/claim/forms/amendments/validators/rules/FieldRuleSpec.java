package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import java.util.List;
import java.util.function.Predicate;

public record FieldRuleSpec(
    RuleCategory category,
    String messageCode,
    Predicate<String> isInvalid,
    List<Object> messageArgs) {

  public FieldRuleSpec(RuleCategory category, String messageCode, Predicate<String> isInvalid) {
    this(category, messageCode, isInvalid, List.of());
  }
}
