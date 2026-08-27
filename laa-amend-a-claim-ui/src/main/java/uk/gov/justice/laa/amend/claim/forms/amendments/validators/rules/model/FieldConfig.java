package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules.model;

import java.util.List;

public record FieldConfig(List<String> ruleGroups, List<RuleDto> rules) {}
