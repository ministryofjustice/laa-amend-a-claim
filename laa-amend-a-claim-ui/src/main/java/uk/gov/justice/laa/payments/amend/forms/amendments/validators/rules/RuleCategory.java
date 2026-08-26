package uk.gov.justice.laa.payments.amend.forms.amendments.validators.rules;

/**
 * Helps with ordering the field validation rules in a priority order (e.g. show parse issues before
 * format issues)
 */
public enum RuleCategory {
  PARSEABILITY,
  FORMAT,
  LENGTH,
  NUMERIC_RANGE,
  MEMBERSHIP
}
