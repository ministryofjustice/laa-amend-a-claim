package uk.gov.justice.laa.amend.claim.models.search;

import java.util.Arrays;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.sorting.SortField;

@Getter
public enum SearchSortField implements SortField {
  UNIQUE_FILE_NUMBER("unique_file_number"),
  CASE_REFERENCE_NUMBER("case_reference_number"),
  CLIENT_SURNAME("client_surname"),
  SUBMISSION_PERIOD("submission_period"),
  CATEGORY_OF_LAW("category_of_law"),
  VOIDED("status");

  private final String value;

  SearchSortField(String value) {
    this.value = value;
  }

  public static SearchSortField fromValue(String value) {
    return Arrays.stream(values())
        .filter(sortField -> sortField.value.equals(value))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
