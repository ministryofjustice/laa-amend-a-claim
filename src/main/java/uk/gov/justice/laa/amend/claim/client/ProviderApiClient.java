package uk.gov.justice.laa.amend.claim.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.models.HealthDto;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;

@HttpExchange()
public interface ProviderApiClient {

    @GetExchange(url = "/actuator/health", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<HealthDto> ping();

    @GetExchange(url = "/api/v1/provider-offices/{officeCode}", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<ProviderFirmOfficeDto> getProviderOffice(@PathVariable String officeCode);
}
