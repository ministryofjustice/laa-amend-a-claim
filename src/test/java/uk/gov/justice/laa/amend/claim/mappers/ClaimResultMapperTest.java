package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClaimResultMapperTest {

    @Test
    void givenValidClaimResultSet_whenToDtoIsCalled_thenCorrectViewModelIsReturned() {
        // Arrange
        ClaimResultSet claimResultSet = mock(ClaimResultSet.class);
        ClaimResponse claimResponseMock = mock(ClaimResponse.class);

        when(claimResultSet.getTotalElements()).thenReturn(15);
        when(claimResultSet.getSize()).thenReturn(5);
        when(claimResultSet.getNumber()).thenReturn(2);
        when(claimResultSet.getContent()).thenReturn(Collections.singletonList(claimResponseMock));

        ClaimResultMapper mapper = new ClaimResultMapperImpl();
        Claim expectedClaim = new Claim();
        expectedClaim.setUniqueFileNumber("UFN123");
        expectedClaim.setCaseReferenceNumber("CRN001");
        expectedClaim.setClientSurname("Doe");
        expectedClaim.setDateSubmitted(LocalDate.of(2023, 1, 1));
        expectedClaim.setAccount("Account001");
        expectedClaim.setType("MatterType001");
        expectedClaim.setStatus("VALID");
        expectedClaim.setDateSubmittedForDisplay("01 Jan 2023");

        when(claimResponseMock.getUniqueFileNumber()).thenReturn("UFN123");
        when(claimResponseMock.getCaseReferenceNumber()).thenReturn("CRN001");
        when(claimResponseMock.getClientSurname()).thenReturn("Doe");
        when(claimResponseMock.getCaseStartDate()).thenReturn(LocalDate.of(2023, 1, 1).toString());
        when(claimResponseMock.getScheduleReference()).thenReturn("Account001");
        when(claimResponseMock.getMatterTypeCode()).thenReturn("MatterType001");
        when(claimResponseMock.getStatus()).thenReturn(ClaimStatus.VALID);

        // Act
        SearchResultViewModel resultViewModel = mapper.toDto(claimResultSet);

        // Assert
        assertEquals(15, resultViewModel.getPagination().getResults().getCount());
        assertEquals(3, resultViewModel.getPagination().getItems().size());

        List<Claim> claims = resultViewModel.getClaims();
        assertEquals(1, claims.size());
        Claim resultClaim = claims.getFirst();

        assertEquals(expectedClaim.getUniqueFileNumber(), resultClaim.getUniqueFileNumber());
        assertEquals(expectedClaim.getCaseReferenceNumber(), resultClaim.getCaseReferenceNumber());
        assertEquals(expectedClaim.getClientSurname(), resultClaim.getClientSurname());
        assertEquals(expectedClaim.getDateSubmitted(), resultClaim.getDateSubmitted());
        assertEquals(expectedClaim.getAccount(), resultClaim.getAccount());
        assertEquals(expectedClaim.getType(), resultClaim.getType());
        assertEquals(expectedClaim.getStatus(), resultClaim.getStatus());
        assertEquals(expectedClaim.getDateSubmittedForDisplay(), resultClaim.getDateSubmittedForDisplay());
    }

    @Test
    void givenEmptyClaimResultSet_whenToDtoIsCalled_thenEmptyViewModelIsReturned() {
        // Arrange
        ClaimResultSet claimResultSet = mock(ClaimResultSet.class);

        when(claimResultSet.getTotalElements()).thenReturn(0);
        when(claimResultSet.getSize()).thenReturn(10);
        when(claimResultSet.getNumber()).thenReturn(1);
        when(claimResultSet.getContent()).thenReturn(List.of());

        ClaimResultMapper mapper = new ClaimResultMapperImpl();

        // Act
        SearchResultViewModel resultViewModel = mapper.toDto(claimResultSet);

        assertEquals(0, resultViewModel.getPagination().getResults().getCount());
        assertEquals(0, resultViewModel.getPagination().getItems().size());
        assertEquals(0, resultViewModel.getClaims().size());
    }

    @Test
    void givenClaimResponseWithoutOptionalValues_whenToDtoIsCalled_thenDefaultValuesAreApplied() {
        // Arrange
        ClaimResultSet claimResultSet = mock(ClaimResultSet.class);
        ClaimResponse claimResponseMock = mock(ClaimResponse.class);

        when(claimResultSet.getContent()).thenReturn(Collections.singletonList(claimResponseMock));
        when(claimResultSet.getTotalElements()).thenReturn(1);
        when(claimResultSet.getSize()).thenReturn(1);
        when(claimResultSet.getNumber()).thenReturn(1);

        when(claimResponseMock.getUniqueFileNumber()).thenReturn("UFN456");
        when(claimResponseMock.getCaseStartDate()).thenReturn(null);
        when(claimResponseMock.getScheduleReference()).thenReturn(null);
        when(claimResponseMock.getMatterTypeCode()).thenReturn(null);
        when(claimResponseMock.getStatus()).thenReturn(null);
        when(claimResponseMock.getClientSurname()).thenReturn(null);
        when(claimResponseMock.getCaseReferenceNumber()).thenReturn(null);

        ClaimResultMapper mapper = new ClaimResultMapperImpl();

        // Act
        SearchResultViewModel resultViewModel = mapper.toDto(claimResultSet);

        // Assert
        List<Claim> claims = resultViewModel.getClaims();
        assertEquals(1, claims.size());
        Claim resultClaim = claims.get(0);

        assertEquals("UFN456", resultClaim.getUniqueFileNumber());
        assertEquals("Unknown", resultClaim.getCaseReferenceNumber());
        assertEquals("Unknown", resultClaim.getClientSurname());
        assertNull(resultClaim.getDateSubmitted());
        assertNull(resultClaim.getAccount());
        assertNull(resultClaim.getType());
        assertEquals("Unknown", resultClaim.getStatus());
        assertNull(resultClaim.getDateSubmittedForDisplay());
    }
}