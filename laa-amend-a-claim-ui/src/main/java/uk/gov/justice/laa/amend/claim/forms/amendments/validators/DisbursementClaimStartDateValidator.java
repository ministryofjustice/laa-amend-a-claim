package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.time.LocalDate;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationType;

@Component
public class DisbursementClaimStartDateValidator extends AmendmentDateValidator {

  public static final int MAXIMUM_MONTHS_DIFFERENCE = 3;
  public static final String DISBURSEMENT_DATE_TO_EARLY = "amendmentForm.dates.disbursementToEarly";

  public DisbursementClaimStartDateValidator(MessageSource messageSource) {
    super(messageSource);
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return ClaimDetailsViewField.CASE_START_DATE.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var caseStartDate = form.getDateValue(field.name());
    var submissionPeriod = claim.getSubmissionPeriod();
    var feeType = claim.getFeeType();

    if (caseStartDate == null
        || submissionPeriod == null
        || !FeeCalculationType.DISB_ONLY.equals(feeType)) {
      return;
    }

    LocalDate submissionEndDate = getSubmissionPeriodCutoffDate(submissionPeriod);
    if (caseStartDate.plusMonths(MAXIMUM_MONTHS_DIFFERENCE).isAfter(submissionEndDate)) {
      addUniqueFieldError(
          field,
          DISBURSEMENT_DATE_TO_EARLY,
          new Object[] {
            MAXIMUM_MONTHS_DIFFERENCE, DATE_FORMATTER_D_MMM_YYYY.format(submissionEndDate)
          },
          errors);
    }
  }
}
