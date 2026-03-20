package uk.gov.justice.laa.amend.claim.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ProviderApiClient;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;

@ExtendWith(MockitoExtension.class)
class ProviderServiceTest {

    @Mock
    private ProviderApiClient providerApiClient;

    @InjectMocks
    private ProviderService providerService;

    @Test
    void testGetProviderFirmSuccess() {
        String officeCode = "0P322F";

        ProviderFirmOfficeDto providerFirm = mock(ProviderFirmOfficeDto.class, RETURNS_DEEP_STUBS);

        when(providerApiClient.getProviderOffice(officeCode)).thenReturn(Mono.just(providerFirm));

        var result = providerService.getProviderFirm(officeCode);

        assertNotNull(result);
        verify(providerApiClient, times(1)).getProviderOffice(officeCode);
    }

    @Test
    void testGetProviderFirmWhenExceptionReturnsNull() {
        String officeCode = "0P322F";

        when(providerApiClient.getProviderOffice(officeCode)).thenReturn(Mono.error(new RuntimeException("error")));

        var result = providerService.getProviderFirm(officeCode);

        assertNull(result);
        verify(providerApiClient, times(1)).getProviderOffice(officeCode);
    }
}
