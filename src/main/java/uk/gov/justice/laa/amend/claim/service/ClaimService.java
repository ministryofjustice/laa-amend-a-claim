package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimsApiClient claimsApiClient;

    public ClaimResultSet searchClaims(String officeCode,
                                       Optional<String> uniqueFileNumber,
                                       Optional<String> caseReferenceNumber,
                                       int page,
                                       int size) {
        try {
            return claimsApiClient.searchClaims(officeCode,
                            uniqueFileNumber.orElse(null),
                            caseReferenceNumber.orElse(null),
                            page - 1,
                            size).block();
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
}
