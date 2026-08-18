package uk.gov.justice.laa.amend.claim.forms.amendments.validators.unique;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

@RequiredArgsConstructor
public abstract class AmendmentDateValidator implements FieldSpecificAmendmentValidator {

  private final MessageSource messageSource;

  private static final String DATE_FORMAT_D_MMM_YYYY = "d MMMM yyyy";
  private static final DateTimeFormatter DATE_FORMATTER_D_MMM_YYYY =
      DateTimeFormatter.ofPattern(DATE_FORMAT_D_MMM_YYYY);

  protected void addDateTooEarlyMessage(Errors errors, ClaimViewField<?> field,
      LocalDate earliestDate) {
    addUniqueFieldError(
        field,
        DATE_CANT_BE_BEFORE_CODE,
        new Object[] {
            field.label(messageSource),
            DATE_FORMATTER_D_MMM_YYYY.format(earliestDate)
        },
        errors);
  }
}
