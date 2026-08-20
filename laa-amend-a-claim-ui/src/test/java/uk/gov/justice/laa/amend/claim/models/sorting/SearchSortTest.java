package uk.gov.justice.laa.amend.claim.models.sorting;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.amend.claim.models.search.SearchSort;
import uk.gov.justice.laa.amend.claim.models.search.SearchSortField;

public class SearchSortTest {

  @Nested
  class ConstructorTests {
    @Test
    void shouldConvertStringToSortWhenAscendingOrder() {
      String str = "unique_file_number,asc";
      var result = new SearchSort(str);
      Assertions.assertEquals(SearchSortField.UNIQUE_FILE_NUMBER, result.getField());
      Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
    }

    @Test
    void shouldConvertStringToSortWhenDescendingOrder() {
      String str = "case_reference_number,desc";
      var result = new SearchSort(str);
      Assertions.assertEquals(SearchSortField.CASE_REFERENCE_NUMBER, result.getField());
      Assertions.assertEquals(SortDirection.DESCENDING, result.getDirection());
    }

    @Test
    void shouldThrowExceptionForInvalidDirection() {
      String str = "unique_file_number,foo";
      Assertions.assertThrows(IllegalArgumentException.class, () -> new SearchSort(str));
    }

    @Test
    void shouldThrowExceptionForNonCommaSeparatedInput() {
      String str = "unique_file_number";
      Assertions.assertThrows(IllegalArgumentException.class, () -> new SearchSort(str));
    }

    @Test
    void shouldThrowExceptionForInvalidInput() {
      String str = "unique_file_number,desc,foo";
      Assertions.assertThrows(IllegalArgumentException.class, () -> new SearchSort(str));
    }
  }

  @Nested
  class ToStringTests {

    @ParameterizedTest
    @MethodSource("sortArguments")
    void shouldBuildSort(SearchSortField sortField, SortDirection sortDirection) {
      var sort = SearchSort.builder().field(sortField).direction(sortDirection).build();

      Assertions.assertEquals(sortField, sort.getField());
      Assertions.assertEquals(sortDirection, sort.getDirection());
    }

    static Stream<Arguments> sortArguments() {
      return Arrays.stream(SearchSortField.values())
          .flatMap(
              field ->
                  Arrays.stream(SortDirection.values())
                      .map(sortDirection -> Arguments.of(field, sortDirection)));
    }

    @Test
    void shouldConvertSortToStringWhenNoOrder() {
      var sort =
          SearchSort.builder()
              .field(SearchSortField.UNIQUE_FILE_NUMBER)
              .direction(SortDirection.NONE)
              .build();
      Assertions.assertNull(sort.toString());
    }
  }
}
