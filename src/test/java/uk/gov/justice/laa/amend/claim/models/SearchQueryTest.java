package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.search.SearchQuery;
import uk.gov.justice.laa.amend.claim.models.search.SearchSort;
import uk.gov.justice.laa.amend.claim.models.search.SearchSortField;
import uk.gov.justice.laa.amend.claim.models.sorting.SortDirection;

public class SearchQueryTest {

  @Test
  void createRedirectUrlWhenUnsorted() {
    SearchQuery query = SearchQuery.builder().build();

    String result = query.getRedirectUrl(1, null);

    Assertions.assertEquals("/?page=1", result);
  }

  @Test
  void createRedirectUrlWhenOfficeCodePresent() {
    var sort =
        SearchSort.builder()
            .field(SearchSortField.UNIQUE_FILE_NUMBER)
            .direction(SortDirection.ASCENDING)
            .build();

    SearchQuery query = SearchQuery.builder().officeCode("123").sort(sort).build();

    String result = query.getRedirectUrl();

    Assertions.assertEquals("/?officeCode=123&page=1&sort=unique_file_number,asc", result);
  }

  @Test
  void createRedirectUrlWhenOfficeCodeAndDatePresent() {
    var sort =
        SearchSort.builder()
            .field(SearchSortField.UNIQUE_FILE_NUMBER)
            .direction(SortDirection.ASCENDING)
            .build();

    SearchQuery query =
        SearchQuery.builder()
            .page(2)
            .officeCode("123")
            .submissionDateMonth("3")
            .submissionDateYear("2007")
            .sort(sort)
            .build();

    String result = query.getRedirectUrl();

    Assertions.assertEquals(
        "/?officeCode=123&submissionDateMonth=3&submissionDateYear=2007&page=2&sort=unique_file_number,asc",
        result);
  }

  @Test
  void createRedirectUrlWhenAllPresent() {
    var sort =
        SearchSort.builder()
            .field(SearchSortField.UNIQUE_FILE_NUMBER)
            .direction(SortDirection.ASCENDING)
            .build();

    SearchQuery query =
        SearchQuery.builder()
            .page(3)
            .officeCode("123")
            .submissionDateMonth("3")
            .submissionDateYear("2007")
            .uniqueFileNumber("456")
            .caseReferenceNumber("789")
            .sort(sort)
            .build();

    String result = query.getRedirectUrl();

    Assertions.assertEquals(
        "/?officeCode=123&submissionDateMonth=3&submissionDateYear=2007&uniqueFileNumber=456&caseReferenceNumber=789&page=3&sort=unique_file_number,asc",
        result);
  }
}
