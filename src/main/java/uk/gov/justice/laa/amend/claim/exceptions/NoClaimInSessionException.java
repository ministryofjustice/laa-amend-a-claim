package uk.gov.justice.laa.amend.claim.exceptions;

import java.util.UUID;
import lombok.Getter;

@Getter
public class NoClaimInSessionException extends RuntimeException {

  private final UUID submissionId;
  private final UUID claimId;

  public NoClaimInSessionException(UUID submissionId, UUID claimId, String message) {
    super(message);
    this.submissionId = submissionId;
    this.claimId = claimId;
  }
}
