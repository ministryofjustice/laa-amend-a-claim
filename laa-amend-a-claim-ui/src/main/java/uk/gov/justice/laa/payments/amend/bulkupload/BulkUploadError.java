package uk.gov.justice.laa.payments.amend.bulkupload;

public record BulkUploadError(Integer rowNumber, String message) {}
