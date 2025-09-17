package uk.gov.justice.laa.amendaclaim.exception;

/** Exception thrown when a virus check fails. */
public class VirusCheckException extends RuntimeException {

  public VirusCheckException(String message) {
    super(message);
  }
}
