package uk.gov.justice.laa.amend.claim.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @Mock
    private TimeService timeService;

    @InjectMocks
    private ProviderService providerService;

    @Test
    void testGetProviderOfficeDuringInHours() {
        // Arrange
        String officeCode = "0P322F";

        ProviderFirmOfficeDto providerFirm = mock(ProviderFirmOfficeDto.class, RETURNS_DEEP_STUBS);

        when(providerApiProperties.getStart()).thenReturn(new TimeProperties(7, 0));
        when(providerApiProperties.getEnd()).thenReturn(new TimeProperties(21, 30));
        when(timeService.isInTimeRange(any(), any())).thenReturn(true);
        when(providerApiClient.getProviderOffice(officeCode)).thenReturn(Mono.just(providerFirm));

        // Act
        var result = providerService.getProviderFirm(officeCode);

        // Assert
        assertNotNull(result);
        verify(timeService).isInTimeRange(LocalTime.of(7, 0), LocalTime.of(21, 30));
        verify(providerApiClient, times(1)).getProviderOffice(officeCode);
    }

    @Test
    void testGetProviderOfficeDuringOutOfHours() {
        // Arrange
        String officeCode = "0P322F";

        when(providerApiProperties.getStart()).thenReturn(new TimeProperties(7, 0));
        when(providerApiProperties.getEnd()).thenReturn(new TimeProperties(21, 30));
        when(timeService.isInTimeRange(any(), any())).thenReturn(false);

        // Act
        var result = providerService.getProviderFirm(officeCode);

        // Assert
        assertNull(result);
        verify(timeService).isInTimeRange(LocalTime.of(7, 0), LocalTime.of(21, 30));
        verifyNoInteractions(providerApiClient);
    }

    @Test
    void testGetProviderOfficeWhenClientThrowsException() {
        // Arrange
        String officeCode = "0P322F";

        when(providerApiProperties.getStart()).thenReturn(new TimeProperties(7, 0));
        when(providerApiProperties.getEnd()).thenReturn(new TimeProperties(21, 30));
        when(timeService.isInTimeRange(any(), any())).thenReturn(true);
        when(providerApiClient.getProviderOffice(officeCode)).thenReturn(Mono.error(new RuntimeException("error")));

        // Act
        var result = providerService.getProviderFirm(officeCode);

        // Assert
        assertNull(result);
        verify(timeService).isInTimeRange(LocalTime.of(7, 0), LocalTime.of(21, 30));
        verify(providerApiClient, times(1)).getProviderOffice(officeCode);
    }
}
