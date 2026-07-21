package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum OutcomeCode implements FieldOption {
  CN01("CN01"),
  CN02("CN02"),
  CN03("CN03"),
  CN04("CN04"),
  CN05("CN05"),
  CN06("CN06"),
  CN07("CN07"),
  CN08("CN08"),
  CN09("CN09"),
  CN10("CN10"),
  CN11("CN11"),
  CN12("CN12"),
  CN13("CN13"),
  CP01("CP01"),
  CP02("CP02"),
  CP03("CP03"),
  CP04("CP04"),
  CP05("CP05"),
  CP06("CP06"),
  CP07("CP07"),
  CP08("CP08"),
  CP09("CP09"),
  CP10("CP10"),
  CP11("CP11"),
  CP12("CP12"),
  CP13("CP13"),
  CP14("CP14"),
  CP15("CP15"),
  CP16("CP16"),
  CP17("CP17"),
  CP18("CP18"),
  CP19("CP19"),
  CP20("CP20"),
  CP21("CP21"),
  CP22("CP22"),
  CP23("CP23"),
  CP24("CP24"),
  CP25("CP25"),
  CP26("CP26"),
  CP27("CP27"),
  CP28("CP28"),
  PL01("PL01"),
  PL02("PL02"),
  PL03("PL03"),
  PL04("PL04"),
  PL05("PL05"),
  PL06("PL06"),
  PL07("PL07"),
  PL08("PL08"),
  PL09("PL09"),
  PL10("PL10"),
  PL11("PL11"),
  PL12("PL12"),
  PL13("PL13"),
  PL14("PL14"),
  A("A"),
  B("B"),
  S("S"),
  C("C"),
  P("P");

  private final String value;

  OutcomeCode(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public String messageKeyPrefix() {
    return "outcomeCode";
  }
}
