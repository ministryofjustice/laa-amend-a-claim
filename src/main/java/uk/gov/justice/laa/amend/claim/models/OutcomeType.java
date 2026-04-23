package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;

@Getter
public enum OutcomeType {
  PAID_IN_FULL("outcome.paidInFull", "paid-in-full", "Paid in Full"),
  REDUCED("outcome.reduced", "reduced-still-escaped", "Reduced"),
  REDUCED_TO_FIXED_FEE(
      "outcome.reducedToFixedFee", "reduced-to-fixed-fee-assessed", "Reduced to Fixed Fee"),
  NILLED("outcome.nilled", "nilled", "Nilled");

  private final String messageKey;
  private final String formValue;
  private final String csvLabel;

  OutcomeType(String messageKey, String formValue, String csvLabel) {
    this.messageKey = messageKey;
    this.formValue = formValue;
    this.csvLabel = csvLabel;
  }

  /**
   * Parses a form value string to an OutcomeType enum.
   *
   * @param formValue the form value (e.g., "reduced-to-fixed-fee-assessed")
   * @return the corresponding OutcomeType, or null if not found
   */
  public static OutcomeType fromFormValue(String formValue) {
    if (formValue == null) {
      return null;
    }
    for (OutcomeType type : values()) {
      if (type.formValue.equals(formValue)) {
        return type;
      }
    }
    return null;
  }

  public static OutcomeType fromCsvLabel(String csvLabel) {
    if (csvLabel == null) {
      return null;
    }
    if (csvLabel.equalsIgnoreCase("Reduced to Fixed Fee")
        || csvLabel.equalsIgnoreCase("Reduced to Fixed Fee - Assessed")) {
      return REDUCED_TO_FIXED_FEE;
    }
    for (OutcomeType outcome : values()) {
      if (outcome.getCsvLabel().equalsIgnoreCase(csvLabel)) {
        return outcome;
      }
    }
    return null;
  }
}
