package uk.gov.justice.laa.amend.claim.models.enums;

import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldOption;

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
