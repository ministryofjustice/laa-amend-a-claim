package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;

@Getter
public enum SortDirection {

    ASCENDING("asc"),
    DESCENDING("desc"),
    NONE("none");

    private final String value;

    SortDirection(String value) {
        this.value = value;
    }

    public SortDirection toggle() {
        return switch (this) {
            case ASCENDING -> DESCENDING;
            case DESCENDING, NONE -> ASCENDING;
        };
    }

    public static SortDirection fromValue(String value) {
        return switch (value) {
            case "asc" -> SortDirection.ASCENDING;
            case "desc" -> SortDirection.DESCENDING;
            default -> SortDirection.NONE;
        };
    }
}
