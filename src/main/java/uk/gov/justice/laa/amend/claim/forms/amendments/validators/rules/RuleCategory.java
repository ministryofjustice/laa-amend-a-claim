package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

/**
 * Global priority order for curated field rules, per
 * docs/adr/0006-per-field-amendment-validation-curation-spec.md. Declaration order is priority
 * order (lowest ordinal wins); required/blank is deliberately excluded, as requiredness stays owned
 * by the existing generic {@code FieldType} validators.
 */
public enum RuleCategory {
  PARSEABILITY,
  FORMAT,
  LENGTH,
  NUMERIC_RANGE,
  MEMBERSHIP
}
