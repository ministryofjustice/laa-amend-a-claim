package uk.gov.justice.laa.amend.claim.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;


@HttpExchange("/api/v1")
public interface ProviderApiClient {

    @GetExchange(url = "/provider-offices/{officeCode}", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<ProviderFirmOfficeDto> getProviderOffice(@PathVariable String officeCode);
}
