package uk.gov.justice.laa.amend.claim.bulkupload;

import java.io.Serializable;

public record BulkUploadError(Integer rowNumber, String message) implements Serializable {}
