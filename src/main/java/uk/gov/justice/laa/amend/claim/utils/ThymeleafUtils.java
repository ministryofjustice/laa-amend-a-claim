package uk.gov.justice.laa.amend.claim.utils;

import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.forms.errors.AssessmentOutcomeFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.SearchFormError;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ThymeleafUtils {

    public List<SearchFormError> sortSearchErrors(List<DetailedError> errors) {
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

    public List<AssessmentOutcomeFormError> sortAssessmentOutcomeErrors(List<DetailedError> errors) {
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
}
