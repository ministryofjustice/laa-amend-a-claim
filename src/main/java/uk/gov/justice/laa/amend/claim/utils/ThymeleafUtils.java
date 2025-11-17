package uk.gov.justice.laa.amend.claim.utils;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.thymeleaf.expression.Messages;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.forms.errors.AssessmentOutcomeFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.MonetaryValueFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.SearchFormError;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;

@AllArgsConstructor
public class ThymeleafUtils {

    private final MessageSource messageSource;

    public List<SearchFormError> toSearchFormErrors(List<DetailedError> errors) {
        return errors
            .stream()
            .map(SearchFormError::new)
            .sorted()
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toMap(
                        SearchFormError::getMessage,
                        Function.identity(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                    ),
                    map -> map.values().stream().toList()
                )
            );
    }

    public List<AssessmentOutcomeFormError> toAssessmentOutcomeErrors(List<DetailedError> errors) {
        return errors
                .stream()
                .map(AssessmentOutcomeFormError::new)
                .sorted()
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toMap(
                                        AssessmentOutcomeFormError::getMessage,
                                        Function.identity(),
                                        (e1, e2) -> e1,
                                        LinkedHashMap::new
                                ),
                                map -> map.values().stream().toList()
                        )
                );
    }

    public List<MonetaryValueFormError> toMonetaryFormValueErrors(List<DetailedError> errors) {
        return errors
            .stream()
            .map(MonetaryValueFormError::new)
            .toList();
    }

    public String getFormattedValue(Object value) {
        return switch (value) {
            case null -> getMessage("service.noData");
            case BigDecimal bigDecimal -> CurrencyUtils.formatCurrency(bigDecimal);
            case Integer i -> i.toString();
            case Boolean b -> getFormattedBoolean(b);
            case String s -> s;
            default -> value.toString();
        };
    }

    public String getFormattedBoolean(Boolean value) {
        String key = (value != null && value) ? "service.yes" : "service.no";
        return getMessage(key);
    }

    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }
}
