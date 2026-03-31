package uk.gov.justice.laa.amend.claim.service;

import static uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadHelper.MAX_ROWS;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.PARSING_FAILURE;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUBMISSION_FAILURE;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvHeaderValidator;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvRowMapper;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvSchemaProvider;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.models.BulkUploadValidationOutcome;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

@RequiredArgsConstructor
@Slf4j
public abstract class BulkUploadService<T> {

    private static final int ROW_OFFSET = 2;
    protected final CsvSchemaProvider<T> schemaProvider;
    protected final CsvRowMapper<T> rowMapper;
    protected final CsvHeaderValidator csvHeaderValidator;

    private final AssessmentService assessmentService;

    public BulkUploadResult upload(MultipartFile file, UUID userId) {
        List<T> rows = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try {
            parseFile(file, rows, errors);
        } catch (Exception ex) {
            log.error("Error parsing file", ex);
            errors.add(StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : "Error parsing file");
        }
        if (!errors.isEmpty()) {
            return new BulkUploadResult(PARSING_FAILURE, errors);
        }
        log.info("Parsed {} rows from file", rows.size());

        if (rows.size() > MAX_ROWS) {
            return new BulkUploadResult(
                    PARSING_FAILURE, List.of("File contains too many rows. Maximum allowed is " + MAX_ROWS));
        }

        var validationOutcome = validateRows(rows);
        if (validationOutcome.result().status() != SUCCESS) {
            return validationOutcome.result();
        }

        return submit(validationOutcome.claimDetailsList(), userId);
    }

    private void parseFile(MultipartFile file, List<T> rows, List<String> errors) throws IOException {
        try (BufferedReader reader =
                        new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                CSVParser parser = CSVFormat.DEFAULT
                        .builder()
                        .setHeader()
                        .setIgnoreHeaderCase(true)
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .setIgnoreEmptyLines(true)
                        .get()
                        .parse(reader)) {

            // Header validation
            try {
                csvHeaderValidator.validate(schemaProvider.getSchema(), parser.getHeaderNames());
            } catch (Exception ex) {
                errors.add(ex.getMessage());
                return;
            }

            int row = 1;
            for (CSVRecord record : parser) {
                row++;
                try {
                    rows.add(rowMapper.mapRow(record, row));
                } catch (Exception ex) {
                    errors.add(ex.getMessage());
                }
            }
        }
    }

    protected abstract BulkUploadValidationOutcome validateRows(List<T> rows);

    protected BulkUploadResult submit(List<? extends ClaimDetails> claimDetails, UUID userId) {
        for (int row = 0; row < claimDetails.size(); ++row) {
            try {
                var claim = claimDetails.get(row);
                log.info(
                        "Bulk upload in progress. Submitting assessment for row {}, claim {}, UFN {}",
                        row + ROW_OFFSET,
                        claim.getClaimId(),
                        claim.getUniqueFileNumber());
                assessmentService.submitAssessment(claim, userId.toString());
            } catch (Exception ex) {
                var message = String.format(
                        "Row %s: Failed to submit assessment. %s prior rows in the file have already been"
                                + " processed and do not need to be reuploaded.",
                        row + ROW_OFFSET, row);
                log.error(message, ex);
                return new BulkUploadResult(SUBMISSION_FAILURE, List.of(message));
            }
        }
        var successMessage = String.format("Successfully uploaded %s assessments", claimDetails.size());
        log.info(successMessage);
        return new BulkUploadResult(SUCCESS, List.of(successMessage));
    }
}
