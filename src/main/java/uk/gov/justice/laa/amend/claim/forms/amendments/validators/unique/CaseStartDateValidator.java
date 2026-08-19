package uk.gov.justice.laa.amend.claim.forms.amendments.validators.unique;

import java.time.LocalDate;
import java.time.Month;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

@Component
public class CaseStartDateValidator extends AmendmentDateValidator {

  private static final LocalDate OLDEST_DATE_ALLOWED = LocalDate.of(1995, Month.JANUARY, 1);

  public CaseStartDateValidator(MessageSource messageSource) {
    super(messageSource);
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return ClaimDetailsViewField.CASE_START_DATE.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var caseStartDate = form.getDateValue(ClaimDetailsViewField.CASE_START_DATE.toString());

    if (caseStartDate != null && caseStartDate.isBefore(OLDEST_DATE_ALLOWED)) {
      addDateTooEarlyMessage(errors, ClaimDetailsViewField.CASE_START_DATE, OLDEST_DATE_ALLOWED);
    }
  }
}
