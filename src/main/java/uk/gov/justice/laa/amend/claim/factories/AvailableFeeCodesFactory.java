package uk.gov.justice.laa.amend.claim.factories;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.client.FeeSchemePlatformApiClient;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.fsp.FeeCode;
import uk.gov.justice.laa.amend.claim.models.fsp.FeeCodes;

@Component
public class AvailableFeeCodesFactory {

  private final FeeSchemePlatformApiClient feeSchemePlatformApiClient;

  public AvailableFeeCodesFactory(FeeSchemePlatformApiClient feeSchemePlatformApiClient) {
    this.feeSchemePlatformApiClient = feeSchemePlatformApiClient;
  }

  public Map<String, String> getAvailableFeeCodes(AreaOfLaw areaOfLaw) {
    // FSP expects either "LEGAL_HELP", "CRIME_LOWER" or "MEDIATION" as strings so using enum
    // name, and is case insensitive.
    return feeSchemePlatformApiClient
        .getFeeCodes(areaOfLaw.name())
        .map(FeeCodes::feeCodes)
        .map(
            feeCodes ->
                feeCodes.stream()
                    .collect(Collectors.toMap(FeeCode::feeCode, FeeCode::fullFeeCodeDescription)))
        .block();
  }
}
