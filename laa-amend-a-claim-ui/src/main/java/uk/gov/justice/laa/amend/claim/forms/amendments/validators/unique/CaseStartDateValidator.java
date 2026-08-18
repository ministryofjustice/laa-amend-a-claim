package uk.gov.justice.laa.amend.claim.forms.amendments.validators.unique;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

@Component
@RequiredArgsConstructor
public class CaseStartDateValidator implements FieldSpecificAmendmentValidator {

  public static final String INVALID_CODE = "amendmentForm.dates.cantBeBefore";

  private static final String DATE_FORMAT_D_MMM_YYYY = "d MMMM yyyy";
  public static final DateTimeFormatter DATE_FORMATTER_D_MMM_YYYY =
      DateTimeFormatter.ofPattern(DATE_FORMAT_D_MMM_YYYY);

  private static final LocalDate OLDEST_DATE_ALLOWED = LocalDate.of(1995, Month.JANUARY, 1);

  private final MessageSource messageSource;

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return ClaimDetailsViewField.CASE_START_DATE.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var caseStartDate = form.getDateValue(ClaimDetailsViewField.CASE_START_DATE.toString());

    if (caseStartDate != null && caseStartDate.isBefore(OLDEST_DATE_ALLOWED)) {
      addUniqueFieldError(
          ClaimDetailsViewField.CASE_START_DATE,
          INVALID_CODE,
          new Object[] {
            field.label(messageSource), DATE_FORMATTER_D_MMM_YYYY.format(OLDEST_DATE_ALLOWED)
          },
          errors);
    }
  }
}
