package uk.gov.justice.laa.amend.claim.forms.amendments.validators.unique;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

@Component
public class CivilCaseConcludedDateValidator extends AmendmentDateValidator {

  private static final LocalDate EARLIEST_CASE_CONCLUDED_DATE_ALLOWED =
      LocalDate.of(2013, Month.APRIL, 1);

  public CivilCaseConcludedDateValidator(MessageSource messageSource) {
    super(messageSource);
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return CivilClaimDetailsViewField.CASE_CONCLUDED_CLAIMED_DATE.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var caseConcludedDate =
        form.getDateValue(CivilClaimDetailsViewField.CASE_CONCLUDED_CLAIMED_DATE.toString());

    if (caseConcludedDate != null
        && caseConcludedDate.isBefore(EARLIEST_CASE_CONCLUDED_DATE_ALLOWED)) {
      addDateTooEarlyMessage(
          errors, CivilClaimDetailsViewField.CASE_CONCLUDED_CLAIMED_DATE,
          EARLIEST_CASE_CONCLUDED_DATE_ALLOWED);
    }
  }
}
