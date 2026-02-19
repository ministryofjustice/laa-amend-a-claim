package uk.gov.justice.laa.amend.claim.service;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.client.ProviderApiClient;
import uk.gov.justice.laa.amend.claim.exceptions.ClaimNotFoundException;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Sort;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSetV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimsApiClient claimsApiClient;
    private final ClaimMapper claimMapper;
    private final ProviderApiClient providerApiClient;

    public ClaimResultSetV2 searchClaims(
            String officeCode,
            Optional<String> uniqueFileNumber,
            Optional<String> caseReferenceNumber,
            Optional<String> submissionPeriod,
            Optional<AreaOfLaw> areaOfLaw,
            Optional<Boolean> escapeCase,
            int page,
            int size,
            Sort sort) {
        try {
            return claimsApiClient
                    .searchClaims(
                            officeCode.toUpperCase(),
                            uniqueFileNumber.orElse(null),
                            caseReferenceNumber.orElse(null),
                            submissionPeriod.orElse(null),
                            areaOfLaw.orElse(null),
                            escapeCase.orElse(null),
                            page - 1,
                            size,
                            Objects.toString(sort, null))
                    .block();
        } catch (Exception e) {
            log.error("Error searching claims", e);
            throw new RuntimeException(e);
        }
    }

    public ClaimResponseV2 getClaim(String submissionId, String claimId) {
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
                    String.format("Claim with ID %s not found for submission %s", claimId, submissionId));
        }
        var officeCode = submissionResponse.getOfficeAccountNumber();
        var claimDetails = claimMapper.mapToClaimDetails(claimResponse, submissionResponse);
        claimMapper.enrichWithProviderName(claimDetails, getProviderFirmName(officeCode));
        return claimDetails;
    }

    public SubmissionResponse getSubmission(String submissionId) {
        try {
            return claimsApiClient.getSubmission(submissionId).block();
        } catch (Exception e) {
            log.error("Error getting submission {}", submissionId, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches provider firm name from Provider API
     *
     * @param officeAccountNumber the office account number
     * @return firm name from API or office account number as fallback
     */
    private String getProviderFirmName(String officeAccountNumber) {
        try {
            ProviderFirmOfficeDto providerOffice =
                    providerApiClient.getProviderOffice(officeAccountNumber).block();

            if (providerOffice != null && providerOffice.getFirm() != null) {
                return providerOffice.getFirm().getFirmName();
            }
        } catch (Exception e) {
            log.warn(
                    "Failed to fetch provider firm name for office account: {}. Error: {}",
                    officeAccountNumber,
                    e.getMessage());
        }
        return null;
    }
}
