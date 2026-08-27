package uk.gov.justice.laa.payments.amend.utils;

import static uk.gov.justice.laa.payments.amend.utils.CurrencyUtils.formatCurrency;
import static uk.gov.justice.laa.payments.amend.utils.DateUtils.displayDateTimeDateValue;
import static uk.gov.justice.laa.payments.amend.utils.DateUtils.displayDateTimeTimeValue;
import static uk.gov.justice.laa.payments.amend.utils.DateUtils.displayDateValue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.validation.FieldError;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.payments.amend.forms.errors.AllowedTotalFormError;
import uk.gov.justice.laa.payments.amend.forms.errors.AmendmentFormError;
import uk.gov.justice.laa.payments.amend.forms.errors.AssessedTotalFormError;
import uk.gov.justice.laa.payments.amend.forms.errors.AssessmentOutcomeFormError;
import uk.gov.justice.laa.payments.amend.forms.errors.MonetaryValueFormError;
import uk.gov.justice.laa.payments.amend.forms.errors.SearchFormError;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.models.enums.FieldType;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafLiteralString;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafMessage;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafString;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.FieldOption;

@AllArgsConstructor
public class ThymeleafUtils {

  public List<SearchFormError> toSearchFormErrors(List<DetailedError> errors) {
    return mapErrors(errors, SearchFormError::new, SearchFormError::getMessage);
  }

  public List<AssessmentOutcomeFormError> toAssessmentOutcomeErrors(List<DetailedError> errors) {
    return mapErrors(
        errors, AssessmentOutcomeFormError::new, AssessmentOutcomeFormError::getMessage);
  }

  public List<MonetaryValueFormError> toMonetaryFormValueErrors(List<DetailedError> errors) {
    return mapErrors(errors, MonetaryValueFormError::new, MonetaryValueFormError::getMessage);
  }

  public List<AssessedTotalFormError> toAssessedTotalFormErrors(List<DetailedError> errors) {
    return mapErrors(errors, AssessedTotalFormError::new, AssessedTotalFormError::getMessage);
  }

  public List<AllowedTotalFormError> toAllowedTotalFormErrors(List<DetailedError> errors) {
    return mapErrors(errors, AllowedTotalFormError::new, AllowedTotalFormError::getMessage);
  }

  public List<AmendmentFormError> toAmendmentFormErrors(List<FieldError> errors) {
    return mapErrors(errors, AmendmentFormError::new, AmendmentFormError::getMessage);
  }

  public List<AmendmentFormError> orEmpty(List<AmendmentFormError> errors) {
    return errors == null ? List.of() : errors;
  }

  public boolean hasAmendmentFieldError(List<AmendmentFormError> errors, String fieldKey) {
    return orEmpty(errors).stream().anyMatch(error -> error.getFieldName().equals(fieldKey));
  }

  public AmendmentFormError amendmentFieldErrorMessage(
      List<AmendmentFormError> errors, String fieldKey) {
    return orEmpty(errors).stream()
        .filter(error -> error.getFieldName().equals(fieldKey))
        .findFirst()
        .orElse(null);
  }

  public boolean supportsClient2(AreaOfLaw areaOfLaw) {
    return AreaOfLaw.MEDIATION.equals(areaOfLaw);
  }

  public String client1DetailsMessageKey(AreaOfLaw areaOfLaw) {
    return supportsClient2(areaOfLaw) ? "claimClient.client1Details" : "claimClient.clientDetails";
  }

  private <S, T> List<T> mapErrors(
      List<S> errors, Function<S, T> mapper, Function<T, String> keyExtractor) {
    return errors.stream()
        .map(mapper)
        .sorted()
        .collect(
            Collectors.collectingAndThen(
                Collectors.toMap(
                    keyExtractor, Function.identity(), (e1, e2) -> e1, LinkedHashMap::new),
                map -> map.values().stream().toList()));
  }

  public ThymeleafString getFormattedValue(Object value) {
    return switch (value) {
      case null -> new ThymeleafMessage("service.noData");
      case BigDecimal bigDecimal -> new ThymeleafLiteralString(formatCurrency(bigDecimal));
      case Integer i -> new ThymeleafLiteralString(i.toString());
      case Boolean b -> getFormattedBoolean(b);
      case String s -> new ThymeleafLiteralString(s);
      case ThymeleafMessage s -> s;
      case OffsetDateTime o ->
          new ThymeleafMessage(
              "fulldate.format", displayDateTimeDateValue(o), displayDateTimeTimeValue(o));
      case LocalDate d -> new ThymeleafLiteralString(displayDateValue(d));
      default -> new ThymeleafLiteralString(value.toString());
    };
  }

  public ThymeleafString getFormattedValue(ClaimViewField<?> field, Object value) {
    if (field.getFieldType() == FieldType.ENUM && value != null) {
      return getFormattedOptionValue(field.getOptions(), value);
    }

    return getFormattedValue(value);
  }

  public ThymeleafString getFormattedOptionValue(List<FieldOption> options, Object value) {
    if (value == null) {
      return getFormattedValue(null);
    }

    var selectedValue = value.toString();
    return options.stream()
        .filter(option -> option.value().equals(selectedValue))
        .findFirst()
        .<ThymeleafString>map(option -> new ThymeleafMessage(option.messageKey()))
        .orElseGet(() -> new ThymeleafLiteralString(selectedValue));
  }

  public ThymeleafString getFormattedBoolean(Boolean value) {
    String key = (value != null && value) ? "service.yes" : "service.no";
    return new ThymeleafMessage(key);
  }
}
