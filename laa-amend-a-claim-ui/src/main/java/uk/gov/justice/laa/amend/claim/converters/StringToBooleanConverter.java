package uk.gov.justice.laa.amend.claim.converters;

import java.util.Locale;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToBooleanConverter implements Converter<String, Boolean> {

  @Override
  public Boolean convert(String source) {
    if (source == null || source.isEmpty()) {
      return null;
    }
    return "yes".equalsIgnoreCase(source) || Boolean.parseBoolean(source);
  }

  public static Boolean convertStrict(String source) {
    if (source == null || source.isBlank()) {
      return null;
    }
    return switch (source.trim().toLowerCase(Locale.ROOT)) {
      case "true", "yes" -> true;
      case "false", "no" -> false;
      default -> throw new IllegalArgumentException("Invalid boolean value: " + source);
    };
  }
}
