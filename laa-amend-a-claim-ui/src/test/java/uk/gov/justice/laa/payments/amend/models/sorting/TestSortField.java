package uk.gov.justice.laa.payments.amend.models.sorting;

record TestSortField(String value) implements SortField {
  @Override
  public String getValue() {
    return value;
  }
}
