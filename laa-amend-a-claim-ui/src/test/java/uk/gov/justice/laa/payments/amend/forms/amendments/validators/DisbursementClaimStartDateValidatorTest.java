package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationType;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.support.TestMessageSources;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;

class DisbursementClaimStartDateValidatorTest {

  ClaimDetails claimDetails;
  DisbursementClaimStartDateValidator validator;

  @BeforeEach
  void beforeEach() {
    claimDetails = MockClaimsFunctions.createMockCivilClaim();
    claimDetails.setFeeType(FeeCalculationType.DISB_ONLY);

    validator = new DisbursementClaimStartDateValidator(TestMessageSources.real());
  }

  @Test
  void testAppliesTo() {
    assertTrue(validator.appliesTo(ClaimDetailsViewField.CASE_START_DATE));
  }

  @ParameterizedTest
  @MethodSource(
      "uk.gov.justice.laa.payments.amend.forms.amendments.validators"
          + ".InvalidDateArgumentsProvider#invalidDateProvider")
  void shouldNotAddErrorsForInvalidDates(String day, String month, String year) {
    Map<String, String> input =
        Map.of(
            "CASE_START_DATE-day", day,
            "CASE_START_DATE-month", month,
            "CASE_START_DATE-year", year);

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @Test
  void shouldNotAddErrorsWhenSubmissionPeriodMissing() {
    claimDetails.setSubmissionPeriod(null);
    Map<String, String> input =
        Map.of(
            "CASE_START_DATE-day", "1",
            "CASE_START_DATE-month", "2",
            "CASE_START_DATE-year", "2020");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @ParameterizedTest
  @EnumSource(
      value = FeeCalculationType.class,
      names = {"DISB_ONLY"},
      mode = EnumSource.Mode.EXCLUDE)
  void shouldNotAddErrorsWhenFeeCalculationTypeNotDisbursement(
      FeeCalculationType feeCalculationType) {
    claimDetails.setFeeType(feeCalculationType);
    Map<String, String> input =
        Map.of(
            "CASE_START_DATE-day", "1",
            "CASE_START_DATE-month", "2",
            "CASE_START_DATE-year", "2020");

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({
    "2025-11-07, 2026-01", // caseStartDate + 3 months = 2026-02-07 < 2026-02-20
    "2025-01-20, 2025-03", // caseStartDate + 3 months = 2025-04-20 = 2025-04-20
    "2025-01-01, 2025-04", // caseStartDate + 3 months = 2025-04-01 < 2025-05-20
    "2025-01-31, 2025-04",
    "2025-01-01, 2025-05",
    "2025-01-10, 2025-05",
    "2025-01-31, 2025-05",
    "2025-02-28, 2025-05",
    "2024-11-30, 2025-02",
    "2024-01-20, 2024-03", // caseStartDate + 3 months = 2024-04-20 = 2024-04-20 (leap year)
    "2024-01-19, 2024-03", // caseStartDate + 3 months = 2024-04-19 < 2024-04-20 (leap year)
    "2024-01-31, 2024-04",
    "2024-01-30, 2024-04"
  })
  void shouldPassValidationWhenCaseStartDateInRange(
      LocalDate caseStartDate, YearMonth submissionPeriod) {
    claimDetails.setSubmissionPeriod(submissionPeriod);
    Map<String, String> input =
        Map.of(
            "CASE_START_DATE-day", String.valueOf(caseStartDate.getDayOfMonth()),
            "CASE_START_DATE-month", String.valueOf(caseStartDate.getMonthValue()),
            "CASE_START_DATE-year", String.valueOf(caseStartDate.getYear()));

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({
    "2025-01-10, 2025-02", // caseStartDate + 3 months = 2025-04-10 > 2025-03-20
    "2025-01-21, 2025-03", // caseStartDate + 3 months = 2025-04-21 > 2025-04-20
    "2025-01-31, 2025-03", // caseStartDate + 3 months = 2025-04-31 > 2025-04-20
    "2025-02-21, 2025-04",
    "2024-12-21, 2025-02",
    "2024-01-31, 2024-03",
    "2024-01-21, 2024-03", // caseStartDate + 3 months = 2024-04-21 > 2024-04-20 (leap year)
    "2024-02-29, 2024-04",
    "2024-02-21, 2024-04"
  })
  void shouldFailValidationWhenCaseStartDateLessThan3Months(
      LocalDate caseStartDate, YearMonth submissionPeriod) {
    claimDetails.setSubmissionPeriod(submissionPeriod);
    Map<String, String> input =
        Map.of(
            "CASE_START_DATE-day", String.valueOf(caseStartDate.getDayOfMonth()),
            "CASE_START_DATE-month", String.valueOf(caseStartDate.getMonthValue()),
            "CASE_START_DATE-year", String.valueOf(caseStartDate.getYear()));

    Errors result = validate(input);

    assertThat(result.hasFieldErrors()).isTrue();
    assertThat(result.getFieldError("inputs['CASE_START_DATE']").getCode())
        .isEqualTo(DisbursementClaimStartDateValidator.DISBURSEMENT_DATE_TO_EARLY);
    assertThat(result.getFieldError("inputs['CASE_START_DATE']").getArguments()[0])
        .isEqualTo(DisbursementClaimStartDateValidator.MAXIMUM_MONTHS_DIFFERENCE);
    LocalDate cutOffDateForSubmissionPeriod = submissionPeriod.atDay(20).plusMonths(1);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
    assertThat(result.getFieldError("inputs['CASE_START_DATE']").getArguments()[1])
        .isEqualTo(dateTimeFormatter.format(cutOffDateForSubmissionPeriod));
  }

  private Errors validate(Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(claimDetails, ClaimDetailsViewField.CASE_START_DATE, form, errors);
    return errors;
  }
}
