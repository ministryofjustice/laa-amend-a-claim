package uk.gov.justice.laa.amend.claim.models.fsp;

public record FeeCode(String feeCode, String feeCodeDescription) {

  public String fullFeeCodeDescription() {
    return feeCode + " - " + feeCodeDescription;
  }
}
