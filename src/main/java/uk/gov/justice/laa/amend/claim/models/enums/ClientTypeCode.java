package uk.gov.justice.laa.amend.claim.models.enums;

import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldOption;

public enum ClientTypeCode implements FieldOption {
  PARENT("P"),
  CHILD("C"),
  JOINED_PARTY("J");

  private final String value;

  ClientTypeCode(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
