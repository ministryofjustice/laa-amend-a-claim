package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.time.LocalDate;
import java.time.Month;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;

@Component
public class CrimeMinRepresentationOrderDateValidator extends AmendmentDateValidator {

  private static final LocalDate EARLIEST_MIN_REP_ORDER_DATE = LocalDate.of(2016, Month.APRIL, 1);

  public CrimeMinRepresentationOrderDateValidator(MessageSource messageSource) {
    super(messageSource);
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return CrimeClaimDetailsViewField.REPRESENTATION_ORDER_DATE.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var representationOrderDate =
        form.getDateValue(CrimeClaimDetailsViewField.REPRESENTATION_ORDER_DATE.toString());

    if (representationOrderDate != null
        && representationOrderDate.isBefore(EARLIEST_MIN_REP_ORDER_DATE)) {
      addDateTooEarlyMessage(
          errors,
          field,
          EARLIEST_MIN_REP_ORDER_DATE);
    }
  }
}
