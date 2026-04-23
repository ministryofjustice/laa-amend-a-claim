package uk.gov.justice.laa.amend.claim.bulkupload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;

class BulkUploadCivilClaimTest {

  private BulkUploadCivilClaim newValidRow() {
    BulkUploadCivilClaim row = new BulkUploadCivilClaim();
    row.setRowNumber(5);
    row.setOfficeCode("ABC123");
    row.setUfn("123456/001");
    row.setAssessmentOutcome("Reduced"); // assume valid
    row.setProfitCost(new BigDecimal("10.00"));
    row.setDisbursements(new BigDecimal("5.50"));
    row.setDisbursementsVat(new BigDecimal("1.00"));
    row.setCounselCosts(new BigDecimal("2.00"));
    row.setTotalAllowedVat(new BigDecimal("3.00"));
    row.setTotalAllowedInclVat(new BigDecimal("15.00"));
    return row;
  }

  @Test
  void validateShouldReturnNoErrorsForValidRow() {
    BulkUploadCivilClaim row = newValidRow();

    List<BulkUploadError> errors = row.validate();

    assertTrue(errors.isEmpty(), "Expected no validation errors");
  }

  @Test
  void validateShouldFailWhenOfficeCodeBlank() {
    BulkUploadCivilClaim row = newValidRow();
    row.setOfficeCode("");

    List<BulkUploadError> errors = row.validate();

    assertEquals(1, errors.size());
    assertTrue(errors.getFirst().message().contains("Invalid office code"));
  }

  @Test
  void validateShouldFailWhenOfficeCodeWrongLength() {
    BulkUploadCivilClaim row = newValidRow();
    row.setOfficeCode("ABC12"); // only 5 chars

    List<BulkUploadError> errors = row.validate();

    assertEquals(1, errors.size());
    assertTrue(errors.getFirst().message().contains("Invalid office code"));
  }

  @Test
  void validateShouldFailWhenOfficeCodeInvalidFormat() {
    BulkUploadCivilClaim row = newValidRow();
    row.setOfficeCode("###123"); // invalid regex

    List<BulkUploadError> errors = row.validate();

    assertEquals(1, errors.size());
    assertTrue(errors.getFirst().message().contains("Invalid office code"));
  }

  @Test
  void validateShouldFailWhenUfnBlank() {
    BulkUploadCivilClaim row = newValidRow();
    row.setUfn("");

    List<BulkUploadError> errors = row.validate();

    assertEquals(1, errors.size());
    assertTrue(errors.getFirst().message().contains("Invalid UFN"));
  }

  @Test
  void validateShouldFailWhenUfnInvalidFormat() {
    BulkUploadCivilClaim row = newValidRow();
    row.setUfn("BAD_UFN");

    List<BulkUploadError> errors = row.validate();

    assertEquals(1, errors.size());
    assertTrue(errors.getFirst().message().contains("Invalid UFN"));
  }

  @Test
  void validateShouldFailWhenCurrencyNull() {
    BulkUploadCivilClaim row = newValidRow();
    row.setProfitCost(null);

    List<BulkUploadError> errors = row.validate();

    assertTrue(errors.stream().anyMatch(e -> e.message().contains("Invalid Profit Cost")));
  }

  @Test
  void validateShouldFailWhenCurrencyHasMoreThanTwoDecimals() {
    BulkUploadCivilClaim row = newValidRow();
    row.setProfitCost(new BigDecimal("10.999"));

    List<BulkUploadError> errors = row.validate();

    assertTrue(
        errors.stream()
            .anyMatch(e -> e.message().contains("must be a number with up to 2 decimal places")));
  }

  @Test
  void validateShouldFailWhenCurrencyIsNegative() {
    BulkUploadCivilClaim row = newValidRow();
    row.setProfitCost(new BigDecimal("-1.00"));

    List<BulkUploadError> errors = row.validate();

    assertTrue(errors.stream().anyMatch(e -> e.message().contains("must not be negative")));
  }

  @Test
  void validateShouldFailWhenCurrencyIsTooLarge() {
    BulkUploadCivilClaim row = newValidRow();
    row.setProfitCost(new BigDecimal("100000000")); // MAX or above

    List<BulkUploadError> errors = row.validate();

    assertTrue(errors.stream().anyMatch(e -> e.message().contains("must be less than")));
  }

  @Test
  void validateShouldReturnMultipleErrors() {
    BulkUploadCivilClaim row = newValidRow();
    row.setOfficeCode("BAD"); // invalid
    row.setUfn("BAD"); // invalid
    row.setProfitCost(new BigDecimal("-1")); // invalid
    row.setDisbursements(null); // invalid

    List<BulkUploadError> errors = row.validate();

    assertTrue(errors.size() >= 4, "Should contain multiple errors");
  }

  @Test
  void validateShouldIncludeRowNumberOnAllErrors() {
    BulkUploadCivilClaim row = newValidRow();
    row.setRowNumber(7);
    row.setOfficeCode("BAD");

    List<BulkUploadError> errors = row.validate();

    assertTrue(errors.stream().allMatch(e -> e.rowNumber() == 7));
  }
}
