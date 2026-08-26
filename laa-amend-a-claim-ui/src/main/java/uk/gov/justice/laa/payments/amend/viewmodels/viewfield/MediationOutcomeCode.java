package uk.gov.justice.laa.payments.amend.viewmodels.viewfield;

public enum MediationOutcomeCode implements FieldOption {
  A,
  B,
  S,
  C,
  P;

  @Override
  public String value() {
    return name();
  }

  @Override
  public String messageKeyPrefix() {
    return "outcomeCode";
  }
}
