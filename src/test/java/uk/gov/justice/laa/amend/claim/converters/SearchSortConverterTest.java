package uk.gov.justice.laa.amend.claim.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static uk.gov.justice.laa.amend.claim.models.search.SearchSortField.CASE_REFERENCE_NUMBER;
import static uk.gov.justice.laa.amend.claim.models.search.SearchSortField.UNIQUE_FILE_NUMBER;
import static uk.gov.justice.laa.amend.claim.models.sorting.SortDirection.ASCENDING;
import static uk.gov.justice.laa.amend.claim.models.sorting.SortDirection.DESCENDING;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

public class SearchSortConverterTest {

  @Test
  void shouldConvertAscendingField() {
    var source = "unique_file_number,asc";
    var converter = new SearchSortConverter();

    var result = converter.convert(source);

    assertThat(result).isNotNull();
    assertThat(result.getField()).isEqualTo(UNIQUE_FILE_NUMBER);
    assertThat(result.getDirection()).isEqualTo(ASCENDING);
  }

  @Test
  void shouldConvertDescendingField() {
    var source = "case_reference_number,desc";
    var converter = new SearchSortConverter();

    var result = converter.convert(source);

    assertThat(result).isNotNull();
    assertThat(result.getField()).isEqualTo(CASE_REFERENCE_NUMBER);
    assertThat(result.getDirection()).isEqualTo(DESCENDING);
  }

  @Test
  void shouldThrow400ExceptionWhenInvalidSort() {
    var source = "foo,bar";
    var converter = new SearchSortConverter();

    assertThatThrownBy(() -> converter.convert(source))
        .isInstanceOf(ResponseStatusException.class)
        .extracting("statusCode")
        .isEqualTo(BAD_REQUEST);
  }
}
