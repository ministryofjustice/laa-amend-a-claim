package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

import java.util.Collections;

public class SearchResultViewModelTest {

    @Test
    void handleEmptyResponse() {
        ClaimResultSet response = new ClaimResultSet();
        SearchResultViewModel result = new SearchResultViewModel(response, "pan");

        Assertions.assertEquals(0, result.getClaims().size());
        Assertions.assertEquals(0, result.getPagination().getItems().size());
        Assertions.assertEquals(0, result.getPagination().getResults().getCount());
        Assertions.assertEquals(0, result.getPagination().getResults().getFrom());
        Assertions.assertEquals(0, result.getPagination().getResults().getTo());
        Assertions.assertEquals("results", result.getPagination().getResults().getText());
        Assertions.assertNull(result.getPagination().getPrevious());
        Assertions.assertNull(result.getPagination().getNext());
    }

    @Test
    void handleNonEmptyResponse() {
        ClaimResponse claim = new ClaimResponse();
        claim.setUniqueFileNumber("290419/711");
        claim.setCaseReferenceNumber("EF/4560/2018/4364683");
        claim.setClientSurname("Doe");
        claim.setCaseStartDate("2019-04-29");
        claim.setFeeCode("CAPA");
        claim.setMatterTypeCode("IMCB:IRVL");
        claim.setStatus(ClaimStatus.READY_TO_PROCESS);

        ClaimResultSet response = new ClaimResultSet();
        response.setContent(Collections.nCopies(10, claim));
        response.setTotalPages(9);
        response.setTotalElements(90);
        response.setNumber(5);
        response.setSize(10);

        SearchResultViewModel result = new SearchResultViewModel(response, "pan");

        Assertions.assertEquals(10, result.getClaims().size());
        Assertions.assertEquals(7, result.getPagination().getItems().size());
        Assertions.assertEquals(90, result.getPagination().getResults().getCount());
        Assertions.assertEquals(41, result.getPagination().getResults().getFrom());
        Assertions.assertEquals(50, result.getPagination().getResults().getTo());
        Assertions.assertEquals("results", result.getPagination().getResults().getText());
        Assertions.assertNotNull(result.getPagination().getPrevious());
        Assertions.assertNotNull(result.getPagination().getNext());
    }
}
