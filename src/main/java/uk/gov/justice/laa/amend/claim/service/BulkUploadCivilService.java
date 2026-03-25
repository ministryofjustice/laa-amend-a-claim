package uk.gov.justice.laa.amend.claim.service;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvHeaderValidator;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvRowMapper;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvSchemaProvider;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;

@Component
public class BulkUploadCivilService extends BulkUploadService<BulkUploadCivilClaim> {

    public BulkUploadCivilService(
            CsvSchemaProvider<BulkUploadCivilClaim> schemaProvider,
            CsvRowMapper<BulkUploadCivilClaim> rowMapper,
            CsvHeaderValidator csvHeaderValidator) {
        super(schemaProvider, rowMapper, csvHeaderValidator);
    }
}
