package uk.gov.justice.laa.amend.claim.service;

import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.PARSING_FAILURE;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUCCESS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvHeaderValidator;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvRowMapper;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvSchemaProvider;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;

@RequiredArgsConstructor
@Slf4j
public abstract class BulkUploadService<T> {

    protected final CsvSchemaProvider<?> schemaProvider;
    protected final CsvRowMapper<T> rowMapper;
    protected final CsvHeaderValidator csvHeaderValidator;

    public BulkUploadResult upload(MultipartFile file, UUID userId) {
        List<T> rows = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try {
            parseFile(file, rows, errors);
        } catch (Exception ex) {
            log.error("Error parsing file", ex);
            errors.add(ex.getMessage());
        }
        if (!errors.isEmpty()) {
            return new BulkUploadResult(PARSING_FAILURE, errors);
        }
        log.info("Parsed {} rows from file", rows.size());
        var validationResult = validateRows(rows);
        if (validationResult.status() != SUCCESS) {
            return validationResult;
        }
        return submit(rows, userId);
    }

    private void parseFile(MultipartFile file, List<T> rows, List<String> errors) throws IOException {
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CSVParser parser = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(false)
                    .setTrim(true)
                    .setIgnoreEmptyLines(true)
                    .get()
                    .parse(reader);
            // Header validation
            try {
                csvHeaderValidator.validate(schemaProvider.getSchema(), parser.getHeaderNames());
            } catch (Exception ex) {
                errors.add("Header error: " + ex.getMessage());
                return;
            }
            int row = 1;
            for (CSVRecord record : parser) {
                row++;
                try {
                    rows.add(rowMapper.mapRow(record, row));
                } catch (Exception ex) {
                    errors.add("Row " + row + ": " + ex.getMessage());
                }
            }
        }
    }

    protected BulkUploadResult validateRows(List<T> rows) {
        return new BulkUploadResult(SUCCESS, List.of("Validation Successful for " + rows.size() + " rows"));
    }

    protected BulkUploadResult submit(List<T> rows, UUID userId) {
        return new BulkUploadResult(SUCCESS, List.of("Submission Successful for " + rows.size() + " rows"));
    }
}
