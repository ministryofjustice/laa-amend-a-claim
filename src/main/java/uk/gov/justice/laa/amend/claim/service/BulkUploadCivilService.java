package uk.gov.justice.laa.amend.claim.service;

import static java.util.stream.Collectors.groupingBy;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.ASSESSMENT_REASON_ESCAPE_CASE_CONTINGENCY;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUCCESS;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.VALIDATION_FAILURE;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadError;
import uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadHelper;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvHeaderValidator;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvRowMapper;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvSchemaProvider;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.models.BulkUploadValidationOutcome;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;

@Service
@Slf4j
public class BulkUploadCivilService extends BulkUploadService<BulkUploadCivilClaim> {
    private final BulkUploadHelper bulkUploadHelper;
    private final ClaimMapper claimMapper;

    public BulkUploadCivilService(
            CsvSchemaProvider<BulkUploadCivilClaim> schemaProvider,
            CsvRowMapper<BulkUploadCivilClaim> rowMapper,
            CsvHeaderValidator csvHeaderValidator,
            AssessmentService assessmentService,
            BulkUploadHelper bulkUploadHelper,
            ClaimMapper claimMapper) {
        super(schemaProvider, rowMapper, csvHeaderValidator, assessmentService);
        this.bulkUploadHelper = bulkUploadHelper;
        this.claimMapper = claimMapper;
    }

    /**
     *  Validate rows and return a list of assessed claim details objects in BulkUploadValidationOutcome
     */
    @Override
    protected BulkUploadValidationOutcome validateRows(List<BulkUploadCivilClaim> rows) {
        List<BulkUploadError> errors = new ArrayList<>();
        List<ClaimDetails> claimsData = new ArrayList<>();

        var apiClaimResponseObjects = bulkUploadHelper.getAllClaims(rows, errors);
        var claimsByOfficeCodeAndUfn = apiClaimResponseObjects.stream()
                .collect(groupingBy(claim -> Pair.of(claim.getOfficeCode(), claim.getUniqueFileNumber())));
        for (var row : rows) {
            var claimResponsesForRow = claimsByOfficeCodeAndUfn.get(Pair.of(row.getOfficeCode(), row.getUfn()));

            // Row validation
            List<BulkUploadError> rowErrors = row.validate();
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
            }

            if (claimResponsesForRow == null) {
                errors.add(new BulkUploadError(
                        row.getRowNumber(),
                        String.format(
                                "Escaped Civil Claim not found for UFN %s and officeCode %s",
                                row.getUfn(), row.getOfficeCode())));
                continue;
            }
            if (claimResponsesForRow.size() > 1) {
                errors.add(new BulkUploadError(
                        row.getRowNumber(),
                        String.format(
                                "Duplicate Escaped Civil Claim found for UFN %s and officeCode %s",
                                row.getUfn(), row.getOfficeCode())));
                continue;
            }

            var claimResponseForRow = claimResponsesForRow.getFirst();
            var assessedClaimForRow = mapToAssessedClaim(claimResponseForRow, row);
            claimsData.add(assessedClaimForRow);
        }
        if (!errors.isEmpty()) {
            log.info("Failed validation with {} errors", errors.size());
            return new BulkUploadValidationOutcome(
                    new BulkUploadResult(VALIDATION_FAILURE, sortedByRowNumber(errors), List.of()), List.of());
        } else {
            String message = String.format("Successfully validated %d rows", rows.size());
            log.info(message);
            return new BulkUploadValidationOutcome(
                    new BulkUploadResult(SUCCESS, List.of(new BulkUploadError(null, message)), List.of()), claimsData);
        }
    }

    private ClaimDetails mapToAssessedClaim(ClaimResponseV2 claim, BulkUploadCivilClaim row) {
        var civilClaimDetails = claimMapper.mapToCivilClaimDetails(claim);
        applyRowToClaimDetails(civilClaimDetails, row);
        return civilClaimDetails;
    }

    /**
     * Map Row details into assessed values of ClaimDetails fields
     */
    private void applyRowToClaimDetails(CivilClaimDetails details, BulkUploadCivilClaim row) {
        details.setAssessmentReason(ASSESSMENT_REASON_ESCAPE_CASE_CONTINGENCY);
        details.getNetProfitCost().setAssessed(row.getProfitCost());
        details.getDisbursementVatAmount().setAssessed(row.getDisbursementsVat());
        details.getNetDisbursementAmount().setAssessed(row.getDisbursements());
        details.getCounselsCost().setAssessed(row.getCounselCosts());
        details.getAllowedTotalVat().setAssessed(row.getTotalAllowedVat());
        details.getAssessedTotalVat().setAssessed(row.getTotalAllowedVat());
        details.getAssessedTotalInclVat().setAssessed(row.getTotalAllowedInclVat());
        details.getAllowedTotalInclVat().setAssessed(row.getTotalAllowedInclVat());
        details.setAssessmentOutcome(OutcomeType.fromCsvLabel(row.getAssessmentOutcome()));
    }
}
