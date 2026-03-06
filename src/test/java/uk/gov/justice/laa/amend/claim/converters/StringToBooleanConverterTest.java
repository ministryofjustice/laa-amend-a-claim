package uk.gov.justice.laa.amend.claim.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class StringToBooleanConverterTest {

    private final StringToBooleanConverter converter = new StringToBooleanConverter();

    private static Stream<Arguments> provideArguments() {
        return Stream.of(
                Arguments.of("true", true),
                Arguments.of("True", true),
                Arguments.of("false", false),
                Arguments.of("False", false),
                Arguments.of("yes", true),
                Arguments.of("Yes", true),
                Arguments.of("no", false),
                Arguments.of("No", false),
                Arguments.of(null, null),
                Arguments.of("", null));
    }

    @MethodSource("provideArguments")
    @ParameterizedTest
    void convertsStringToBoolean(String input, Boolean expected) {
        assertEquals(expected, converter.convert(input));
    }
}
