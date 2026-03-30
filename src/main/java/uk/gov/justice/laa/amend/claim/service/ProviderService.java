package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ProviderApiClient;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderApiClient providerApiClient;

    /**
     * Fetches provider firm office from Provider API
     *
     * @param officeCode the office code
     * @return firm office from API or null as fallback
     */
    public ProviderFirmOfficeDto getProviderFirm(String officeCode) {
        try {
            return providerApiClient.getProviderOffice(officeCode).block();
        } catch (Exception e) {
            log.info("Failed to fetch provider firm for office code: {}. Error: {}", officeCode, e.getMessage());
            log.debug("Provider API failure details for office code: {}", officeCode, e);
        }
        return null;
    }
}
