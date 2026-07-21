package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum AdviceType implements FieldOption {
  FTF("FTF"),
  REM("REM");

  private final String value;

  AdviceType(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
