package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum AmendmentReason {
  INCORRECT_MEANS_ASSESSMENT("Incorrect means assessment"),
  OTHER("Other");

  private final String displayName;

  AmendmentReason(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
