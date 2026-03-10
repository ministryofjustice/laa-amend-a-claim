package uk.gov.justice.laa.amend.claim.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ProviderApiClient;
import uk.gov.justice.laa.amend.claim.client.config.ProviderApiProperties;
import uk.gov.justice.laa.amend.claim.client.config.TimeProperties;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;

@ExtendWith(MockitoExtension.class)
class ProviderServiceTest {

    @Mock
    private ProviderApiClient providerApiClient;

    @Mock
    private ProviderApiProperties providerApiProperties;

    @Test
    void testGetProviderOfficeDuringInHours() {
        String officeCode = "0P322F";

        ProviderFirmOfficeDto providerFirm = mock(ProviderFirmOfficeDto.class, RETURNS_DEEP_STUBS);

        when(providerApiProperties.getStart()).thenReturn(new TimeProperties(7, 0));
        when(providerApiProperties.getEnd()).thenReturn(new TimeProperties(21, 30));
        when(providerApiClient.getProviderOffice(officeCode)).thenReturn(Mono.just(providerFirm));

        Clock clock = Clock.fixed(Instant.parse("2026-03-10T12:00:00Z"), ZoneId.systemDefault());

        ProviderService providerService = new ProviderService(providerApiClient, providerApiProperties, clock);

        var result = providerService.getProviderFirm(officeCode);

        assertNotNull(result);
        verify(providerApiClient, times(1)).getProviderOffice(officeCode);
    }

    @Test
    void testGetProviderOfficeDuringOutOfHours() {
        String officeCode = "0P322F";

        when(providerApiProperties.getStart()).thenReturn(new TimeProperties(7, 0));
        when(providerApiProperties.getEnd()).thenReturn(new TimeProperties(21, 30));

        Clock clock = Clock.fixed(Instant.parse("2026-03-10T06:00:00Z"), ZoneId.systemDefault());

        ProviderService providerService = new ProviderService(providerApiClient, providerApiProperties, clock);

        var result = providerService.getProviderFirm(officeCode);

        assertNull(result);
        verifyNoInteractions(providerApiClient);
    }

    @Test
    void testGetProviderOfficeWhenClientThrowsException() {
        String officeCode = "0P322F";

        when(providerApiProperties.getStart()).thenReturn(new TimeProperties(7, 0));
        when(providerApiProperties.getEnd()).thenReturn(new TimeProperties(21, 30));
        when(providerApiClient.getProviderOffice(officeCode)).thenReturn(Mono.error(new RuntimeException("error")));

        ProviderService providerService =
                new ProviderService(providerApiClient, providerApiProperties, Clock.systemUTC());

        var result = providerService.getProviderFirm(officeCode);

        assertNull(result);
        verify(providerApiClient, times(1)).getProviderOffice(officeCode);
    }
}
