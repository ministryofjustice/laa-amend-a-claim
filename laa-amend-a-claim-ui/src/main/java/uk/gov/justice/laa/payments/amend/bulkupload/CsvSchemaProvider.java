package uk.gov.justice.laa.payments.amend.bulkupload;

public interface CsvSchemaProvider<T> {
  CsvSchema getSchema();
}
