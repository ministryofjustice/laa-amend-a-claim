package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules.ClaimFieldRuleJsonLoader;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules.FieldRuleEngine;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules.RuleCategory;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

// Makes this validator run first before all others
@Order(0)
@Component
public class ClaimFieldRuleValidator implements FieldSpecificAmendmentValidator {

  private final MessageSource messageSource;

  public ClaimFieldRuleValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return ClaimFieldRuleJsonLoader.hasRules(field);
  }

  @Override
  public void validate(
      ClaimDetails claimDetails, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var value = form.getInputs().get(field.name());
    var rules = ClaimFieldRuleJsonLoader.rulesFor(field);
    var rulesToEvaluate =
        isBlank(value)
            ? rules.stream().filter(rule -> rule.category() == RuleCategory.MANDATORY).toList()
            : rules;

    FieldRuleEngine.firstFailingRule(rulesToEvaluate, value, claimDetails)
        .ifPresent(
            rule -> {
              var args = new ArrayList<>();
              args.add(field.label(messageSource));
              args.addAll(rule.messageArgs());
              addUniqueFieldError(field, rule.messageCode(), args.toArray(), errors);
            });
  }
}
