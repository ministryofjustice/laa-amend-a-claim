package uk.gov.justice.laa.amend.claim.utils;

import lombok.AllArgsConstructor;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.forms.errors.AllowedTotalFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.AssessedTotalFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.AssessmentOutcomeFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.MonetaryValueFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.SearchFormError;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafLiteralString;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafMessage;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.amend.claim.utils.CurrencyUtils.formatCurrency;
import static uk.gov.justice.laa.amend.claim.utils.DateUtils.displayDateTimeDateValue;
import static uk.gov.justice.laa.amend.claim.utils.DateUtils.displayDateTimeTimeValue;
import static uk.gov.justice.laa.amend.claim.utils.DateUtils.displayDateValue;

@AllArgsConstructor
public class ThymeleafUtils {

    public List<SearchFormError> toSearchFormErrors(List<DetailedError> errors) {
        return mapErrors(errors, SearchFormError::new, SearchFormError::getMessage);
    }

    public List<AssessmentOutcomeFormError> toAssessmentOutcomeErrors(List<DetailedError> errors) {
        return mapErrors(errors, AssessmentOutcomeFormError::new, AssessmentOutcomeFormError::getMessage);
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

    private <T> List<T> mapErrors(
        List<DetailedError> errors,
        Function<DetailedError, T> mapper,
        Function<T, String> keyExtractor
    ) {
        return errors
            .stream()
            .map(mapper)
            .sorted()
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toMap(
                        keyExtractor,
                        Function.identity(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                    ),
                    map -> map.values().stream().toList()
                )
            );
    }

    public ThymeleafString getFormattedValue(Object value) {
        return switch (value) {
            case null -> new ThymeleafMessage("service.noData");
            case BigDecimal bigDecimal -> new ThymeleafLiteralString(formatCurrency(bigDecimal));
            case Integer i -> new ThymeleafLiteralString(i.toString());
            case Boolean b -> getFormattedBoolean(b);
            case String s -> new ThymeleafLiteralString(s);
            case OffsetDateTime o -> getFormattedValue(o.toLocalDateTime());
            case LocalDate d -> new ThymeleafLiteralString(displayDateValue(d));
            case LocalDateTime d -> new ThymeleafMessage("fulldate.format", displayDateTimeDateValue(d), displayDateTimeTimeValue(d));
            default -> new ThymeleafLiteralString(value.toString());
        };
    }

    public ThymeleafString getAssessedValue(ClaimField value) {
        Object obj = value != null && value.getAssessed() != null ? value.getAssessed() : null;
        return getFormattedValue(obj);
    }

    public ThymeleafString getChangeLinkText(ClaimField value) {
        var val = value != null && value.isAssessed() ? "service.change" : "service.add";
        return new ThymeleafMessage(val);
    }


    public ThymeleafString getFormattedBoolean(Boolean value) {
        String key = (value != null && value) ? "service.yes" : "service.no";
        return new ThymeleafMessage(key);
    }
}
