package uk.gov.justice.laa.amend.claim.bulkupload;

public record CsvField(String key, String displayName, boolean required, Class<?> type) {}
