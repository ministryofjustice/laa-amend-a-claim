package uk.gov.justice.laa.amend.claim.models;

import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.PARSING_FAILURE;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUBMISSION_FAILURE;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.VALIDATION_FAILURE;

import java.util.EnumSet;
import java.util.List;
import uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadError;

public record BulkUploadResult(BulkUploadStatus status, List<BulkUploadError> errors) {

    public enum BulkUploadStatus {
        SUCCESS,
        PARSING_FAILURE,
        VALIDATION_FAILURE,
        SUBMISSION_FAILURE
    }

    public boolean isSuccess() {
        return status == BulkUploadStatus.SUCCESS;
    }

    public boolean isError() {
        return EnumSet.of(PARSING_FAILURE, VALIDATION_FAILURE, SUBMISSION_FAILURE)
                .contains(status);
    }
}
