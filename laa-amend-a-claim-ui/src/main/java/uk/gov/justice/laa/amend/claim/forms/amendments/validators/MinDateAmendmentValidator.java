package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.time.LocalDate;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.utils.DateWrapperUtil;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

/**
 * Base for date validators that only reject dates before a fixed earliest-allowed date or in the
 * future, with no submission-period dependency.
 */
public abstract class MinDateAmendmentValidator extends AmendmentDateValidator {

  private final DateWrapperUtil dateWrapperUtil;

  protected MinDateAmendmentValidator(
      MessageSource messageSource, DateWrapperUtil dateWrapperUtil) {
    super(messageSource);
    this.dateWrapperUtil = dateWrapperUtil;
  }

  protected abstract LocalDate earliestAllowedDate();

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var date = form.getDateValue(field.name());

    if (date == null) {
      return;
    }

    if (dateWrapperUtil.isFutureDate(date)) {
      addDateInTheFutureMessage(errors, field);
    } else if (date.isBefore(earliestAllowedDate())) {
      addDateTooEarlyMessage(errors, field, earliestAllowedDate());
    }
  }
}
