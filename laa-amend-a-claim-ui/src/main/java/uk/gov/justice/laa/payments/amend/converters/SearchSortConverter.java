package uk.gov.justice.laa.payments.amend.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.payments.amend.models.search.SearchSort;

@Component
public class SearchSortConverter implements Converter<String, SearchSort> {

  @Override
  public SearchSort convert(String source) {
    try {
      return new SearchSort(source);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }
  }
}
