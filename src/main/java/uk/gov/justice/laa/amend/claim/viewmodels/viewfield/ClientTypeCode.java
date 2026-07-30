package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

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
