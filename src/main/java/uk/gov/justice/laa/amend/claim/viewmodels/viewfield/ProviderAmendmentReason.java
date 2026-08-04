package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum ProviderAmendmentReason {
  PROVIDER_ERROR("Provider error"),
  CASE_REOPENED("Case reopened"),
  MONEY_RECOVERED("Money recovered");

  private final String displayName;

  ProviderAmendmentReason(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
