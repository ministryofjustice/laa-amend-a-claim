package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.time.LocalDate;
import java.time.Month;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.utils.DateWrapperUtil;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

@Component
public class CaseStartDateValidator extends MinDateAmendmentValidator {

  private static final LocalDate OLDEST_DATE_ALLOWED = LocalDate.of(1995, Month.JANUARY, 1);

  public CaseStartDateValidator(MessageSource messageSource, DateWrapperUtil dateWrapperUtil) {
    super(messageSource, dateWrapperUtil);
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return ClaimDetailsViewField.CASE_START_DATE.equals(field);
  }

  @Override
  protected LocalDate earliestAllowedDate() {
    return OLDEST_DATE_ALLOWED;
  }
}
