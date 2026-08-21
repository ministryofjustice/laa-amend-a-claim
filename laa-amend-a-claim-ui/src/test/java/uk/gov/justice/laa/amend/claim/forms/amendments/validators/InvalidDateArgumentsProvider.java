package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

final class InvalidDateArgumentsProvider {

  private InvalidDateArgumentsProvider() {}

  static Stream<Arguments> invalidDateProvider() {
    return Stream.of(
        // Empty strings
        Arguments.of("", "", ""),
        Arguments.of("", "12", "2020"),
        Arguments.of("15", "", "2020"),
        Arguments.of("15", "12", ""),
        // Invalid day (40th date)
        Arguments.of("40", "12", "2020"),
        Arguments.of("32", "01", "2020"),
        Arguments.of("00", "12", "2020"),
        // Invalid month
        Arguments.of("15", "13", "2020"),
        Arguments.of("15", "00", "2020"),
        // Characters instead of numbers
        Arguments.of("abc", "12", "2020"),
        Arguments.of("15", "xyz", "2020"),
        Arguments.of("15", "12", "abcd"),
        Arguments.of("!@#", "$%^", "&*()"),
        // Mixed valid and invalid characters
        Arguments.of("1a", "12", "2020"),
        Arguments.of("15", "1b", "2020"),
        Arguments.of("15", "12", "20c0"));
  }
}
