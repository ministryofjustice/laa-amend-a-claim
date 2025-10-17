package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.BoltOnPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.time.LocalDate;
import java.time.YearMonth;
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
        expectedClaim.setSubmissionId("submissionId");
        expectedClaim.setClaimId("claimId");
        expectedClaim.setCaseReferenceNumber("CRN001");
        expectedClaim.setClientSurname("Doe");
        expectedClaim.setClientForename("John");
        expectedClaim.setSubmissionPeriod(YearMonth.of(2023, 1));
        expectedClaim.setCaseStartDate(LocalDate.of(2023, 1, 1));
        expectedClaim.setCaseEndDate(LocalDate.of(2023, 12, 31));
        expectedClaim.setFeeScheme("fee scheme");
        expectedClaim.setCategoryOfLaw("category of law");
        expectedClaim.setMatterTypeCode("matter type code");
        expectedClaim.setScheduleReference("0U733A/2018/02");
        expectedClaim.setEscaped(true);
        expectedClaim.setProviderAccountNumber("TODO");

        when(claimResponseMock.getFeeCalculationResponse()).thenReturn(feeCalculationPatchMock);
        when(feeCalculationPatchMock.getBoltOnDetails()).thenReturn(boltOnPatchMock);
        when(claimResponseMock.getUniqueFileNumber()).thenReturn("UFN123");
        when(claimResponseMock.getSubmissionId()).thenReturn("submissionId");
        when(claimResponseMock.getId()).thenReturn("claimId");
        when(claimResponseMock.getCaseReferenceNumber()).thenReturn("CRN001");
        when(claimResponseMock.getClientSurname()).thenReturn("Doe");
        when(claimResponseMock.getClientForename()).thenReturn("John");
        when(claimResponseMock.getSubmissionPeriod()).thenReturn("JAN-2023");
        when(claimResponseMock.getCaseStartDate()).thenReturn("2023-01-01");
        when(claimResponseMock.getCaseConcludedDate()).thenReturn("2023-12-31");
        when(claimResponseMock.getFeeSchemeCode()).thenReturn("fee scheme");
        when(feeCalculationPatchMock.getCategoryOfLaw()).thenReturn("category of law");
        when(claimResponseMock.getMatterTypeCode()).thenReturn("matter type code");
        when(claimResponseMock.getScheduleReference()).thenReturn("0U733A/2018/02");
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
        assertEquals(expectedClaim.getSubmissionId(), resultClaim.getSubmissionId());
        assertEquals(expectedClaim.getClaimId(), resultClaim.getClaimId());
        assertEquals(expectedClaim.getCaseReferenceNumber(), resultClaim.getCaseReferenceNumber());
        assertEquals(expectedClaim.getClientSurname(), resultClaim.getClientSurname());
        assertEquals(expectedClaim.getClientForename(), resultClaim.getClientForename());
        assertEquals(expectedClaim.getSubmissionPeriod(), resultClaim.getSubmissionPeriod());
        assertEquals(expectedClaim.getCaseStartDate(), resultClaim.getCaseStartDate());
        assertEquals(expectedClaim.getCaseEndDate(), resultClaim.getCaseEndDate());
        assertEquals(expectedClaim.getFeeScheme(), resultClaim.getFeeScheme());
        assertEquals(expectedClaim.getCategoryOfLaw(), resultClaim.getCategoryOfLaw());
        assertEquals(expectedClaim.getMatterTypeCode(), resultClaim.getMatterTypeCode());
        assertEquals(expectedClaim.getScheduleReference(), resultClaim.getScheduleReference());
        assertEquals(expectedClaim.getEscaped(), resultClaim.getEscaped());
        assertEquals(expectedClaim.getProviderAccountNumber(), resultClaim.getProviderAccountNumber());
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

        ClaimResultMapper mapper = new ClaimResultMapperImpl();

        // Act
        SearchResultViewModel resultViewModel = mapper.toDto(claimResultSet, "/");

        // Assert
        List<Claim> claims = resultViewModel.getClaims();
        assertEquals(1, claims.size());
        Claim resultClaim = claims.get(0);

        assertNull(resultClaim.getUniqueFileNumber());
        assertNull(resultClaim.getSubmissionId());
        assertNull(resultClaim.getClaimId());
        assertNull(resultClaim.getCaseReferenceNumber());
        assertNull(resultClaim.getClientSurname());
        assertNull(resultClaim.getClientForename());
        assertNull(resultClaim.getSubmissionPeriod());
        assertNull(resultClaim.getCaseStartDate());
        assertNull(resultClaim.getCaseEndDate());
        assertNull(resultClaim.getFeeScheme());
        assertNull(resultClaim.getCategoryOfLaw());
        assertNull(resultClaim.getMatterTypeCode());
        assertNull(resultClaim.getScheduleReference());
        assertNull(resultClaim.getEscaped());
        assertEquals("TODO", resultClaim.getProviderAccountNumber());
    }
}