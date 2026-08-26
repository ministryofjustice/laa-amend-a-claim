package uk.gov.justice.laa.payments.amend.bulkupload;

public record CsvField(String key, String displayName, boolean required, Class<?> type) {}
