package uk.gov.justice.laa.amend.claim.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Status;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ProviderApiClient;
import uk.gov.justice.laa.amend.claim.models.HealthDto;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;

@ExtendWith(MockitoExtension.class)
class ProviderServiceTest {

    @Mock
    private ProviderApiClient providerApiClient;

    @InjectMocks
    private ProviderService providerService;

    @Test
    void testGetProviderFirmWhenDuringInHours() {
        String officeCode = "0P322F";

        HealthDto health = new HealthDto();
        health.setStatus(Status.UP);

        ProviderFirmOfficeDto providerFirm = mock(ProviderFirmOfficeDto.class, RETURNS_DEEP_STUBS);

        when(providerApiClient.ping()).thenReturn(Mono.just(health));
        when(providerApiClient.getProviderOffice(officeCode)).thenReturn(Mono.just(providerFirm));

        var result = providerService.getProviderFirm(officeCode);

        assertNotNull(result);
        verify(providerApiClient, times(1)).ping();
        verify(providerApiClient, times(1)).getProviderOffice(officeCode);
    }

    @Test
    void testGetProviderFirmWhenOutOfHours() {
        String officeCode = "0P322F";

        HealthDto health = new HealthDto();
        health.setStatus(Status.DOWN);

        when(providerApiClient.ping()).thenReturn(Mono.just(health));

        var result = providerService.getProviderFirm(officeCode);

        assertNull(result);
        verify(providerApiClient, times(1)).ping();
        verify(providerApiClient, never()).getProviderOffice(officeCode);
    }

    @Test
    void testGetProviderFirmWhenGetHealthThrowsException() {
        String officeCode = "0P322F";

        when(providerApiClient.ping()).thenReturn(Mono.error(new Exception("")));

        var result = providerService.getProviderFirm(officeCode);

        assertNull(result);
        verify(providerApiClient, times(1)).ping();
        verify(providerApiClient, never()).getProviderOffice(officeCode);
    }

    @Test
    void testGetProviderFirmWhenGetProviderOfficeThrowsException() {
        String officeCode = "0P322F";

        HealthDto health = new HealthDto();
        health.setStatus(Status.UP);

        when(providerApiClient.ping()).thenReturn(Mono.just(health));
        when(providerApiClient.getProviderOffice(officeCode)).thenReturn(Mono.error(new RuntimeException("error")));

        var result = providerService.getProviderFirm(officeCode);

        assertNull(result);
        verify(providerApiClient, times(1)).ping();
        verify(providerApiClient, times(1)).getProviderOffice(officeCode);
    }
}
