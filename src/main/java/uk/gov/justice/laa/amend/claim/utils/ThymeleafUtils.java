package uk.gov.justice.laa.amend.claim.utils;

import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.forms.errors.SearchFormError;

import java.util.List;

public class ThymeleafUtils {

    public List<SearchFormError> sortSearchErrors(List<DetailedError> errors) {
        return errors.stream().map(SearchFormError::new).sorted().toList();
    }
}
