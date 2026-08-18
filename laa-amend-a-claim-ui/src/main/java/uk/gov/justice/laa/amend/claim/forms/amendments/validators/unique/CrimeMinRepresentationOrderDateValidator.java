package uk.gov.justice.laa.amend.claim.forms.amendments.validators.unique;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;

@Component
@RequiredArgsConstructor
public class CrimeMinRepresentationOrderDateValidator implements FieldSpecificAmendmentValidator {

  private static final String DATE_FORMAT_D_MMM_YYYY = "d MMMM yyyy";
  public static final DateTimeFormatter DATE_FORMATTER_D_MMM_YYYY =
      DateTimeFormatter.ofPattern(DATE_FORMAT_D_MMM_YYYY);
  private static final LocalDate EARLIEST_MIN_REP_ORDER_DATE
      = LocalDate.of(2016, Month.APRIL, 1);

  private final MessageSource messageSource;

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
      addUniqueFieldError(
          CrimeClaimDetailsViewField.REPRESENTATION_ORDER_DATE,
          DATE_CANT_BE_BEFORE_CODE,
          new Object[] {
              field.label(messageSource),
              DATE_FORMATTER_D_MMM_YYYY.format(EARLIEST_MIN_REP_ORDER_DATE)
          },
          errors);
    }
  }
}
