package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum DesignatedAccreditedRepresentative implements FieldOption {
  DESIGNATED_ACCREDITED_REPRESENTATIVE("1"),
  NON_DAR_EMPLOYEE("2"),
  NON_DAR_AGENT("3"),
  NON_DAR_COUNSEL("4"),
  NOT_APPLICABLE("5");

  private final String value;

  DesignatedAccreditedRepresentative(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
