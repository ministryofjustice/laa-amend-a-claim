package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClaimType {
    ESCAPE("Escape case", "red"),
    FIXED("Fixed fee", "green"),
    UNKNOWN("Unknown", "grey");
    private final String description;
    private final String tag;
}
