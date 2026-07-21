package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum AitHearingCentre implements FieldOption {
  BIRMINGHAM("01"),
  BRADFORD("02"),
  HARMONDSWORTH("03"),
  LONDON_FIELD_HOUSE("04"),
  LONDON_HATTON_CROSS("05"),
  LONDON_TAYLOR_HOUSE("06"),
  MANCHESTER("07"),
  NEWPORT("08"),
  NORTH_SHIELDS("09"),
  NOTTS("10"),
  STOKE("11"),
  SURBITON("12"),
  WALSALL("13"),
  YARLS_WOOD("14"),
  N_A_APPLICATION_ONLY("15"),
  OTHER("16");

  private final String value;

  AitHearingCentre(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
