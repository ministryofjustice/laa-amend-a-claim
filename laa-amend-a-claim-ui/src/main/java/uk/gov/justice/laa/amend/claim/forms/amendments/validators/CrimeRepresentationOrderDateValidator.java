package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.time.LocalDate;
import java.time.Month;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.utils.DateWrapperUtil;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;

@Component
public class CrimeRepresentationOrderDateValidator extends MinDateAmendmentValidator {

  private static final LocalDate EARLIEST_MIN_REP_ORDER_DATE = LocalDate.of(2016, Month.APRIL, 1);

  public CrimeRepresentationOrderDateValidator(
      MessageSource messageSource, DateWrapperUtil dateWrapperUtil) {
    super(messageSource, dateWrapperUtil);
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return CrimeClaimDetailsViewField.REPRESENTATION_ORDER_DATE.equals(field);
  }

  @Override
  protected LocalDate earliestAllowedDate() {
    return EARLIEST_MIN_REP_ORDER_DATE;
  }
}
