package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import java.util.List;
import java.util.function.BiPredicate;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

public record FieldRuleSpec(
    RuleCategory category,
    String messageCode,
    BiPredicate<ClaimDetails, String> isInvalid,
    List<Object> messageArgs,
    List<String> areasOfLaw) {

  public FieldRuleSpec(
      RuleCategory category, String messageCode, BiPredicate<ClaimDetails, String> isInvalid) {
    this(category, messageCode, isInvalid, List.of(), List.of());
  }
}
