package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;

@Getter
public enum SortField {

    UNIQUE_FILE_NUMBER("uniqueFileNumber"),
    CASE_REFERENCE_NUMBER("caseReferenceNumber"),
    SCHEDULE_REFERENCE("scheduleReference");

    private final String value;

    SortField(String value) {
        this.value = value;
    }

    public static SortField fromValue(String value) {
        return switch (value) {
            case "uniqueFileNumber" -> UNIQUE_FILE_NUMBER;
            case "caseReferenceNumber" -> CASE_REFERENCE_NUMBER;
            case "scheduleReference" -> SCHEDULE_REFERENCE;
            default -> throw new IllegalArgumentException("Could not parse sort field: " + value);
        };
    }
}
