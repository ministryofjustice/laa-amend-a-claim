package uk.gov.justice.laa.amend.claim.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

@HttpExchange("/api/v0")
public interface ClaimsApiClient {

    @GetExchange(url = "/claims", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<ClaimResultSet> searchClaims(
            @RequestParam(value = "office_code") String officeCode,
            @RequestParam(value = "submission_id", required = false, defaultValue = "") String submissionId,
            @RequestParam(value = "unique_file_number", required = false, defaultValue = "") String uniqueFileNumber,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size
            );
}