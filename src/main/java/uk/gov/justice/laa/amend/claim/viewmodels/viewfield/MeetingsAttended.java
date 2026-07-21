package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum MeetingsAttended implements FieldOption {
  MTGA01("MTGA01"),
  MTGA02("MTGA02"),
  MTGA03("MTGA03"),
  MTGA04("MTGA04"),
  MTGA05("MTGA05"),
  MTGA06("MTGA06"),
  MTGA07("MTGA07"),
  MTGA08("MTGA08"),
  MTGA09("MTGA09"),
  MTGA10("MTGA10"),
  MTGA11("MTGA11"),
  MTGA12("MTGA12"),
  MTGA13("MTGA13"),
  MTGA14("MTGA14"),
  MTGA15("MTGA15"),
  MTGA16("MTGA16"),
  MTGA17("MTGA17"),
  MTGA18("MTGA18"),
  MTGA19("MTGA19"),
  MTGA20("MTGA20"),
  MTGA21("MTGA21"),
  MTGA22("MTGA22"),
  MTGA23("MTGA23"),
  MTGA24("MTGA24");

  private final String value;

  MeetingsAttended(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
