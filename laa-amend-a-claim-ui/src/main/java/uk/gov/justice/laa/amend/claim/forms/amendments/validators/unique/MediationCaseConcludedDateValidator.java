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
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

@Component
@RequiredArgsConstructor
public class MediationCaseConcludedDateValidator implements FieldSpecificAmendmentValidator {

  private static final String DATE_FORMAT_D_MMM_YYYY = "d MMMM yyyy";
  public static final DateTimeFormatter DATE_FORMATTER_D_MMM_YYYY =
      DateTimeFormatter.ofPattern(DATE_FORMAT_D_MMM_YYYY);
  private static final LocalDate EARLIEST_CASE_CONCLUDED_DATE_ALLOWED =
      LocalDate.of(2013, Month.APRIL, 1);

  private final MessageSource messageSource;

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return MediationClaimDetailsViewField.CASE_CONCLUDED_DATE.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var caseConcludedDate =
        form.getDateValue(MediationClaimDetailsViewField.CASE_CONCLUDED_DATE.toString());

    if (caseConcludedDate != null
        && caseConcludedDate.isBefore(EARLIEST_CASE_CONCLUDED_DATE_ALLOWED)) {
      addUniqueFieldError(
          MediationClaimDetailsViewField.CASE_CONCLUDED_DATE,
          DATE_CANT_BE_BEFORE_CODE,
          new Object[] {
              field.label(messageSource),
              DATE_FORMATTER_D_MMM_YYYY.format(EARLIEST_CASE_CONCLUDED_DATE_ALLOWED)
          },
          errors);
    }
  }
}
