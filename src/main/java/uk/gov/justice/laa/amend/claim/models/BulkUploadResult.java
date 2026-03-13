package uk.gov.justice.laa.amend.claim.models;

import java.util.List;

public record BulkUploadResult(BulkUploadStatus status, List<String> reasons) {

    public enum BulkUploadStatus {
        SUCCESS,
        PARSING_FAILURE,
        VALIDATION_FAILURE,
        SUBMISSION_FAILURE
    }
}
