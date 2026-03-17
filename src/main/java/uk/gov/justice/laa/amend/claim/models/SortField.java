package uk.gov.justice.laa.amend.claim.models;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum SortField {
    UNIQUE_FILE_NUMBER("unique_file_number"),
    CASE_REFERENCE_NUMBER("case_reference_number"),
    CLIENT_SURNAME("client_surname"),
    SUBMISSION_PERIOD("submission_period"),
    CATEGORY_OF_LAW("category_of_law"),
    VOIDED("status");

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
