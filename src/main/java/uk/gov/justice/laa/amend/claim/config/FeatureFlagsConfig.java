package uk.gov.justice.laa.amend.claim.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Data
@Configuration
@ConfigurationProperties(prefix = "feature-flags")
public class FeatureFlagsConfig {
  private Boolean isBulkUploadEnabled;
  private Boolean isRequestedAndCalculatedSwapEnabled;
  private Boolean isFullClaimDetailsEnabled;
  private Boolean isClaimAmendmentEnabled;

  public void checkFullClaimDetailsEnabled() {
    if (!isFullClaimDetailsEnabled) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "isFullClaimDetailsEnabled is false");
    }
  }

  public void checkClaimAmendmentEnabled() {
    if (!isClaimAmendmentEnabled) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "isClaimAmendmentEnabled is false");
    }
  }
}
