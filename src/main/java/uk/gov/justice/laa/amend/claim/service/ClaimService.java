package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;


@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimsApiClient claimsApiClient;

    public ClaimResultSet searchClaims(String officeCode, int page, int size) {
        try {
            return claimsApiClient.searchClaims("0P322F",
                            null,
                            null,
                            page - 1,
                            size);
        } catch (Exception e) {
            log.error("Error searching claims", e);
            throw new RuntimeException(e);
        }
    }
}
