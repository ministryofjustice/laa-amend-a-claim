package uk.gov.justice.laa.payments.amend.exceptions;

public class ClaimNotFoundException extends RuntimeException {
  public ClaimNotFoundException(final String message) {
    super(message);
  }
}
