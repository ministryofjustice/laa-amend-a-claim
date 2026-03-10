package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ProviderApiClient;
import uk.gov.justice.laa.amend.claim.client.config.ProviderApiProperties;
import uk.gov.justice.laa.amend.claim.client.config.TimeProperties;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderApiClient providerApiClient;
    private final ProviderApiProperties providerApiProperties;
    private final TimeService timeService;

    /**
     * Fetches provider firm name from Provider API
     *
     * @param officeAccountNumber the office account number
     * @return firm name from API or office account number as fallback
     */
    public ProviderFirmOfficeDto getProviderFirm(String officeAccountNumber) {
        TimeProperties start = providerApiProperties.getStart();
        TimeProperties end = providerApiProperties.getEnd();
        if (timeService.isInTimeRange(start.toLocalTime(), end.toLocalTime())) {
            try {
                return providerApiClient.getProviderOffice(officeAccountNumber).block();
            } catch (Exception e) {
                log.warn(
                        "Failed to fetch provider firm for office account: {}. Error: {}",
                        officeAccountNumber,
                        e.getMessage());
            }
        }
        return null;
    }
}
