package uk.gov.justice.laa.payments.amend.exceptions;

import uk.gov.justice.laa.payments.amend.config.features.Feature;

public class FeatureNotImplementedRuntimeException extends RuntimeException {

  public FeatureNotImplementedRuntimeException(Feature feature) {
    super("Feature has not been implemented: %s".formatted(feature));
  }
}
