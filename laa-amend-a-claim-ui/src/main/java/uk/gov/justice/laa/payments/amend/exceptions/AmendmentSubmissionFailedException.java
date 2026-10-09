package uk.gov.justice.laa.payments.amend.exceptions;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import uk.gov.justice.laa.payments.amend.models.AmendmentError;

@Getter
public class AmendmentSubmissionFailedException extends RuntimeException {

  private final UUID submissionId;
  private final UUID claimId;
  private final List<AmendmentError> errors;

  public AmendmentSubmissionFailedException(
      UUID submissionId, UUID claimId, List<AmendmentError> errors) {
    super("Amendment submission failed with %d error(s)".formatted(errors.size()));
    this.submissionId = submissionId;
    this.claimId = claimId;
    this.errors = errors;
  }
}
