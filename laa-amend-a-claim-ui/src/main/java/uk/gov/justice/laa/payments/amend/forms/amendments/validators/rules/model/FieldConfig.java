package uk.gov.justice.laa.payments.amend.forms.amendments.validators.rules.model;

import java.util.List;

public record FieldConfig(String ruleGroup, List<RuleDto> rules) {}
