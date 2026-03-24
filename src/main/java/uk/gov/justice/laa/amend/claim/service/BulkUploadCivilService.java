package uk.gov.justice.laa.amend.claim.service;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.OFFICE_CODE_REGEX;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.UNIQUE_FILE_NUMBER_CHARACTER_REGEX;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.PARSING_FAILURE;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUCCESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvHeaderValidator;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvRowMapper;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvSchemaProvider;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

@Service
@Slf4j
public class BulkUploadCivilService extends BulkUploadService<BulkUploadCivilClaim> {

    public BulkUploadCivilService(
            CsvSchemaProvider<BulkUploadCivilClaim> schemaProvider,
            CsvRowMapper<BulkUploadCivilClaim> rowMapper,
            CsvHeaderValidator csvHeaderValidator,
            AssessmentService assessmentService
            ClaimService claimService,
            ClaimMapper claimMapper) {
        super(schemaProvider, rowMapper, csvHeaderValidator, claimService, claimMapper, assessmentService);
    }

    /**
     * Validates BulkUploadCivilClaim row
     */
    private static List<String> validateEachRow(BulkUploadCivilClaim row, int lineNumber) {
        List<String> errors = new ArrayList<>();
        String code = row.getOfficeCode();
        String ufn = row.getUfn();

        if (StringUtils.isBlank(code) || code.length() != 6 || !code.matches(OFFICE_CODE_REGEX)) {
            errors.add("Line " + lineNumber + ": Invalid office code: " + code);
        }

        if (StringUtils.isBlank(ufn) || !ufn.matches(UNIQUE_FILE_NUMBER_CHARACTER_REGEX)) {
            errors.add("Line " + lineNumber + ": Invalid UFN: " + ufn);
        }

        if (row.getCounselCosts() == null) {
            errors.add("Line " + lineNumber + ": Invalid Counsel Costs");
        }
        if (OutcomeType.fromCsvLabel(row.getAssessmentOutcome()) == null) {
            errors.add("Line " + lineNumber + ": Invalid assessment result : " + row.getAssessmentOutcome());
        }

        if (row.getDisbursements() == null) {
            errors.add("Line " + lineNumber + ": Invalid Disbursements");
        }
        return errors;
    }

    @Override
    protected BulkUploadResult validateRows(List<BulkUploadCivilClaim> rows) {

        // 1. Get unique office codes
        var officeCodes = rows.stream()
                .filter(Objects::nonNull)
                .map(BulkUploadCivilClaim::getOfficeCode)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        List<String> errors = new ArrayList<>();
        // Validate each row only once, collect errors and build officeCode/ufn map
        var officeCodeToUfnToRowIdx = new HashMap<String, HashMap<String, Integer>>();
        for (int i = 0; i < rows.size(); i++) {
            BulkUploadCivilClaim row = rows.get(i);
            if (row == null) {
                continue;
            }
            List<String> rowErrors = validateEachRow(row, i + 1);
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                continue; // Skip this row for further processing
            }
            String code = row.getOfficeCode();
            String ufn = row.getUfn();
            if (code != null && ufn != null) {
                officeCodeToUfnToRowIdx
                        .computeIfAbsent(code.trim(), k -> new HashMap<>())
                        .put(ufn, i);
            }
        }

        // For each office code, process claims page by page
        for (String officeCode : officeCodes) {
            var ufnToRowIdx = officeCodeToUfnToRowIdx.getOrDefault(officeCode, new HashMap<>());
            // Track which UFNs have been matched
            var matchedUfns = new HashSet<String>();
            int page = 1;
            int size = 400;
            int totalElements;
            do {
                var resultSet = claimService.searchClaims(
                        officeCode,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        page,
                        size,
                        null);
                if (resultSet == null || resultSet.getContent() == null) {
                    break;
                }
                for (var claim : resultSet.getContent()) {
                    String claimUfn = claim.getUniqueFileNumber();
                    if (claimUfn != null && ufnToRowIdx.containsKey(claimUfn)) {
                        int rowIdx = ufnToRowIdx.get(claimUfn);
                        BulkUploadCivilClaim row = rows.get(rowIdx);
                        if (row != null && row.getCivilClaimDetails() == null) {
                            row.setCivilClaimDetails(claimMapper.mapToCivilClaimDetails(claim));
                        }
                        matchedUfns.add(claimUfn);
                    }
                }
                totalElements = resultSet.getTotalElements() != null ? resultSet.getTotalElements() : 0;
                page++;
            } while ((page - 1) * size < totalElements);

            // After all pages, add errors for any unmatched UFNs
            for (var entry : ufnToRowIdx.entrySet()) {
                if (!matchedUfns.contains(entry.getKey())) {
                    errors.add("Line " + (entry.getValue() + 2) + ": UFN " + entry.getKey()
                            + " not found for office code " + officeCode);
                }
            }
        }

        if (!errors.isEmpty()) {
            log.info("Failed validation with {} errors", errors.size());
            return new BulkUploadResult(PARSING_FAILURE, errors);
        } else {
            log.info("Successfully validated {} rows", rows.size());
            String message = "Parsed " + rows.size() + " rows from file";
            return new BulkUploadResult(SUCCESS, List.of(message));
        }
    }
}
