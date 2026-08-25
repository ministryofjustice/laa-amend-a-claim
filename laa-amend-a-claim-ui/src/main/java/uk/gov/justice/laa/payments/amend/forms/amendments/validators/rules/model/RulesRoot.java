package uk.gov.justice.laa.payments.amend.forms.amendments.validators.rules.model;

import java.util.List;
import java.util.Map;

public record RulesRoot(Map<String, List<RuleDto>> ruleGroups, Map<String, FieldConfig> fields) {}
