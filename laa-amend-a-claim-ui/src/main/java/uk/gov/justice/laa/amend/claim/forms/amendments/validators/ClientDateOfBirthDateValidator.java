package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.time.LocalDate;
import java.time.Month;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.utils.DateWrapperUtil;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

@Component
public class ClientDateOfBirthDateValidator extends AmendmentDateValidator {

  private static final LocalDate OLDEST_DATE_ALLOWED = LocalDate.of(1900, Month.JANUARY, 1);

  private final DateWrapperUtil dateWrapperUtil;

  public ClientDateOfBirthDateValidator(
      MessageSource messageSource, DateWrapperUtil dateWrapperUtil) {
    super(messageSource);
    this.dateWrapperUtil = dateWrapperUtil;
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return CivilClaimDetailsViewField.DATE_OF_BIRTH.equals(field)
        || MediationClaimDetailsViewField.DATE_OF_BIRTH.equals(field)
        || MediationClaimDetailsViewField.CLIENT_2_DATE_OF_BIRTH.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var dateOfBirth = form.getDateValue(field.name());

    if (dateOfBirth == null) {
      return;
    }

    if (dateOfBirth.isBefore(OLDEST_DATE_ALLOWED)) {
      addDateTooEarlyMessage(errors, field, OLDEST_DATE_ALLOWED);
    } else if (dateWrapperUtil.isFutureDate(dateOfBirth)) {
      addDateInTheFutureMessage(errors, field);
    }
  }
}
