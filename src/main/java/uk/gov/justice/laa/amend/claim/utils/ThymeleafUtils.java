package uk.gov.justice.laa.amend.claim.utils;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.forms.errors.AllowedTotalFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.AssessedTotalFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.AssessmentOutcomeFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.MonetaryValueFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.SearchFormError;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.JR_FORM_FILLING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.amend.claim.utils.DateUtils.displayFullDateValue;
import static uk.gov.justice.laa.amend.claim.utils.DateUtils.getDateValue;
import static uk.gov.justice.laa.amend.claim.utils.DateUtils.getTimeValue;

@AllArgsConstructor
public class ThymeleafUtils {

    private final MessageSource messageSource;

    private static final List<String> hiddenFields = List.of(CMRH_TELEPHONE, CMRH_ORAL, JR_FORM_FILLING, ADJOURNED_FEE, HO_INTERVIEW, SUBSTANTIVE_HEARING);

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

    public String getFormattedValue(Object value) {
        return switch (value) {
            case null -> getMessage("service.noData");
            case BigDecimal bigDecimal -> CurrencyUtils.formatCurrency(bigDecimal);
            case Integer i -> i.toString();
            case Boolean b -> getFormattedBoolean(b);
            case String s -> s;
            case OffsetDateTime o -> displayFormattedFullDateValue(o.toLocalDateTime());
            case LocalDate d -> displayFullDateValue(d);
            case LocalDateTime d -> displayFormattedFullDateValue(d);
            default -> value.toString();
        };
    }

    public String getAssessedValue(ClaimField value) {
        return (value != null && value.getAssessed() != null)
            ? getFormattedValue(value.getAssessed()) : getMessage("service.noData");
    }


    public String getFormattedBoolean(Boolean value) {
        String key = (value != null && value) ? "service.yes" : "service.no";
        return getMessage(key);
    }

    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }

    private boolean isEmptyValue(Object value) {
        return switch (value) {
            case null -> true;
            case BigDecimal bigDecimal -> BigDecimal.ZERO.compareTo(bigDecimal) == 0;
            case Integer i -> i == 0;
            default -> false;
        };
    }


    /**
     * Determines whether a claim field should be displayed, it used only in summary page.
     * A field displayed:
     * - if field has a non empty submitted value.
     *
     * A field is not displayed if:
     *  - It has a empty submitted value, and
     *  - Its label is in the list of hidden fields.
     *
     * @param claimField the claim field to check
     * @return true if the field should be displayed, false otherwise
     */
    public boolean displayClaimField(ClaimField claimField) {
        if (!isEmptyValue(claimField.getSubmitted())) {
            return true;
        }
        return !hiddenFields.contains(claimField.getKey());
    }

    public String getLastAssessmentAlertMessage(OffsetDateTime value, String user) {
        if (value == null || user == null) {
            return getMessage("service.noData");
        }
        return getMessage("claimSummary.lastAssessmentText", user, displayFormattedFullDateValue(value.toLocalDateTime()));
    }

    public String displayFormattedFullDateValue(LocalDateTime value) {
        return getMessage("fulldate.format", getDateValue(value), getTimeValue(value));
    }
}
