package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules.model;

import java.util.List;
import java.util.Map;

public record RulesRoot(Map<String, List<RuleDto>> ruleGroups, Map<String, FieldConfig> fields) {}
