package uk.gov.justice.laa.amend.claim.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.exceptions.ClaimNotFoundException;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Sort;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSetV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimsApiClient claimsApiClient;
    private final ClaimMapper claimMapper;
    private final ProviderService providerService;

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
        var provider = providerService.getProviderFirm(claimDetails.getProviderAccountNumber());
        if (provider != null && provider.getFirm() != null) {
            claimMapper.enrichWithProviderName(claimDetails, provider.getFirm().getFirmName());
        }
        return claimDetails;
    }
}
