package uk.gov.justice.laa.amend.claim.models.sorting;

record TestSortField(String value) implements SortField {
  @Override
  public String getValue() {
    return value;
  }
}
