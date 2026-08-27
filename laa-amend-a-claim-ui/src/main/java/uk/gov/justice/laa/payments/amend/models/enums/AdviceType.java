package uk.gov.justice.laa.payments.amend.models.enums;

import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.FieldOption;

public enum AdviceType implements FieldOption {
  FACE_TO_FACE("FTF"),
  REMOTELY("REM");

  private final String value;

  AdviceType(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
