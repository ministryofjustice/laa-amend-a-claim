package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class FieldRuleEngineTest {

  private static final FieldRuleSpec FORMAT_RULE =
      new FieldRuleSpec(RuleCategory.FORMAT, "format.invalid", value -> value.contains("!"));
  private static final FieldRuleSpec LENGTH_RULE =
      new FieldRuleSpec(RuleCategory.LENGTH, "length.invalid", value -> value.length() > 5);
  private static final FieldRuleSpec MEMBERSHIP_RULE =
      new FieldRuleSpec(
          RuleCategory.MEMBERSHIP, "membership.invalid", value -> !value.equals("ALLOWED"));

  @Test
  void returnsEmptyWhenNoRuleFails() {
    var result = FieldRuleEngine.firstFailingRule(List.of(FORMAT_RULE, LENGTH_RULE), "ok");

    assertThat(result).isEmpty();
  }

  @Test
  void returnsTheOnlyFailingRule() {
    var result = FieldRuleEngine.firstFailingRule(List.of(FORMAT_RULE, LENGTH_RULE), "toolong");

    assertThat(result).contains(LENGTH_RULE);
  }

  @Test
  void surfacesOnlyTheHigherPriorityRuleWhenSeveralRulesFail() {
    // Both FORMAT and LENGTH fail here ("toolong!" contains "!" and is over 5 chars), and rules
    // are supplied out of priority order - the evaluator must still pick FORMAT (lower ordinal).
    var result = FieldRuleEngine.firstFailingRule(List.of(LENGTH_RULE, FORMAT_RULE), "toolong!");

    assertThat(result).contains(FORMAT_RULE);
  }

  @Test
  void priorityOrderIsFormatThenLengthThenMembership() {
    var allFailing = List.of(MEMBERSHIP_RULE, LENGTH_RULE, FORMAT_RULE);

    assertThat(FieldRuleEngine.firstFailingRule(allFailing, "toolong!")).contains(FORMAT_RULE);
    assertThat(FieldRuleEngine.firstFailingRule(List.of(MEMBERSHIP_RULE, LENGTH_RULE), "toolong"))
        .contains(LENGTH_RULE);
    assertThat(FieldRuleEngine.firstFailingRule(List.of(MEMBERSHIP_RULE), "ok"))
        .contains(MEMBERSHIP_RULE);
  }
}
