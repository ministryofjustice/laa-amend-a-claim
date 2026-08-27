package uk.gov.justice.laa.payments.amend.forms.amendments.validators.rules.model;

import java.util.List;

public record RuleDto(
    String category,
    String kind,
    String messageCode,
    List<String> messageArgs,
    String pattern,
    List<String> flags,
    String min,
    String max,
    Integer length) {}
