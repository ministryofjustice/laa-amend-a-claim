package uk.gov.justice.laa.payments.amend.models.fsp;

public record FeeCode(String feeCode, String feeCodeDescription) {

  public String fullFeeCodeDescription() {
    return feeCode + " - " + feeCodeDescription;
  }
}
