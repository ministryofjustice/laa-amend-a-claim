package uk.gov.justice.laa.amend.claim.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

@HttpExchange("/api/v0")
public interface ClaimsApiClient {

    @GetExchange(url = "/claims", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<ClaimResultSet> searchClaims(
            @RequestParam(value = "office_code") String officeCode,
            @RequestParam(value = "unique_file_number", required = false, defaultValue = "") String uniqueFileNumber,
            @RequestParam(value = "case_reference_number", required = false, defaultValue = "") String caseReferenceNumber,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);


    @GetExchange(url = "/submissions/{submissionId}/claims/{claimId}", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<ClaimResponse> getClaim(
            @PathVariable String submissionId,
            @PathVariable String claimId);
}
