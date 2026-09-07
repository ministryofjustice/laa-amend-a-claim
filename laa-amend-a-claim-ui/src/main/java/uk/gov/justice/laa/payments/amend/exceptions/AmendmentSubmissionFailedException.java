package uk.gov.justice.laa.payments.amend.exceptions;

import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class AmendmentSubmissionFailedException extends RuntimeException {

  private final UUID submissionId;
  private final UUID claimId;
  private final List<String> errorMessages;

  public AmendmentSubmissionFailedException(
      UUID submissionId, UUID claimId, List<String> errorMessages) {
    super("Amendment submission failed with %d error(s)".formatted(errorMessages.size()));
    this.submissionId = submissionId;
    this.claimId = claimId;
    this.errorMessages = errorMessages;
  }
}
