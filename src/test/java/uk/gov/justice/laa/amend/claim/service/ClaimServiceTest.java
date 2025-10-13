package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClaimServiceTest {

    @Mock
    private ClaimsApiClient claimsApiClient;

    @InjectMocks
    private ClaimService claimService;

    public ClaimServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return valid SearchResultViewModel when API client provides valid response")
    void testSearchClaims_ValidResponse() {
        // Arrange
        var mockApiResponse = new ClaimResultSet(); // Replace with appropriate type or mock object

        when(claimsApiClient.searchClaims("0P322F", null, null, 0, 10))
                .thenReturn(Mono.just(mockApiResponse));

        // Act
        ClaimResultSet result = claimService.searchClaims("0P322F", 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(mockApiResponse, result);

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, 0, 10);
    }

    @Test
    @DisplayName("Should throw RuntimeException when API client throws exception")
    void testSearchClaims_ApiClientThrowsException() {
        // Arrange
        when(claimsApiClient.searchClaims("0P322F", null, null, 0, 10))
                .thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                claimService.searchClaims("0P322F", 1, 10)
        );
        assertTrue(exception.getMessage().contains("API Error"));

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, 0, 10);
    }

    @Test
    @DisplayName("Should handle empty API response without exception")
    void testSearchClaims_EmptyResponse() {
        // Arrange
        when(claimsApiClient.searchClaims("0P322F", null, null, 0, 10))
                .thenReturn(Mono.empty());

        // Act
        ClaimResultSet result = claimService.searchClaims("0P322F", 1, 10);

        // Assert
        assertNull(result);

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, 0, 10);
    }
}