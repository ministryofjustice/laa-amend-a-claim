package uk.gov.justice.laa.payments.amend.models;

import java.util.Set;

public record AmendmentConfirmation(Boolean hasCalculatedCostsChanged, Set<String> amendedFields) {}
