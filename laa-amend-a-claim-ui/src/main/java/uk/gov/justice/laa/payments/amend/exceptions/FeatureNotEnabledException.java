package uk.gov.justice.laa.payments.amend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeatureNotEnabledException extends ResponseStatusException {

  public FeatureNotEnabledException(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}
