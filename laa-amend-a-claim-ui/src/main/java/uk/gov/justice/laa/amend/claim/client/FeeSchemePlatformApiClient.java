package uk.gov.justice.laa.amend.claim.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.models.fsp.FeeCodes;

@HttpExchange("/api")
public interface FeeSchemePlatformApiClient {

  @GetExchange("/v1/fee-codes/{areaOfLaw}")
  Mono<FeeCodes> getFeeCodes(@PathVariable String areaOfLaw);
}
