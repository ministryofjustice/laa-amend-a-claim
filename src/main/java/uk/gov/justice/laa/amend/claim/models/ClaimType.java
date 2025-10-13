package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClaimType {
    ESCAPE("Escape type"),
    NORMAL("Normal claim");
    private final String description;
}
