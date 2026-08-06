package uk.gov.justice.laa.amend.claim.models;

import java.math.BigDecimal;
import java.util.UUID;
import uk.gov.justice.laa.amend.claim.models.enums.OutcomeType;

public record BulkUploadAssessmentSummary(
    UUID submissionId,
    UUID claimId,
    String uniqueFileNumber,
    String officeCode,
    OutcomeType assessmentOutcome,
    BigDecimal allowedTotalInclVat) {}
