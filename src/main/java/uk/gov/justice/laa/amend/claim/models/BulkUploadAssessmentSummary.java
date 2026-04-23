package uk.gov.justice.laa.amend.claim.models;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record BulkUploadAssessmentSummary(
    UUID submissionId,
    UUID claimId,
    String uniqueFileNumber,
    String officeCode,
    OutcomeType assessmentOutcome,
    BigDecimal allowedTotalInclVat)
    implements Serializable {

  @Serial private static final long serialVersionUID = 1L;
}
