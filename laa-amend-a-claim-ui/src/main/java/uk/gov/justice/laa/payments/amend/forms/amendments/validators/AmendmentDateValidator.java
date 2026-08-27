package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

@RequiredArgsConstructor
public abstract class AmendmentDateValidator implements FieldSpecificAmendmentValidator {

  private final MessageSource messageSource;

  public static final String DATE_CANT_BE_BEFORE_CODE = "amendmentForm.dates.cantBeBefore";
  public static final String DATE_CANT_BE_IN_FUTURE_CODE = "amendmentForm.dates.cantBeInFuture";
  public static final String DATE_CANT_BE_LATER_THAN_SUBMISSION_PERIOD_CODE =
      "amendmentForm.dates.cantBeLaterThanSubmissionPeriod";

  private static final String DATE_FORMAT_D_MMM_YYYY = "d MMMM yyyy";
  protected static final DateTimeFormatter DATE_FORMATTER_D_MMM_YYYY =
      DateTimeFormatter.ofPattern(DATE_FORMAT_D_MMM_YYYY);

  protected void addDateTooEarlyMessage(
      Errors errors, ClaimViewField<?> field, LocalDate earliestDate) {
    addUniqueFieldError(
        field,
        DATE_CANT_BE_BEFORE_CODE,
        new Object[] {field.label(messageSource), DATE_FORMATTER_D_MMM_YYYY.format(earliestDate)},
        errors);
  }

  protected void addDateInTheFutureMessage(Errors errors, ClaimViewField<?> field) {
    addUniqueFieldError(
        field, DATE_CANT_BE_IN_FUTURE_CODE, new Object[] {field.label(messageSource)}, errors);
  }

  protected void addDateOutsideSubmissionPeriodMessage(Errors errors, ClaimViewField<?> field) {
    addUniqueFieldError(
        field,
        DATE_CANT_BE_LATER_THAN_SUBMISSION_PERIOD_CODE,
        new Object[] {field.label(messageSource)},
        errors);
  }

  protected final LocalDate getSubmissionPeriodCutoffDate(@NotNull YearMonth submissionPeriod) {
    return submissionPeriod.plusMonths(1).atDay(20);
  }
}
