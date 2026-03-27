package uk.gov.justice.laa.amend.claim.service;

import static java.util.stream.Collectors.groupingBy;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUCCESS;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.VALIDATION_FAILURE;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadHelper;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvHeaderValidator;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvRowMapper;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvSchemaProvider;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.models.BulkUploadValidationOutcome;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

@Service
@Slf4j
public class BulkUploadCivilService extends BulkUploadService<BulkUploadCivilClaim> {
    private final BulkUploadHelper bulkUploadHelper;

    public BulkUploadCivilService(
            CsvSchemaProvider<BulkUploadCivilClaim> schemaProvider,
            CsvRowMapper<BulkUploadCivilClaim> rowMapper,
            CsvHeaderValidator csvHeaderValidator,
            BulkUploadHelper bulkUploadHelper,
            AssessmentService assessmentService) {
        super(schemaProvider, rowMapper, csvHeaderValidator, assessmentService);
        this.bulkUploadHelper = bulkUploadHelper;
    }

    /**
     *  Validate rows and return a list of assessed claim details objects in BulkUploadValidationOutcome
     */
    @Override
    protected BulkUploadValidationOutcome validateRows(List<BulkUploadCivilClaim> rows) {
        List<String> errors = new ArrayList<>();
        List<ClaimDetails> claimsData = new ArrayList<>();

        var apiClaimResponseObjects = bulkUploadHelper.getAllClaims(rows, errors);
        var claimsByOfficeCodeAndUfn = apiClaimResponseObjects.stream()
                .collect(groupingBy(claim -> Pair.of(claim.getOfficeCode(), claim.getUniqueFileNumber())));
        for (var row : rows) {
            var claimResponsesForRow = claimsByOfficeCodeAndUfn.get(Pair.of(row.getOfficeCode(), row.getUfn()));

            // Row validation
            List<String> rowErrors = row.validate();
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
            }

            if (claimResponsesForRow == null) {
                errors.add(String.format(
                        "Row %d: Escaped Civil Claim not found for UFN %s and officeCode %s",
                        row.getRowNumber(), row.getUfn(), row.getOfficeCode()));
                continue;
            }
            if (claimResponsesForRow.size() > 1) {
                errors.add(String.format(
                        "Row %d: Duplicate Escaped Civil Claim found for UFN %s and officeCode %s",
                        row.getRowNumber(), row.getUfn(), row.getOfficeCode()));
                continue;
            }

            var claimResponseForRow = claimResponsesForRow.getFirst();
            var assessedClaimForRow = bulkUploadHelper.mapToAssessedClaim(claimResponseForRow, row);
            claimsData.add(assessedClaimForRow);
        }
        if (!errors.isEmpty()) {
            log.info("Failed validation with {} errors", errors.size());
            return new BulkUploadValidationOutcome(new BulkUploadResult(VALIDATION_FAILURE, errors), List.of());
        } else {
            String message = String.format("Successfully validated %d rows", rows.size());
            log.info(message);
            return new BulkUploadValidationOutcome(new BulkUploadResult(SUCCESS, List.of(message)), claimsData);
        }
    }
}
