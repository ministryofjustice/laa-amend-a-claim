package uk.gov.justice.laa.amend.claim.models;

import java.util.Set;

public record AmendmentConfirmation(Boolean hasCalculatedCostsChanged, Set<String> amendedFields) {}
