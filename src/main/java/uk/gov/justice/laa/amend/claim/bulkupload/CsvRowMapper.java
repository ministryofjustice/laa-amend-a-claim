package uk.gov.justice.laa.amend.claim.bulkupload;

import java.util.List;
import org.apache.commons.csv.CSVRecord;

public interface CsvRowMapper<T> {
  T mapRow(CSVRecord record, int rowNumber, List<BulkUploadError> errors);
}
