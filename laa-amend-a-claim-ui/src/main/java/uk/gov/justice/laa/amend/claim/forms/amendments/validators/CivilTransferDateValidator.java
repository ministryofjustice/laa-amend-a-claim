package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.time.LocalDate;
import java.time.Month;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

@Component
public class CivilTransferDateValidator extends AmendmentDateValidator {

  private static final LocalDate EARLIEST_TRANSFER_DATE = LocalDate.of(1995, Month.JANUARY, 1);

  public CivilTransferDateValidator(MessageSource messageSource) {
    super(messageSource);
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return CivilClaimDetailsViewField.TRANSFER_DATE.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var transferDate =
        form.getDateValue(field.toString());

    if (transferDate != null
        && transferDate.isBefore(EARLIEST_TRANSFER_DATE)) {
      addDateTooEarlyMessage(
          errors,
          field,
          EARLIEST_TRANSFER_DATE);
    }
  }
}
