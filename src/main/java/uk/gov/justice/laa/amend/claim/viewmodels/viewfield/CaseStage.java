package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum CaseStage implements FieldOption {
  FPL01("FPL01"),
  FPL02("FPL02"),
  FPL03("FPL03"),
  FPL04("FPL04"),
  FPL05("FPL05"),
  FPL06("FPL06"),
  FPL07("FPL07"),
  FPL08("FPL08"),
  FPL09("FPL09"),
  FPL10("FPL10"),
  FPL11("FPL11"),
  FPL12("FPL12"),
  FPL13("FPL13"),
  FPL14("FPL14"),
  FPL15("FPL15"),
  FPL16("FPL16"),
  FPL17("FPL17"),
  FPL18("FPL18"),
  FPL19("FPL19"),
  FPL20("FPL20"),
  FPL21("FPL21"),
  FPC01("FPC01"),
  FPC02("FPC02"),
  FPC03("FPC03"),
  MHL01("MHL01"),
  MHL02("MHL02"),
  MHL03("MHL03"),
  MHL04("MHL04"),
  MHL05("MHL05"),
  MHL06("MHL06"),
  MHL07("MHL07"),
  MHL08("MHL08"),
  MHL09("MHL09"),
  MHL10("MHL10");

  private final String value;

  CaseStage(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
