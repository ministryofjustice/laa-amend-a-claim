package uk.gov.justice.laa.amend.claim.config;

import static java.lang.Boolean.TRUE;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.exceptions.FeatureNotEnabledException;

@Data
@Configuration
@ConfigurationProperties(prefix = "feature-flags")
public class FeatureFlagsConfig {
  private Boolean isBulkUploadEnabled;
  private Boolean isRequestedAndCalculatedSwapEnabled;
  private Boolean isFullClaimDetailsEnabled;
  private Boolean isClaimAmendmentEnabled;

  private void checkBulkUploadEnabled() {
    if (!TRUE.equals(isBulkUploadEnabled)) {
      throw new FeatureNotEnabledException("isBulkUploadEnabled is false");
    }
  }

  private void checkFullClaimDetailsEnabled() {
    if (!TRUE.equals(isFullClaimDetailsEnabled)) {
      throw new FeatureNotEnabledException("isFullClaimDetailsEnabled is false");
    }
  }

  public void checkClaimAmendmentEnabled() {
    if (!TRUE.equals(isClaimAmendmentEnabled)) {
      throw new FeatureNotEnabledException("isClaimAmendmentEnabled is false");
    }
  }

  public void checkEnabled(Feature[] features) {
    for (var feature : features) {
      switch (feature) {
        case BULK_UPLOAD -> checkBulkUploadEnabled();
        case FULL_CLAIM_DETAILS -> checkFullClaimDetailsEnabled();
        case CLAIM_AMENDMENT -> checkClaimAmendmentEnabled();
        default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Feature not found");
      }
    }
  }
}
