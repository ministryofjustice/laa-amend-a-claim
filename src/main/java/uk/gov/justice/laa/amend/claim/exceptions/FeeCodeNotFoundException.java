package uk.gov.justice.laa.amend.claim.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FeeCodeNotFoundException extends RuntimeException {
  public FeeCodeNotFoundException(final String feeCode) {
    super("Fee code not found: %s".formatted(feeCode));
  }

  public FeeCodeNotFoundException(AreaOfLaw areaOfLaw) {
    super("Fee codes not found for area of law: %s".formatted(areaOfLaw));
  }
}
