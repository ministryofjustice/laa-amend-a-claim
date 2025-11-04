package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.repositories.CacheRepository;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheRepository cacheRepository;

    public Claim getClaim(String claimId) {
        return cacheRepository.get(claimId, Claim.class);
    }

    public void setClaim(Claim claim) {
        cacheRepository.set(claim.getClaimId(), claim);
    }

    public void deleteClaim(String claimId) {
        cacheRepository.delete(claimId);
    }
}
