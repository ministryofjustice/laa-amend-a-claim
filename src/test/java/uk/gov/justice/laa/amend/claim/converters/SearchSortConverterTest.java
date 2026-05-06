package uk.gov.justice.laa.amend.claim.converters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.models.sorting.SearchSortField;
import uk.gov.justice.laa.amend.claim.models.sorting.Sort;
import uk.gov.justice.laa.amend.claim.models.sorting.SortDirection;

public class SearchSortConverterTest {

  @Test
  void shouldDefaultToAscendingUniqueFileNumberIfSourceIsNull() {
    String source = null;
    SearchSortConverter converter = new SearchSortConverter();
    Sort result = converter.convert(source);
    Assertions.assertEquals(SearchSortField.UNIQUE_FILE_NUMBER, result.getField());
    Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
  }

  @Test
  void shouldConvertAscendingField() {
    String source = "unique_file_number,asc";
    SearchSortConverter converter = new SearchSortConverter();
    Sort result = converter.convert(source);
    Assertions.assertEquals(SearchSortField.UNIQUE_FILE_NUMBER, result.getField());
    Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
  }

  @Test
  void shouldConvertDescendingField() {
    String source = "case_reference_number,desc";
    SearchSortConverter converter = new SearchSortConverter();
    Sort result = converter.convert(source);
    Assertions.assertEquals(SearchSortField.CASE_REFERENCE_NUMBER, result.getField());
    Assertions.assertEquals(SortDirection.DESCENDING, result.getDirection());
  }

  @Test
  void shouldThrow400ExceptionWhenInvalidSort() {
    String source = "foo,bar";
    SearchSortConverter converter = new SearchSortConverter();

    ResponseStatusException exception =
        Assertions.assertThrows(ResponseStatusException.class, () -> converter.convert(source));

    Assertions.assertEquals(400, exception.getStatusCode().value());
  }
}
