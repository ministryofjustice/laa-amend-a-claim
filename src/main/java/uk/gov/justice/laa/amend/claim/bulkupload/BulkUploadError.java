package uk.gov.justice.laa.amend.claim.bulkupload;

public record BulkUploadError(Integer rowNumber, String message) {}
