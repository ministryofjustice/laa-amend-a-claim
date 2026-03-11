package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Status;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ProviderApiClient;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderApiClient providerApiClient;

    /**
     * Fetches provider firm name from Provider API
     *
     * @param officeAccountNumber the office account number
     * @return firm name from API or office account number as fallback
     */
    public ProviderFirmOfficeDto getProviderFirm(String officeAccountNumber) {
        if (isApiUp()) {
            try {
                return providerApiClient.getProviderOffice(officeAccountNumber).block();
            } catch (Exception e) {
                log.warn("Failed to fetch provider firm for office account: {}", officeAccountNumber, e);
            }
        }
        return null;
    }

    private boolean isApiUp() {
        try {
            var response = providerApiClient.ping().block();
            return response != null && response.getStatus().equals(Status.UP);
        } catch (Exception e) {
            log.info("Provider details API is out of hours.");
            return false;
        }
    }
}
