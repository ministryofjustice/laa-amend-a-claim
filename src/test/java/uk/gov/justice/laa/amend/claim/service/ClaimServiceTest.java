package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import java.util.Optional;

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
    @DisplayName("Should return valid ClaimResultSet when API client provides valid response")
    void testSearchClaims_ValidResponse() {
        // Arrange
        var mockApiResponse = new ClaimResultSet(); // Replace with appropriate type or mock object

        when(claimsApiClient.searchClaims("0P322F", null, null, null, 0, 10))
                .thenReturn(Mono.just(mockApiResponse));

        // Act
        ClaimResultSet result = claimService.searchClaims("0P322F", Optional.empty(), Optional.empty(), 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(mockApiResponse, result);

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, null, 0, 10);
    }

    @Test
    @DisplayName("Should throw RuntimeException when API client throws exception")
    void testSearchClaims_ApiClientThrowsException() {
        // Arrange
        when(claimsApiClient.searchClaims("0P322F", null, null, null, 0, 10))
                .thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                claimService.searchClaims("0P322F", Optional.empty(), Optional.empty(), 1, 10)
        );
        assertTrue(exception.getMessage().contains("API Error"));

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, null, 0, 10);
    }

    @Test
    @DisplayName("Should handle empty API response without exception")
    void testSearchClaims_EmptyResponse() {
        // Arrange
        when(claimsApiClient.searchClaims("0P322F", null, null, null, 0, 10))
                .thenReturn(Mono.empty());

        // Act
        ClaimResultSet result = claimService.searchClaims("0P322F", Optional.empty(), Optional.empty(), 1, 10);

        // Assert
        assertNull(result);

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, null, 0, 10);
    }


    @Test
    @DisplayName("Should return valid ClaimResponse when API client provides valid response")
    void testGetClaim_ValidResponse() {
        // Arrange
        var mockApiResponse = new ClaimResponse(); // Replace with appropriate type or mock object

        when(claimsApiClient.getClaim("submissionId", "claimId"))
                .thenReturn(Mono.just(mockApiResponse));

        // Act
        ClaimResponse result = claimService.getClaim("submissionId", "claimId");

        // Assert
        assertNotNull(result);
        assertEquals(mockApiResponse, result);

        verify(claimsApiClient, times(1)).getClaim("submissionId", "claimId");
    }

    @Test
    @DisplayName("Should throw RuntimeException when API client throws exception")
    void testGetClaim_ApiClientThrowsException() {
        // Arrange
        when(claimsApiClient.getClaim("submissionId", "claimId"))
                .thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                claimService.getClaim("submissionId", "claimId")
        );
        assertTrue(exception.getMessage().contains("API Error"));

        verify(claimsApiClient, times(1)).getClaim("submissionId", "claimId");
    }
}
