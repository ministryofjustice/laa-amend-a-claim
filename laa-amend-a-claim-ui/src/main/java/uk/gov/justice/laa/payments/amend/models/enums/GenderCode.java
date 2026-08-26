package uk.gov.justice.laa.payments.amend.models.enums;

import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.FieldOption;

public enum GenderCode implements FieldOption {
  MALE("M"),
  FEMALE("F"),
  UNKNOWN("U");

  private final String value;

  GenderCode(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
