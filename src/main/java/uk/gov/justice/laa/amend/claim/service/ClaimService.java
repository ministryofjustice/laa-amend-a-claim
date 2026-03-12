package uk.gov.justice.laa.amend.claim.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.VoidClaim201Response;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.VoidClaimRequest;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimService {

    private static final String VOID_ASSESSMENT_REASON = "Void assessment";

    private final ClaimsApiClient claimsApiClient;
    private final ClaimMapper claimMapper;
    private final ProviderApiClient providerApiClient;

    private static final List<ClaimStatus> claimStatuses = List.of(ClaimStatus.VALID, ClaimStatus.VOID);

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
                            Objects.toString(sort, null),
                            claimStatuses)
                    .block();
        } catch (Exception e) {
            log.error("Error searching claims", e);
            throw new RuntimeException(e);
        }
    }

    public ClaimResponseV2 getClaim(UUID submissionId, UUID claimId) {
        try {
            return claimsApiClient.getClaim(submissionId, claimId).block();
        } catch (Exception e) {
            log.error("Error getting claim {}", claimId, e);
            throw new RuntimeException(e);
        }
    }

    public ClaimDetails getClaimDetails(UUID submissionId, UUID claimId) {
        var claimResponse = getClaim(submissionId, claimId);
        if (claimResponse == null) {
            log.error("Claim not found for submission {} and claim {}", submissionId, claimId);
            throw new ClaimNotFoundException(
                    String.format("Claim with ID %s not found for submission %s", claimId, submissionId));
        }
        var claimDetails = claimMapper.mapToClaimDetails(claimResponse);
        claimMapper.enrichWithProviderName(claimDetails, getProviderFirmName(claimDetails.getProviderAccountNumber()));
        return claimDetails;
    }

    public VoidClaim201Response voidClaim(UUID claimId, UUID userId) {
        try {
            var request = new VoidClaimRequest(userId, VOID_ASSESSMENT_REASON);
            return claimsApiClient.voidClaim(claimId, request).block();
        } catch (Exception e) {
            log.error("Error voiding claim {}", claimId, e);
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
