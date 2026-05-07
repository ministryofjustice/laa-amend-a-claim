package uk.gov.justice.laa.amend.claim.models;

import static org.springframework.util.StringUtils.hasText;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.justice.laa.amend.claim.models.sorting.Sort;
import uk.gov.justice.laa.amend.claim.models.sorting.SortDirection;
import uk.gov.justice.laa.amend.claim.models.sorting.SortField;

public interface PageQuery<T extends SortField, U extends Sort<T>> {
  Integer getPage();

  Integer getSize();

  U getSort();

  String getRedirectUrl(T sortField, SortDirection direction);

  String getRedirectUrl(int page, U sort);

  default String getRedirectUrl() {
    return getRedirectUrl(getPage(), getSort());
  }

  default void rejectUnknownParams(HttpServletRequest request) {
    Set<String> allowed =
        Arrays.stream(this.getClass().getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toSet());
    request
        .getParameterMap()
        .keySet()
        .forEach(
            param -> {
              if (!allowed.contains(param)) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unknown query parameter: " + param);
              }
            });
  }

  default void addQueryParam(UriComponentsBuilder builder, String key, String value) {
    if (hasText(value)) {
      builder.queryParam(key, value);
    }
  }
}
