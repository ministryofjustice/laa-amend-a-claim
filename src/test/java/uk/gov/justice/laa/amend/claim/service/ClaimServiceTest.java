package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.client.ProviderApiClient;
import uk.gov.justice.laa.amend.claim.exceptions.ClaimNotFoundException;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Sort;
import uk.gov.justice.laa.amend.claim.models.SortDirection;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClaimServiceTest {

    @Mock
    private ClaimsApiClient claimsApiClient;

    @Mock
    private ClaimMapper claimMapper;

    @Mock
    private ProviderApiClient providerApiClient;

    @InjectMocks
    private ClaimService claimService;

    public ClaimServiceTest() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("Should return sorted valid ClaimResultSet when API client provides valid response")
    void testSortedSearchClaims_ValidResponse() {
        // Arrange
        var mockApiResponse = new ClaimResultSet(); // Replace with appropriate type or mock object

        when(claimsApiClient.searchClaims("0P322F", null, null, null, 0, 10, "uniqueFileNumber,asc"))
                .thenReturn(Mono.just(mockApiResponse));
        Sort sort = Sort.builder().field("uniqueFileNumber").direction(SortDirection.ASCENDING).build();

        // Act
        ClaimResultSet result = claimService.searchClaims("0p322f", Optional.empty(), Optional.empty(), Optional.empty(), 1, 10, sort);

        // Assert
        assertNotNull(result);
        assertEquals(mockApiResponse, result);

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, null, 0, 10, "uniqueFileNumber,asc");
    }

    @Test
    @DisplayName("Should return valid unsorted ClaimResultSet when API client provides valid response")
    void testUnsortedSearchClaims_ValidResponse() {
        // Arrange
        var mockApiResponse = new ClaimResultSet(); // Replace with appropriate type or mock object

        when(claimsApiClient.searchClaims("0P322F", null, null, null, 0, 10, null))
            .thenReturn(Mono.just(mockApiResponse));

        // Act
        ClaimResultSet result = claimService.searchClaims("0p322f", Optional.empty(), Optional.empty(), Optional.empty(), 1, 10, null);

        // Assert
        assertNotNull(result);
        assertEquals(mockApiResponse, result);

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, null, 0, 10, null);
    }

    @Test
    @DisplayName("Should throw RuntimeException when API client throws exception")
    void testSearchClaims_ApiClientThrowsException() {
        // Arrange
        when(claimsApiClient.searchClaims("0P322F", null, null, null, 0, 10, "uniqueFileNumber,asc"))
                .thenThrow(new RuntimeException("API Error"));
        Sort sort = Sort.builder().field("uniqueFileNumber").direction(SortDirection.ASCENDING).build();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                claimService.searchClaims("0P322F", Optional.empty(), Optional.empty(), Optional.empty(), 1, 10, sort)
        );
        assertTrue(exception.getMessage().contains("API Error"));

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, null, 0, 10, "uniqueFileNumber,asc");
    }

    @Test
    @DisplayName("Should handle empty API response without exception")
    void testSearchClaims_EmptyResponse() {
        // Arrange
        when(claimsApiClient.searchClaims("0P322F", null, null, null, 0, 10, "uniqueFileNumber,asc"))
                .thenReturn(Mono.empty());
        Sort sort = Sort.builder().field("uniqueFileNumber").direction(SortDirection.ASCENDING).build();

        // Act
        ClaimResultSet result = claimService.searchClaims("0P322F", Optional.empty(), Optional.empty(), Optional.empty(), 1, 10, sort);

        // Assert
        assertNull(result);

        verify(claimsApiClient, times(1)).searchClaims("0P322F", null, null, null, 0, 10, "uniqueFileNumber,asc");
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

    @Test
    @DisplayName("Should throw Not Found exception when API client returns null")
    void testGetClaimDetails_ApiClientThrowsException() {
        // Arrange
        when(claimsApiClient.getClaim("submissionId", "claimId"))
                .thenReturn(Mono.empty());

        when(claimsApiClient.getSubmission("submissionId"))
                .thenReturn(Mono.just(new SubmissionResponse()));

        // Act & Assert
        ClaimNotFoundException exception = assertThrows(ClaimNotFoundException.class, () ->
                claimService.getClaimDetails("submissionId", "claimId")
        );
        assertTrue(exception.getMessage().contains( String.format("Claim with ID %s not found for submission %s", "claimId", "submissionId")));

        verify(claimsApiClient, times(1)).getClaim("submissionId", "claimId");
    }


    @Test
    @DisplayName("Should return claim details")
    void testGetClaimDetails_Success() {
        // Arrange
        when(claimsApiClient.getClaim("submissionId", "claimId"))
                .thenReturn(Mono.just(new ClaimResponse()));

        when(claimsApiClient.getSubmission("submissionId"))
                .thenReturn(Mono.just(new SubmissionResponse()));
        when(claimMapper.mapToClaimDetails(any(), any()))
                .thenReturn(new CivilClaimDetails());
        // Act & Assert
        var response = claimService.getClaimDetails("submissionId", "claimId");
        assertNotNull(response);

        verify(claimsApiClient, times(1)).getClaim("submissionId", "claimId");
        verify(claimsApiClient, times(1)).getSubmission("submissionId");
    }

    @Test
    @DisplayName("Should enrich claim details with provider name from provider API")
    void testGetClaimDetailsEnrichesProviderName() {
        // Arrange
        ClaimResponse claimResponse = new ClaimResponse();
        SubmissionResponse submissionResponse = new SubmissionResponse();
        submissionResponse.setOfficeAccountNumber("0P322F");
        CivilClaimDetails claimDetails = new CivilClaimDetails();

        ProviderFirmOfficeDto providerOffice = mock(ProviderFirmOfficeDto.class, RETURNS_DEEP_STUBS);
        when(providerOffice.getFirm().getFirmName()).thenReturn("Test Firm");

        when(claimsApiClient.getClaim("submissionId", "claimId"))
                .thenReturn(Mono.just(claimResponse));
        when(claimsApiClient.getSubmission("submissionId"))
                .thenReturn(Mono.just(submissionResponse));
        when(claimMapper.mapToClaimDetails(claimResponse, submissionResponse))
                .thenReturn(claimDetails);
        when(providerApiClient.getProviderOffice("0P322F"))
                .thenReturn(Mono.just(providerOffice));

        // Act
        var result = claimService.getClaimDetails("submissionId", "claimId");

        // Assert
        assertNotNull(result);
        verify(providerApiClient, times(1)).getProviderOffice("0P322F");
        verify(claimMapper, times(1)).enrichWithProviderName(claimDetails, "Test Firm");
    }
}
