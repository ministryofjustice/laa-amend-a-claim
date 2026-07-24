package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum AdviceType implements FieldOption {
  FACE_TO_FACE("FTF"),
  REMOTELY("REM");

  private final String value;

  AdviceType(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
