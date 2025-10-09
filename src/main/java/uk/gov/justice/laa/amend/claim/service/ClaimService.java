package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;


@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimsApiClient claimsApiClient;
    private final ClaimResultMapper claimResultMapper;

    public SearchResultViewModel searchClaims(String officeCode, int page, int size) {
        try {
            var result = claimsApiClient.searchClaims("0P322F",
                            null,
                            null,
                            page,
                            size);
            return claimResultMapper.toDto(result);
        } catch (Exception e) {
            log.error("Error searching claims", e);
            throw new RuntimeException(e);
        }
    }
}
