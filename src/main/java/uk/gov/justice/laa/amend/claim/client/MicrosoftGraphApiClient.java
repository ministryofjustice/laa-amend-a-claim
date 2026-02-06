package uk.gov.justice.laa.amend.claim.client;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;

@HttpExchange("/v1.0")
public interface MicrosoftGraphApiClient {

    @GetExchange(url = "/users/{upn}", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<MicrosoftApiUser> getUser(@PathVariable String upn, @RequestHeader(AUTHORIZATION) String token);
}
