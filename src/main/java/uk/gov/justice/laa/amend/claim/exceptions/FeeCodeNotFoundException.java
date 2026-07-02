package uk.gov.justice.laa.amend.claim.exceptions;

public class FeeCodeNotFoundException extends RuntimeException {
  public FeeCodeNotFoundException(final String feeCode) {
    super("Fee code not found: %s ".formatted(feeCode));
  }
}
