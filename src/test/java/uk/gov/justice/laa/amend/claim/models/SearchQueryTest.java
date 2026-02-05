package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SearchQueryTest {

    @Test
    void createRedirectUrlWhenUnsorted() {
        SearchQuery query = new SearchQuery();

        String result = query.getRedirectUrl(null);

        Assertions.assertEquals("/?page=1", result);
    }

    @Test
    void createRedirectUrlWhenAccountNumberPresent() {
        SearchQuery query = new SearchQuery();
        query.setProviderAccountNumber("123");

        Sort sort = Sort.builder().field(SortField.UNIQUE_FILE_NUMBER).direction(SortDirection.ASCENDING).build();
        String result = query.getRedirectUrl(sort);

        Assertions.assertEquals("/?providerAccountNumber=123&page=1&sort=uniqueFileNumber,asc", result);
    }

    @Test
    void createRedirectUrlWhenAccountNumberAndDatePresent() {
        SearchQuery query = new SearchQuery();

        query.setPage(2);
        query.setProviderAccountNumber("123");
        query.setSubmissionDateMonth("3");
        query.setSubmissionDateYear("2007");

        Sort sort = Sort.builder().field(SortField.UNIQUE_FILE_NUMBER).direction(SortDirection.ASCENDING).build();
        String result = query.getRedirectUrl(sort);

        Assertions.assertEquals("/?providerAccountNumber=123&submissionDateMonth=3&submissionDateYear=2007&page=2&sort=uniqueFileNumber,asc", result);
    }

    @Test
    void createRedirectUrlWhenAllPresent() {
        SearchQuery query = new SearchQuery();

        query.setPage(3);
        query.setProviderAccountNumber("123");
        query.setSubmissionDateMonth("3");
        query.setSubmissionDateYear("2007");
        query.setUniqueFileNumber("456");
        query.setCaseReferenceNumber("789");

        Sort sort = Sort.builder().field(SortField.UNIQUE_FILE_NUMBER).direction(SortDirection.ASCENDING).build();
        String result = query.getRedirectUrl(sort);

        Assertions.assertEquals("/?providerAccountNumber=123&submissionDateMonth=3&submissionDateYear=2007&uniqueFileNumber=456&caseReferenceNumber=789&page=3&sort=uniqueFileNumber,asc", result);
    }
}
