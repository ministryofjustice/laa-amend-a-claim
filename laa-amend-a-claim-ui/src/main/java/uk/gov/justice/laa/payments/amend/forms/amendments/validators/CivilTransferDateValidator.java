package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import java.time.LocalDate;
import java.time.Month;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.payments.amend.utils.DateWrapperUtil;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

@Component
public class CivilTransferDateValidator extends MinDateAmendmentValidator {

  private static final LocalDate EARLIEST_TRANSFER_DATE = LocalDate.of(1995, Month.JANUARY, 1);

  public CivilTransferDateValidator(MessageSource messageSource, DateWrapperUtil dateWrapperUtil) {
    super(messageSource, dateWrapperUtil);
  }

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return CivilClaimDetailsViewField.TRANSFER_DATE.equals(field);
  }

  @Override
  protected LocalDate earliestAllowedDate() {
    return EARLIEST_TRANSFER_DATE;
  }
}
