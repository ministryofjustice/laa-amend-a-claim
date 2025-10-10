package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ClaimServiceTest {

    @Mock
    private ClaimsApiClient claimsApiClient;

    @Mock
    private ClaimResultMapper claimResultMapper;

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
        var mockViewModel = new SearchResultViewModel();

        when(claimsApiClient.searchClaims("0P322F", null, null, 1, 10))
                .thenReturn(Mono.just(mockApiResponse));
        when(claimResultMapper.toDto(mockApiResponse))
                .thenReturn(mockViewModel);

        // Act
        SearchResultViewModel result = claimService.searchClaims("0P322F", 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(mockViewModel, result);

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, 1, 10);
        verify(claimResultMapper, times(1)).toDto(mockApiResponse);
    }

    @Test
    @DisplayName("Should throw RuntimeException when API client throws exception")
    void testSearchClaims_ApiClientThrowsException() {
        // Arrange
        when(claimsApiClient.searchClaims("0P322F", null, null, 1, 10))
                .thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                claimService.searchClaims("0P322F", 1, 10)
        );
        assertTrue(exception.getMessage().contains("API Error"));

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, 1, 10);
        verifyNoInteractions(claimResultMapper);
    }

    @Test
    @DisplayName("Should handle empty API response without exception")
    void testSearchClaims_EmptyResponse() {
        // Arrange
        when(claimsApiClient.searchClaims("0P322F", null, null, 1, 10))
                .thenReturn(Mono.empty());
        when(claimResultMapper.toDto(null))
                .thenReturn(null);

        // Act
        SearchResultViewModel result = claimService.searchClaims("0P322F", 1, 10);

        // Assert
        assertNull(result);

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, 1, 10);
        verify(claimResultMapper, times(1)).toDto(null);
    }
}