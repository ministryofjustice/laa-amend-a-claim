package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Status;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ProviderApiClient;
import uk.gov.justice.laa.amend.claim.models.HealthDto;
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
        if (isAvailable()) {
            try {
                return providerApiClient.getProviderOffice(officeAccountNumber).block();
            } catch (Exception e) {
                log.warn("Failed to fetch provider firm for office account: {}", officeAccountNumber, e);
            }
        }
        return null;
    }

    private boolean isAvailable() {
        boolean available;
        try {
            HealthDto response = providerApiClient.ping().block();
            available = response != null && response.status().equals(Status.UP);
        } catch (Exception e) {
            available = false;
        }
        if (!available) {
            log.info("Provider details API is unavailable");
        }
        return available;
    }
}
