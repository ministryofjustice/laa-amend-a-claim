package uk.gov.justice.laa.amend.claim.bulkupload.civil;

import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.ASSESSMENT_OUTCOME;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.COUNSEL_COSTS;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.DISBURSEMENTS;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.DISBURSEMENTS_VAT;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.OFFICE_CODE;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.TOTAL_ALLOWED_INCLUDE_VAT;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.TOTAL_ALLOWED_VAT;
import static uk.gov.justice.laa.amend.claim.bulkupload.civil.CivilClaimSchemaProvider.UFN;

import java.math.BigDecimal;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvRowMapper;
import uk.gov.justice.laa.amend.claim.utils.NumberUtils;

@Component
public class BulkUploadCivilClaimCsvMapper implements CsvRowMapper<BulkUploadCivilClaim> {

    @Override
    public BulkUploadCivilClaim mapRow(CSVRecord record, int rowNumber) {
        BulkUploadCivilClaim claim = new BulkUploadCivilClaim();
        claim.setOfficeCode(getRequiredString(record, OFFICE_CODE, rowNumber));
        claim.setUfn(getRequiredString(record, UFN, rowNumber));
        claim.setAssessmentOutcome(getRequiredString(record, ASSESSMENT_OUTCOME, rowNumber));
        claim.setProfitCost(getOptionalBigDecimal(record, PROFIT_COST, rowNumber));
        claim.setDisbursements(getOptionalBigDecimal(record, DISBURSEMENTS, rowNumber));
        claim.setDisbursementsVat(getOptionalBigDecimal(record, DISBURSEMENTS_VAT, rowNumber));
        claim.setCounselCosts(getOptionalBigDecimal(record, COUNSEL_COSTS, rowNumber));
        claim.setTotalAllowedVat(getOptionalBigDecimal(record, TOTAL_ALLOWED_VAT, rowNumber));
        claim.setTotalAllowedInclVat(getOptionalBigDecimal(record, TOTAL_ALLOWED_INCLUDE_VAT, rowNumber));
        claim.setRowNumber(rowNumber);
        return claim;
    }

    private String getRequiredString(CSVRecord record, String header, int rowNumber) {
        String value = record.get(header);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(header + " is required.");
        }
        return value.trim();
    }

    private BigDecimal getOptionalBigDecimal(CSVRecord record, String header, int rowNumber) {
        try {
            String value = record.get(header);
            if (value == null || value.isBlank()) {
                return null;
            }

            String normalized = value.replaceAll("£", "") // remove currency symbols
                    .trim();

            return NumberUtils.parse(normalized);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid number in " + header);
        }
    }
}
