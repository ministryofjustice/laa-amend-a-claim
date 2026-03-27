package uk.gov.justice.laa.amend.claim.models;

import java.util.List;

public record BulkUploadValidationOutcome(BulkUploadResult result, List<ClaimDetails> claimDetailsList) {}
