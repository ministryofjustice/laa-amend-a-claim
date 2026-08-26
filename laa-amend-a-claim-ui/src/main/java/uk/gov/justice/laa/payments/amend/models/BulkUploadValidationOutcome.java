package uk.gov.justice.laa.payments.amend.models;

import java.util.List;

public record BulkUploadValidationOutcome(
    BulkUploadResult result, List<ClaimDetails> claimDetailsList) {}
