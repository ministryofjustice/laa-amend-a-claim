package uk.gov.justice.laa.payments.amend.models.enums;

public enum FieldType {
  TEXT,
  BOOLEAN,
  MONETARY,
  PERCENTAGE,
  NUMBER,
  DATE,
  ENUM_TYPEAHEAD,
  ENUM_DROPDOWN;

  public boolean isEnum() {
    return this == ENUM_TYPEAHEAD || this == ENUM_DROPDOWN;
  }
}
