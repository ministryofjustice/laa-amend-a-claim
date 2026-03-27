package uk.gov.justice.laa.amend.claim.bulkupload.civil;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.MAX;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.MIN;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.OFFICE_CODE_REGEX;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.UNIQUE_FILE_NUMBER_CHARACTER_REGEX;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadCivilClaim {
    private String officeCode;
    private String ufn;
    private String assessmentOutcome;
    private BigDecimal profitCost;
    private BigDecimal disbursements;
    private BigDecimal disbursementsVat;
    private BigDecimal counselCosts;
    private BigDecimal totalAllowedVat;
    private BigDecimal totalAllowedInclVat;
    private int rowNumber;

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(officeCode) || officeCode.length() != 6 || !officeCode.matches(OFFICE_CODE_REGEX)) {
            errors.add("Row " + rowNumber + ": Invalid office code: " + officeCode);
        }

        if (StringUtils.isBlank(ufn) || !ufn.matches(UNIQUE_FILE_NUMBER_CHARACTER_REGEX)) {
            errors.add("Row " + rowNumber + ": Invalid UFN: " + ufn);
        }

        validateCurrency(profitCost, "Profit Cost", errors);
        validateCurrency(disbursements, "Disbursements", errors);
        validateCurrency(disbursementsVat, "Disbursements VAT", errors);
        validateCurrency(counselCosts, "Counsel Costs", errors);
        validateCurrency(totalAllowedVat, "Total Allowed VAT", errors);
        validateCurrency(totalAllowedInclVat, "Total Allowed Incl VAT", errors);

        return errors;
    }

    private void validateCurrency(BigDecimal value, String fieldName, List<String> errors) {
        String prefix = String.format("Row %d: Invalid %s", rowNumber, fieldName);

        if (value == null) {
            errors.add(prefix);
            return;
        }

        if (value.scale() > 2) {
            errors.add(String.format("%s must be a number with up to 2 decimal places", prefix));
        }

        if (value.compareTo(MIN) < 0) {
            errors.add(String.format("%s must not be negative", prefix));
        }

        if (value.compareTo(MAX) >= 0) {
            errors.add(String.format("%s must be less than %s", prefix, MAX));
        }
    }
}
