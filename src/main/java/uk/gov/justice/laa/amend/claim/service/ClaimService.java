package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.client.config.SearchProperties;
import uk.gov.justice.laa.amend.claim.exceptions.ClaimNotFoundException;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
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
    private final ClaimMapper claimMapper;


    public ClaimResultSet searchClaims(
        String officeCode,
        Optional<String> uniqueFileNumber,
        Optional<String> caseReferenceNumber,
        Optional<String> submissionPeriod,
        int page,
        int size,
        String sort
    ) {
        try {
            return claimsApiClient.searchClaims(
                officeCode.toUpperCase(),
                uniqueFileNumber.orElse(null),
                caseReferenceNumber.orElse(null),
                submissionPeriod.orElse(null),
                page - 1,
                size,
                searchProperties.isSortEnabled() ? sort : null
            ).block();
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

    public ClaimDetails getClaimDetails(String submissionId, String claimId) {
        var claimResponse = getClaim(submissionId, claimId);
        var submissionResponse = getSubmission(submissionId);
        if (claimResponse == null || submissionResponse == null) {
            log.error("Claim or submission not found for submission {} and claim {}", submissionId, claimId);
            throw new ClaimNotFoundException(
                String.format("Claim with ID %s not found for submission %s", claimId, submissionId)
            );
        }
        return claimMapper.mapToClaimDetails(claimResponse, submissionResponse);
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
