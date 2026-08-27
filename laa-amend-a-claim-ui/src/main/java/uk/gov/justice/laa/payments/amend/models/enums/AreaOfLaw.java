package uk.gov.justice.laa.payments.amend.models.enums;

import lombok.Getter;

@Getter
public enum AreaOfLaw {
  CRIME_LOWER("areaOfLaw.crimeLower"),
  LEGAL_HELP("areaOfLaw.legalHelp"),
  MEDIATION("areaOfLaw.mediation");

  private final String messageKey;

  AreaOfLaw(final String messageKey) {
    this.messageKey = messageKey;
  }
}
