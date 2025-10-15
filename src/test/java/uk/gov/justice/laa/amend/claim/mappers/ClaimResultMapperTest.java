package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.BoltOnPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

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
        ClaimResultSet claimResultSetMock = mock(ClaimResultSet.class);
        ClaimResponse claimResponseMock = mock(ClaimResponse.class);
        FeeCalculationPatch feeCalculationPatchMock = mock(FeeCalculationPatch.class);
        BoltOnPatch boltOnPatchMock = mock(BoltOnPatch.class);

        when(claimResultSetMock.getTotalElements()).thenReturn(15);
        when(claimResultSetMock.getSize()).thenReturn(5);
        when(claimResultSetMock.getNumber()).thenReturn(2);
        when(claimResultSetMock.getContent()).thenReturn(Collections.singletonList(claimResponseMock));

        ClaimResultMapper mapper = new ClaimResultMapperImpl();
        Claim expectedClaim = new Claim();
        expectedClaim.setUniqueFileNumber("UFN123");
        expectedClaim.setCaseReferenceNumber("CRN001");
        expectedClaim.setClientSurname("Doe");
        expectedClaim.setDateSubmitted(LocalDate.of(2023, 1, 1));
        expectedClaim.setAccount("0U733A");
        expectedClaim.setType(null);
        expectedClaim.setEscaped(true);
        expectedClaim.setReferenceNumber("UFN123");
        expectedClaim.setDateSubmittedForDisplay("01 Jan 2023");
        expectedClaim.setDateSubmittedForSorting(19358);

        when(claimResponseMock.getUniqueFileNumber()).thenReturn("UFN123");
        when(claimResponseMock.getCaseReferenceNumber()).thenReturn("CRN001");
        when(claimResponseMock.getClientSurname()).thenReturn("Doe");
        when(claimResponseMock.getCaseStartDate()).thenReturn(LocalDate.of(2023, 1, 1).toString());
        when(claimResponseMock.getScheduleReference()).thenReturn("0U733A/2018/02");
        when(claimResponseMock.getFeeCalculationResponse()).thenReturn(feeCalculationPatchMock);
        when(feeCalculationPatchMock.getBoltOnDetails()).thenReturn(boltOnPatchMock);
        when(boltOnPatchMock.getEscapeCaseFlag()).thenReturn(true);

        // Act
        SearchResultViewModel resultViewModel = mapper.toDto(claimResultSetMock, "/");

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
        assertEquals(expectedClaim.getEscaped(), resultClaim.getEscaped());
        assertEquals(expectedClaim.getDateSubmittedForDisplay(), resultClaim.getDateSubmittedForDisplay());
        assertEquals(expectedClaim.getDateSubmittedForSorting(), resultClaim.getDateSubmittedForSorting());
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
        SearchResultViewModel resultViewModel = mapper.toDto(claimResultSet, "/");

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
        when(claimResponseMock.getFeeCalculationResponse()).thenReturn(null);
        when(claimResponseMock.getClientSurname()).thenReturn(null);
        when(claimResponseMock.getCaseReferenceNumber()).thenReturn(null);

        ClaimResultMapper mapper = new ClaimResultMapperImpl();

        // Act
        SearchResultViewModel resultViewModel = mapper.toDto(claimResultSet, "/");

        // Assert
        List<Claim> claims = resultViewModel.getClaims();
        assertEquals(1, claims.size());
        Claim resultClaim = claims.get(0);

        assertEquals("UFN456", resultClaim.getUniqueFileNumber());
        assertNull(resultClaim.getCaseReferenceNumber());
        assertNull(resultClaim.getClientSurname());
        assertNull(resultClaim.getDateSubmitted());
        assertNull(resultClaim.getAccount());
        assertNull(resultClaim.getType());
        assertNull(resultClaim.getEscaped());
        assertEquals("UFN456", resultClaim.getReferenceNumber());
        assertNull(resultClaim.getDateSubmittedForDisplay());
        assertEquals(0, resultClaim.getDateSubmittedForSorting());
    }

    @Test
    void givenClaimResponseWithoutUniqueFileNumber_whenToDtoIsCalled_thenCaseReferenceNumberIsUsed() {
        // Arrange
        ClaimResultSet claimResultSetMock = mock(ClaimResultSet.class);
        ClaimResponse claimResponseMock = mock(ClaimResponse.class);

        when(claimResultSetMock.getContent()).thenReturn(Collections.singletonList(claimResponseMock));
        when(claimResultSetMock.getTotalElements()).thenReturn(1);
        when(claimResultSetMock.getSize()).thenReturn(1);
        when(claimResultSetMock.getNumber()).thenReturn(1);

        when(claimResponseMock.getCaseReferenceNumber()).thenReturn("CRN001");

        ClaimResultMapper mapper = new ClaimResultMapperImpl();

        // Act
        SearchResultViewModel resultViewModel = mapper.toDto(claimResultSetMock, "/");

        // Assert
        List<Claim> claims = resultViewModel.getClaims();
        assertEquals(1, claims.size());
        Claim resultClaim = claims.get(0);

        assertNull(resultClaim.getUniqueFileNumber());
        assertEquals("CRN001", resultClaim.getCaseReferenceNumber());
        assertEquals("CRN001", resultClaim.getReferenceNumber());
    }

    @Test
    void givenEscapedClaimThenReturnCorrectStatus() {
        // Arrange
        ClaimResultSet claimResultSetMock = mock(ClaimResultSet.class);
        ClaimResponse claimResponseMock = mock(ClaimResponse.class);
        FeeCalculationPatch feeCalculationPatchMock = mock(FeeCalculationPatch.class);
        BoltOnPatch boltOnPatchMock = mock(BoltOnPatch.class);

        when(claimResultSetMock.getContent()).thenReturn(Collections.singletonList(claimResponseMock));
        when(claimResultSetMock.getTotalElements()).thenReturn(1);
        when(claimResultSetMock.getSize()).thenReturn(1);
        when(claimResultSetMock.getNumber()).thenReturn(1);

        when(claimResponseMock.getFeeCalculationResponse()).thenReturn(feeCalculationPatchMock);
        when(feeCalculationPatchMock.getBoltOnDetails()).thenReturn(boltOnPatchMock);
        when(boltOnPatchMock.getEscapeCaseFlag()).thenReturn(true);

        ClaimResultMapper mapper = new ClaimResultMapperImpl();

        // Act
        SearchResultViewModel resultViewModel = mapper.toDto(claimResultSetMock, "/");

        // Assert
        List<Claim> claims = resultViewModel.getClaims();
        assertEquals(1, claims.size());
        Claim resultClaim = claims.get(0);

        assertEquals(true, resultClaim.getEscaped());
    }

    @Test
    void givenFixedFeeClaimThenReturnCorrectStatus() {
        // Arrange
        ClaimResultSet claimResultSetMock = mock(ClaimResultSet.class);
        ClaimResponse claimResponseMock = mock(ClaimResponse.class);
        FeeCalculationPatch feeCalculationPatchMock = mock(FeeCalculationPatch.class);
        BoltOnPatch boltOnPatchMock = mock(BoltOnPatch.class);

        when(claimResultSetMock.getContent()).thenReturn(Collections.singletonList(claimResponseMock));
        when(claimResultSetMock.getTotalElements()).thenReturn(1);
        when(claimResultSetMock.getSize()).thenReturn(1);
        when(claimResultSetMock.getNumber()).thenReturn(1);

        when(claimResponseMock.getFeeCalculationResponse()).thenReturn(feeCalculationPatchMock);
        when(feeCalculationPatchMock.getBoltOnDetails()).thenReturn(boltOnPatchMock);
        when(boltOnPatchMock.getEscapeCaseFlag()).thenReturn(false);

        ClaimResultMapper mapper = new ClaimResultMapperImpl();

        // Act
        SearchResultViewModel resultViewModel = mapper.toDto(claimResultSetMock, "/");

        // Assert
        List<Claim> claims = resultViewModel.getClaims();
        assertEquals(1, claims.size());
        Claim resultClaim = claims.get(0);

        assertEquals(false, resultClaim.getEscaped());
    }
}