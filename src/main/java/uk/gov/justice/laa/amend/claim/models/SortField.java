package uk.gov.justice.laa.amend.claim.models;

import java.util.Arrays;
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
        return Arrays.stream(values())
                .filter(sortField -> sortField.value.equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
