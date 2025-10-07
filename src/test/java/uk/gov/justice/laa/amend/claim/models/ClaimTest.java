package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

import java.time.LocalDate;

public class ClaimTest {

    @Test
    void convertClaimResponseIntoClaim() {
        ClaimResponse claim = new ClaimResponse();
        claim.setUniqueFileNumber("290419/711");
        claim.setCaseReferenceNumber("EF/4560/2018/4364683");
        claim.setClientSurname("Doe");
        claim.setCaseStartDate("2019-04-29");
        claim.setMatterTypeCode("IMCB:IRVL");
        claim.setStatus(ClaimStatus.READY_TO_PROCESS);

        Claim result = new Claim(claim, "pan");

        Assertions.assertEquals("290419/711", result.getUniqueFileNumber());
        Assertions.assertEquals("EF/4560/2018/4364683", result.getCaseReferenceNumber());
        Assertions.assertEquals("Doe", result.getClientSurname());
        Assertions.assertEquals(LocalDate.of(2019, 4, 29), result.getDateSubmitted());
        Assertions.assertEquals("29 Apr 2019", result.getDateSubmittedForDisplay());
        Assertions.assertEquals(18015, result.getDateSubmittedForSorting());
        Assertions.assertEquals("pan", result.getAccount());
        Assertions.assertEquals("IMCB:IRVL", result.getType());
        Assertions.assertEquals(ClaimStatus.READY_TO_PROCESS, result.getStatus());
    }

    @Test
    void handleNulls() {
        ClaimResponse claim = new ClaimResponse();

        Claim result = new Claim(claim, "pan");

        Assertions.assertNull(result.getUniqueFileNumber());
        Assertions.assertNull(result.getCaseReferenceNumber());
        Assertions.assertNull(result.getClientSurname());
        Assertions.assertNull(result.getDateSubmitted());
        Assertions.assertEquals("", result.getDateSubmittedForDisplay());
        Assertions.assertEquals(0, result.getDateSubmittedForSorting());
        Assertions.assertEquals("pan", result.getAccount());
        Assertions.assertNull(result.getType());
        Assertions.assertNull(result.getStatus());
    }
}
