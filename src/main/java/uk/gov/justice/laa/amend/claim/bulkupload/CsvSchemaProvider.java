package uk.gov.justice.laa.amend.claim.bulkupload;

public interface CsvSchemaProvider<T> {
  CsvSchema getSchema();
}
