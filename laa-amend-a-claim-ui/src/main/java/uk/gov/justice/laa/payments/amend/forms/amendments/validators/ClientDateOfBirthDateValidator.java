package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import java.time.LocalDate;
import java.time.Month;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.payments.amend.utils.DateWrapperUtil;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField;

@Component
public class ClientDateOfBirthDateValidator extends MinDateAmendmentValidator {

  private static final LocalDate OLDEST_DATE_ALLOWED = LocalDate.of(1900, Month.JANUARY, 1);

  public ClientDateOfBirthDateValidator(
      MessageSource messageSource, DateWrapperUtil dateWrapperUtil) {
    super(messageSource, dateWrapperUtil);
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return CivilClaimDetailsViewField.DATE_OF_BIRTH.equals(field)
        || MediationClaimDetailsViewField.DATE_OF_BIRTH.equals(field)
        || MediationClaimDetailsViewField.CLIENT_2_DATE_OF_BIRTH.equals(field);
  }

  @Override
  protected LocalDate earliestAllowedDate() {
    return OLDEST_DATE_ALLOWED;
  }
}
