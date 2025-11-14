package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.client.config.SearchProperties;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimsApiClient claimsApiClient;
    private final SearchProperties searchProperties;


    public ClaimResultSet searchClaims(
        String officeCode,
        Optional<String> uniqueFileNumber,
        Optional<String> caseReferenceNumber,
        int page,
        int size,
        String sort
    ) {
        try {
            if (searchProperties.isSortEnabled()) {
                return claimsApiClient.searchClaimsWithSort(
                        officeCode.toUpperCase(),
                        uniqueFileNumber.orElse(null),
                        caseReferenceNumber.orElse(null),
                        page - 1,
                        size,
                        sort
                ).block();
            } else {
                return claimsApiClient.searchClaimsWithSort(
                        officeCode.toUpperCase(),
                        uniqueFileNumber.orElse(null),
                        caseReferenceNumber.orElse(null),
                        page - 1,
                        size,
                        null
                ).block();
            }
        } catch (Exception e) {
            log.error("Error searching claims", e);
            throw new RuntimeException(e);
        }
    }

    public ClaimResponse getClaim(String submissionId, String claimId) {
        try {
            return claimsApiClient.getClaim(submissionId, claimId).block();
        } catch (Exception e) {
            log.error("Error getting claim {}", claimId, e);
            throw new RuntimeException(e);
        }
    }

    public SubmissionResponse getSubmission(String submissionId) {
        try {
            return claimsApiClient.getSubmission(submissionId).block();
        } catch (Exception e) {
            log.error("Error getting submission {}", submissionId, e);
            throw new RuntimeException(e);
        }
    }

}
