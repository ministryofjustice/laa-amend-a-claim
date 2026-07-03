package uk.gov.justice.laa.amend.claim.factories;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.client.FeeSchemePlatformApiClient;
import uk.gov.justice.laa.amend.claim.exceptions.FeeCodeNotFoundException;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.fsp.FeeCode;
import uk.gov.justice.laa.amend.claim.models.fsp.FeeCodes;

@Component
@RequiredArgsConstructor
public class AvailableFeeCodesService {

  private final FeeSchemePlatformApiClient feeSchemePlatformApiClient;

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
        .blockOptional()
        .orElseThrow(() -> new FeeCodeNotFoundException(areaOfLaw));
  }
}
