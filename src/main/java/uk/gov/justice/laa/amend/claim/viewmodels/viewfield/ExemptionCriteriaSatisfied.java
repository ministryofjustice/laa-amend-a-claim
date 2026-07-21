package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum ExemptionCriteriaSatisfied implements FieldOption {
  DV001("DV001"),
  DV002("DV002"),
  DV003("DV003"),
  DV004("DV004"),
  DV005("DV005"),
  DV006("DV006"),
  DV007("DV007"),
  DV008("DV008"),
  DV009("DV009"),
  DV010("DV010"),
  DV011("DV011"),
  DV012("DV012"),
  DV013("DV013"),
  DV014("DV014"),
  DV015("DV015"),
  DV016("DV016"),
  DV017("DV017"),
  DV018("DV018"),
  DV019("DV019"),
  CA001("CA001"),
  CA002("CA002"),
  CA003("CA003"),
  CA004("CA004"),
  CA005("CA005"),
  CA006("CA006"),
  CA007("CA007"),
  CA008("CA008"),
  TR001("TR001"),
  CN001("CN001"),
  UA001("UA001");

  private final String value;

  ExemptionCriteriaSatisfied(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
