package uk.gov.justice.laa.amend.claim.factories;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.client.FeeSchemePlatformApiClient;

@Component
public class AvailableFeeCodesFactory {

  private final FeeSchemePlatformApiClient feeSchemePlatformApiClient;

  public AvailableFeeCodesFactory(FeeSchemePlatformApiClient feeSchemePlatformApiClient) {
    this.feeSchemePlatformApiClient = feeSchemePlatformApiClient;
  }
}
