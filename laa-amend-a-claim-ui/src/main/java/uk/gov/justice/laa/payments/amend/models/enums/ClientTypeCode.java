package uk.gov.justice.laa.payments.amend.models.enums;

import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.FieldOption;

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
