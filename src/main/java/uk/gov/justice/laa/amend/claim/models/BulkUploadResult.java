package uk.gov.justice.laa.amend.claim.models;

import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.PARSING_FAILURE;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUBMISSION_FAILURE;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.VALIDATION_FAILURE;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;
import uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadError;

public record BulkUploadResult(
    BulkUploadStatus status,
    List<BulkUploadError> errors,
    List<BulkUploadAssessmentSummary> uploadedAssessments)
    implements Serializable {

  public enum BulkUploadStatus {
    SUCCESS,
    PARSING_FAILURE,
    VALIDATION_FAILURE,
    SUBMISSION_FAILURE
  }

  public static BulkUploadResult success(List<BulkUploadAssessmentSummary> uploadedAssessments) {
    return new BulkUploadResult(BulkUploadStatus.SUCCESS, List.of(), uploadedAssessments);
  }

  public static BulkUploadResult failure(BulkUploadStatus status, List<BulkUploadError> errors) {
    return new BulkUploadResult(status, errors, List.of());
  }

  public boolean isSuccess() {
    return status == BulkUploadStatus.SUCCESS;
  }

  public boolean isError() {
    return EnumSet.of(PARSING_FAILURE, VALIDATION_FAILURE, SUBMISSION_FAILURE).contains(status);
  }
}
