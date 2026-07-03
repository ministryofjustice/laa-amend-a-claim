package uk.gov.justice.laa.amend.claim.exceptions;

import uk.gov.justice.laa.amend.claim.config.features.Feature;

public class FeatureNotImplementedRuntimeException extends RuntimeException {

  public FeatureNotImplementedRuntimeException(Feature feature) {
    super("Feature has not been implemented: %s".formatted(feature));
  }
}
