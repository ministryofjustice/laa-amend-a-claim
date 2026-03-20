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
     * @param officeAccountNumber the office account number
     * @return firm office from API or null as fallback
     */
    public ProviderFirmOfficeDto getProviderFirm(String officeAccountNumber) {
        try {
            return providerApiClient.getProviderOffice(officeAccountNumber).block();
        } catch (Exception e) {
            log.info("Failed to fetch provider firm for office account: {}", officeAccountNumber, e);
        }
        return null;
    }
}
