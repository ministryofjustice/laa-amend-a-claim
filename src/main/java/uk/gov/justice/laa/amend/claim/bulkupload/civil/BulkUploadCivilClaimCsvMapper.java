package uk.gov.justice.laa.amend.claim.bulkupload.civil;

import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.ASSESSMENT_OUTCOME;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.COUNSEL_COSTS;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.DISBURSEMENTS;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.DISBURSEMENTS_VAT;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.OFFICE_CODE;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.TOTAL_ALLOWED_INCLUDING_VAT;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.TOTAL_ALLOWED_VAT;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.UFN;

import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadError;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvRowMapper;
import uk.gov.justice.laa.amend.claim.utils.NumberUtils;

@Slf4j
@Component
public class BulkUploadCivilClaimCsvMapper implements CsvRowMapper<BulkUploadCivilClaim> {

    @Override
    public BulkUploadCivilClaim mapRow(CSVRecord record, int rowNumber, List<BulkUploadError> errors) {
        BulkUploadCivilClaim claim = new BulkUploadCivilClaim();
        claim.setOfficeCode(getRequiredString(record, OFFICE_CODE, rowNumber, errors));
        claim.setUfn(getRequiredString(record, UFN, rowNumber, errors));
        claim.setAssessmentOutcome(getRequiredString(record, ASSESSMENT_OUTCOME, rowNumber, errors));
        claim.setProfitCost(getRequiredBigDecimal(record, PROFIT_COST, rowNumber, errors));
        claim.setDisbursements(getRequiredBigDecimal(record, DISBURSEMENTS, rowNumber, errors));
        claim.setDisbursementsVat(getRequiredBigDecimal(record, DISBURSEMENTS_VAT, rowNumber, errors));
        claim.setCounselCosts(getRequiredBigDecimal(record, COUNSEL_COSTS, rowNumber, errors));
        claim.setTotalAllowedVat(getRequiredBigDecimal(record, TOTAL_ALLOWED_VAT, rowNumber, errors));
        claim.setTotalAllowedInclVat(getRequiredBigDecimal(record, TOTAL_ALLOWED_INCLUDING_VAT, rowNumber, errors));
        claim.setRowNumber(rowNumber);
        return claim;
    }

    private String getRequiredString(CSVRecord record, String header, int rowNumber, List<BulkUploadError> errors) {
        try {
            String value = record.get(header);
            if (value == null || value.isBlank()) {
                errors.add(new BulkUploadError(rowNumber, header + " is required"));
                return null;
            }
            return value.trim();
        } catch (Exception ex) {
            errors.add(new BulkUploadError(rowNumber, "Invalid string in " + header));
            return null;
        }
    }

    private BigDecimal getRequiredBigDecimal(
            CSVRecord record, String header, int rowNumber, List<BulkUploadError> errors) {
        try {
            String value = record.get(header);
            if (value == null || value.isBlank()) {
                errors.add(new BulkUploadError(rowNumber, header + " is required"));
                return null;
            }

            String normalized = value.replaceAll("£", "") // remove currency symbols
                    .trim();

            return NumberUtils.parse(normalized);
        } catch (Exception ex) {
            errors.add(new BulkUploadError(rowNumber, "Invalid number in " + header));
            return null;
        }
    }
}
