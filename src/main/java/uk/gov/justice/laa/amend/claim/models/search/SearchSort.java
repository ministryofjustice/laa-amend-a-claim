package uk.gov.justice.laa.amend.claim.models.search;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.justice.laa.amend.claim.models.sorting.Sort;
import uk.gov.justice.laa.amend.claim.models.sorting.SortDirection;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SearchSort extends Sort<SearchSortField> {

  public SearchSort(String sortString) {
    super(sortString);
  }

  public static SearchSort defaults() {
    return builder()
        .field(SearchSortField.UNIQUE_FILE_NUMBER)
        .direction(SortDirection.ASCENDING)
        .build();
  }

  @Override
  public SearchSortField createField(String value) {
    return SearchSortField.fromValue(value);
  }
}
