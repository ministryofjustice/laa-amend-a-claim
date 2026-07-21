package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum StandardFeeCategory implements FieldOption {
  ONE_A("1A"),
  TWO_A("2A"),
  ONE_B("1B"),
  TWO_B("2B"),
  ONE_A_HSF("1A-HSF"),
  ONE_B_HSF("1B-HSF"),
  ONE_A_LSF("1A-LSF"),
  ONE_B_LSF("1B-LSF"),
  ONE_EW("1EW"),
  ONE_SO("1SO"),
  TWO("2"),
  THREE("3"),
  ULF("ULF"),
  UHF("UHF"),
  CLF("CLF"),
  CHF("CHF"),
  SENDING_HEARING_FIXED_FEE("Sending Hearing Fixed Fee");

  private final String value;

  StandardFeeCategory(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
