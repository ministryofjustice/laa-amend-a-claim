package uk.gov.justice.laa.amend.claim.models.sorting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.justice.laa.amend.claim.models.search.SearchSortField;

public class SearchSortFieldTest {

  @Nested
  class FromValueTests {
    @Test
    void shouldConvertUniqueFileNumber() {
      String str = "unique_file_number";
      SearchSortField result = SearchSortField.fromValue(str);
      Assertions.assertEquals(SearchSortField.UNIQUE_FILE_NUMBER, result);
    }

    @Test
    void shouldConvertCaseReferenceNumber() {
      String str = "case_reference_number";
      SearchSortField result = SearchSortField.fromValue(str);
      Assertions.assertEquals(SearchSortField.CASE_REFERENCE_NUMBER, result);
    }

    @Test
    void shouldConvertClientSurname() {
      String str = "client_surname";
      SearchSortField result = SearchSortField.fromValue(str);
      Assertions.assertEquals(SearchSortField.CLIENT_SURNAME, result);
    }

    @Test
    void shouldConvertSubmissionPeriod() {
      String str = "submission_period";
      SearchSortField result = SearchSortField.fromValue(str);
      Assertions.assertEquals(SearchSortField.SUBMISSION_PERIOD, result);
    }

    @Test
    void shouldConvertCategoryOfLaw() {
      String str = "category_of_law";
      SearchSortField result = SearchSortField.fromValue(str);
      Assertions.assertEquals(SearchSortField.CATEGORY_OF_LAW, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"foo", "bar"})
    void shouldThrowExceptionForAnythingElse(String str) {
      Assertions.assertThrows(IllegalArgumentException.class, () -> SearchSortField.fromValue(str));
    }
  }
}
