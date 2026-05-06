package uk.gov.justice.laa.amend.claim.models.sorting;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class SearchSort extends Sort<SearchSortField> {

  public SearchSort(String str) {
    super(str);
  }

  public static SearchSort defaults() {
    SearchSort sort = builder().build();
    sort.applyDefaults();
    return sort;
  }

  @Override
  void applyDefaults() {
    this.field = SearchSortField.UNIQUE_FILE_NUMBER;
    this.direction = SortDirection.ASCENDING;
  }

  @Override
  SearchSortField createField(String value) {
    return SearchSortField.fromValue(value);
  }
}
