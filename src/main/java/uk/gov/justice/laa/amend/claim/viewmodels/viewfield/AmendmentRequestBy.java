package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum AmendmentRequestBy {
  PROVIDER("Provider"),
  CONTRACT_MANAGEMENT("Contract management"),
  ASSURANCE("Assurance");

  private final String displayName;

  AmendmentRequestBy(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
