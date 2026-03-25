package uk.gov.justice.laa.amend.claim.bulkupload;

import org.apache.commons.csv.CSVRecord;

public interface CsvRowMapper<T> {
    T mapRow(CSVRecord record, int rowNumber);
}
