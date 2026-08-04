package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import java.util.List;
import java.util.function.Predicate;

/**
 * A single curated local validation rule for an amendable field: which {@link RuleCategory} it
 * belongs to (for priority ordering), the message code to raise on failure, the failure predicate,
 * and any message arguments beyond the field label (which is always prepended by the evaluating
 * validator).
 */
public record FieldRuleSpec(
    RuleCategory category,
    String messageCode,
    Predicate<String> isInvalid,
    List<Object> messageArgs) {

  public FieldRuleSpec(RuleCategory category, String messageCode, Predicate<String> isInvalid) {
    this(category, messageCode, isInvalid, List.of());
  }
}
