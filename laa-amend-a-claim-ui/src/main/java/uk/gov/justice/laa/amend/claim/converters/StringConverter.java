package uk.gov.justice.laa.amend.claim.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Normalize all Strings submitted in forms, trimming whitespace and mapping empty strings to null.
 */
@Component
public class StringConverter implements Converter<String, String> {
  @Override
  public String convert(String source) {
    if (source == null) {
      return null;
    }
    var trimmed = source.strip();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
