package uk.gov.justice.laa.amend.claim.client;

import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSetV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;

@HttpExchange("/api")
public interface ClaimsApiClient {

    @GetExchange(url = "/v2/claims", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<ClaimResultSetV2> searchClaims(
            @RequestParam(value = "office_code") String officeCode,
            @RequestParam(value = "unique_file_number", required = false, defaultValue = "") String uniqueFileNumber,
            @RequestParam(value = "case_reference_number", required = false, defaultValue = "")
                    String caseReferenceNumber,
            @RequestParam(value = "submission_period", required = false, defaultValue = "") String submissionPeriod,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "sort", required = false) String sort);

    // TODO: I've asked Jose to make a v2 version so we can correctly use ClaimResponseV2 as return type
    @GetExchange(url = "/v1/submissions/{submissionId}/claims/{claimId}", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<ClaimResponseV2> getClaim(@PathVariable String submissionId, @PathVariable String claimId);

    @GetExchange(url = "/v1/submissions/{id}", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<SubmissionResponse> getSubmission(@PathVariable String id);

    @PostExchange(value = "/v1/claims/{claimId}/assessments", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<CreateAssessment201Response>> submitAssessment(
            @PathVariable String claimId, @RequestBody AssessmentPost body);

    @GetExchange(url = "/v1/claims/{claimId}/assessments", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<AssessmentResultSet> getAssessments(
            @PathVariable UUID claimId,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "sort", required = false) String sort);
}
