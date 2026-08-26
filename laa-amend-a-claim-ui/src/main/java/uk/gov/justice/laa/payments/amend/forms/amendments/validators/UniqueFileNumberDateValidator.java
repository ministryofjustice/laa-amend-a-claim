package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.utils.DateWrapperUtil;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField;

@Component
@RequiredArgsConstructor
public class UniqueFileNumberDateValidator implements FieldSpecificAmendmentValidator {

  static final String INVALID_DATE = "amendmentForm.uniqueFileNumber.invalidDate";
  static final String DATE_IN_FUTURE = "amendmentForm.uniqueFileNumber.dateInFuture";

  private static final Pattern UNIQUE_FILE_NUMBER = Pattern.compile("^([0-9]{6})/[0-9]{3}$");
  private static final int TWO_DIGIT_YEAR_CUTOFF = 50;

  private final MessageSource messageSource;
  private final DateWrapperUtil dateWrapperUtil;

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return CivilClaimDetailsViewField.UNIQUE_FILE_NUMBER.equals(field)
        || CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var value = form.getInputs().get(field.name());
    if (isBlank(value)) {
      return;
    }

    var matcher = UNIQUE_FILE_NUMBER.matcher(value);
    if (!matcher.matches()) {
      return;
    }

    LocalDate date;
    try {
      date = parseDate(matcher.group(1));
    } catch (DateTimeException e) {
      addUniqueFieldError(field, INVALID_DATE, new Object[] {field.label(messageSource)}, errors);
      return;
    }

    if (dateWrapperUtil.isFutureDate(date)) {
      addUniqueFieldError(field, DATE_IN_FUTURE, new Object[] {field.label(messageSource)}, errors);
    }
  }

  private LocalDate parseDate(String ddmmyy) {
    var day = Integer.parseInt(ddmmyy.substring(0, 2));
    var month = Integer.parseInt(ddmmyy.substring(2, 4));
    var twoDigitYear = Integer.parseInt(ddmmyy.substring(4, 6));
    var year = twoDigitYear > TWO_DIGIT_YEAR_CUTOFF ? 1900 + twoDigitYear : 2000 + twoDigitYear;
    return LocalDate.of(year, month, day);
  }
}
