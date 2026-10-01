package uk.gov.justice.laa.payments.amend.models.enums;

import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.FieldOption;

public enum ClaExemptionCode implements FieldOption {
  CLIENT_IS_A_CHILD("ECHI"),
  TWELVE_MONTH_EXEMPTION("EPRE"),
  CLIENT_IS_IN_DETENTION("EDET");

  private final String value;

  ClaExemptionCode(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
