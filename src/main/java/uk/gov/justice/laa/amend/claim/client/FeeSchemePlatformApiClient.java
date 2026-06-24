package uk.gov.justice.laa.amend.claim.client;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange("/api")
public interface FeeSchemePlatformApiClient {

  record FeeCode(String feeCode, String feeCodeDescription) {}

  record FeeCodes(List<FeeCode> feeCodes) {}

  @GetExchange("/v1/fee-codes/{areaOfLaw}")
  Mono<FeeCodes> getFeeCodes(@PathVariable String areaOfLaw);
}
