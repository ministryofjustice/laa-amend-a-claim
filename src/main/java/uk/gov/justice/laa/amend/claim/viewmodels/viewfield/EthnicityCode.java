package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum EthnicityCode implements FieldOption {
  WHITE_BRITISH("00"),
  WHITE_IRISH("01"),
  BLACK_OR_BLACK_BRITISH_AFRICAN("02"),
  BLACK_OR_BLACK_BRITISH_CARIBBEAN("03"),
  BLACK_OR_BLACK_BRITISH_OTHER("04"),
  ASIAN_OR_ASIAN_BRITISH_INDIAN("05"),
  ASIAN_OR_ASIAN_BRITISH_PAKISTANI("06"),
  ASIAN_OR_ASIAN_BRITISH_BANGLADESHI("07"),
  CHINESE("08"),
  MIXED_WHITE_AND_BLACK_CARIBBEAN("09"),
  MIXED_WHITE_AND_BLACK_AFRICAN("10"),
  MIXED_WHITE_AND_ASIAN("11"),
  MIXED_OTHER("12"),
  OTHER_ETHNIC_GROUP("13"),
  GYPSY_OR_IRISH_TRAVELLER("14"),
  ARAB("15"),
  ANY_OTHER_ETHNIC_BACKGROUND("16"),
  UNKNOWN("99");

  private final String value;

  EthnicityCode(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
