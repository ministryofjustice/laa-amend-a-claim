package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

@Component
public class CaseConcludedDateValidator extends AmendmentDateValidator {

  private static final LocalDate EARLIEST_CASE_CONCLUDED_DATE_ALLOWED =
      LocalDate.of(2013, Month.APRIL, 1);
  private static final LocalDate EARLIEST_CRIME_CASE_CONCLUDED_DATE_ALLOWED =
      LocalDate.of(2016, Month.APRIL, 1);

  public CaseConcludedDateValidator(MessageSource messageSource) {
    super(messageSource);
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return CivilClaimDetailsViewField.CASE_CONCLUDED_CLAIMED_DATE.equals(field)
        || CrimeClaimDetailsViewField.CASE_CONCLUDED_DATE.equals(field)
        || MediationClaimDetailsViewField.CASE_CONCLUDED_DATE.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var caseConcludedDate = form.getDateValue(field.name());
    var earliestDate =
        AreaOfLaw.CRIME_LOWER.equals(claim.getAreaOfLaw())
            ? EARLIEST_CRIME_CASE_CONCLUDED_DATE_ALLOWED
            : EARLIEST_CASE_CONCLUDED_DATE_ALLOWED;
    if (caseConcludedDate == null) {
      return;
    }

    if (claim.getSubmissionPeriod() == null) {
      throw new IllegalArgumentException(
          "Claim is missing submission period: " + claim.getClaimId());
    }

    var twentiethOfNextMonth = getTwentiethOfNextMonth(claim.getSubmissionPeriod());
    if (caseConcludedDate.isAfter(LocalDate.now(ZoneId.systemDefault()))) {
      addDateInTheFutureMessage(errors, field);
    } else if (caseConcludedDate.isBefore(earliestDate)) {
      addDateTooEarlyMessage(errors, field, earliestDate);
    } else if (caseConcludedDate.isAfter(twentiethOfNextMonth)) {
      addDateOutsideSubmissionPeriodMessage(errors, field);
    }
  }
}
