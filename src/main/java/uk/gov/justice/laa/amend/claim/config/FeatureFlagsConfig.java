package uk.gov.justice.laa.amend.claim.config;

import static java.lang.Boolean.TRUE;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.exceptions.FeatureNotEnabledException;
import uk.gov.justice.laa.amend.claim.exceptions.FeatureNotImplementedRuntimeException;

@Data
@Configuration
@ConfigurationProperties(prefix = "feature-flags")
public class FeatureFlagsConfig {

  @NotNull private Boolean isBulkUploadEnabled;
  @NotNull private Boolean isClaimAmendmentEnabled;

  private void checkBulkUploadEnabled() {
    if (!TRUE.equals(isBulkUploadEnabled)) {
      throw new FeatureNotEnabledException("isBulkUploadEnabled is false");
    }
  }

  private void checkClaimAmendmentEnabled() {
    if (!TRUE.equals(isClaimAmendmentEnabled)) {
      throw new FeatureNotEnabledException("isClaimAmendmentEnabled is false");
    }
  }

  public void checkEnabled(Feature... features) {
    for (var feature : features) {
      switch (feature) {
        case BULK_UPLOAD -> checkBulkUploadEnabled();
        case CLAIM_AMENDMENT -> checkClaimAmendmentEnabled();
        default -> throw new FeatureNotImplementedRuntimeException(feature);
      }
    }
  }
}
