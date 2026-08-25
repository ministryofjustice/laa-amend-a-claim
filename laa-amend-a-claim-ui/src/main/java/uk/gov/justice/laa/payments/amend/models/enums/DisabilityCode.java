package uk.gov.justice.laa.payments.amend.models.enums;

import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.FieldOption;

public enum DisabilityCode implements FieldOption {
  NO_CONDITION_DECLARED("NCD"),
  MOBILITY("MOB"),
  DEAF("DEA"),
  HEARING("HEA"),
  VISUAL("VIS"),
  BLIND("BLI"),
  MENTAL_HEALTH_CONDITION("MHC"),
  LEARNING_DIFFICULTY_DISABILITY("LDD"),
  COGNITIVE_IMPAIRMENT("COG"),
  LONG_TERM_ILLNESS("ILL"),
  OTHER("OTH"),
  UNKNOWN("UKN"),
  PHYSICAL_IMPAIRMENT("PHY"),
  SPECIAL_EDUCATIONAL_NEEDS("SEN");

  private final String value;

  DisabilityCode(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
