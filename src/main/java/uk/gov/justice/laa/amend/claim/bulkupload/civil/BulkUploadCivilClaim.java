package uk.gov.justice.laa.amend.claim.bulkupload.civil;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.MAX_CURRENCY;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.MIN_CURRENCY;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.OFFICE_CODE_REGEX;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.UNIQUE_FILE_NUMBER_CHARACTER_REGEX;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.UNIQUE_FILE_NUMBER_FORMAT_REGEX;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadError;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

@Data
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

  public void setOfficeCode(String officeCode) {
    this.officeCode = officeCode == null ? null : officeCode.toUpperCase();
  }

  public List<BulkUploadError> validate() {
    List<BulkUploadError> errors = new ArrayList<>();

    if (StringUtils.isBlank(officeCode)
        || officeCode.length() != 6
        || !officeCode.matches(OFFICE_CODE_REGEX)) {
      errors.add(
          new BulkUploadError(rowNumber, String.format("Invalid office code %s", officeCode)));
    }

    if (StringUtils.isBlank(ufn)
        || !ufn.matches(UNIQUE_FILE_NUMBER_CHARACTER_REGEX)
        || !ufn.matches(UNIQUE_FILE_NUMBER_FORMAT_REGEX)) {
      errors.add(new BulkUploadError(rowNumber, String.format("Invalid UFN %s", ufn)));
    }

    if (OutcomeType.fromCsvLabel(assessmentOutcome) == null) {
      errors.add(
          new BulkUploadError(
              rowNumber, String.format("Invalid Assessment Outcome %s", assessmentOutcome)));
    }

    validateCurrency(profitCost, "Profit Cost", errors);
    validateCurrency(disbursements, "Disbursements", errors);
    validateCurrency(disbursementsVat, "Disbursements VAT", errors);
    validateCurrency(counselCosts, "Counsel Costs", errors);
    validateCurrency(totalAllowedVat, "Total Allowed VAT", errors);
    validateCurrency(totalAllowedInclVat, "Total Allowed Incl VAT", errors);

    return errors;
  }

  private void validateCurrency(BigDecimal value, String fieldName, List<BulkUploadError> errors) {
    String prefix = String.format("Invalid %s", fieldName);

    if (value == null) {
      errors.add(new BulkUploadError(rowNumber, prefix));
      return;
    }

    if (value.scale() > 2) {
      errors.add(
          new BulkUploadError(
              rowNumber, String.format("%s must be a number with up to 2 decimal places", prefix)));
    }

    if (value.compareTo(MIN_CURRENCY) < 0) {
      errors.add(new BulkUploadError(rowNumber, String.format("%s must not be negative", prefix)));
    }

    if (value.compareTo(MAX_CURRENCY) >= 0) {
      errors.add(
          new BulkUploadError(
              rowNumber, String.format("%s must be less than %s", prefix, MAX_CURRENCY)));
    }
  }
}
