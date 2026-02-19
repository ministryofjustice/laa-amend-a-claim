package uk.gov.justice.laa.amend.claim.models;

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
