package uk.gov.justice.laa.amend.claim.models;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SortTest {

    @Nested
    class ConstructorTests {
        @Test
        void shouldConvertStringToSortWhenAscendingOrder() {
            String str = "unique_file_number,asc";
            Sort result = new Sort(str);
            Assertions.assertEquals(SortField.UNIQUE_FILE_NUMBER, result.getField());
            Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
        }

        @Test
        void shouldConvertStringToSortWhenDescendingOrder() {
            String str = "case_reference_number,desc";
            Sort result = new Sort(str);
            Assertions.assertEquals(SortField.CASE_REFERENCE_NUMBER, result.getField());
            Assertions.assertEquals(SortDirection.DESCENDING, result.getDirection());
        }

        @Test
        void shouldThrowExceptionForInvalidDirection() {
            String str = "unique_file_number,foo";
            Assertions.assertThrows(IllegalArgumentException.class, () -> new Sort(str));
        }

        @Test
        void shouldThrowExceptionForNonCommaSeparatedInput() {
            String str = "unique_file_number";
            Assertions.assertThrows(IllegalArgumentException.class, () -> new Sort(str));
        }

        @Test
        void shouldThrowExceptionForInvalidInput() {
            String str = "unique_file_number,desc,foo";
            Assertions.assertThrows(IllegalArgumentException.class, () -> new Sort(str));
        }
    }

    @Nested
    class ToStringTests {

        @ParameterizedTest
        @MethodSource("sortArguments")
        void shouldBuildSort(SortField sortField, SortDirection sortDirection) {
            Sort sort = Sort.builder().field(sortField).direction(sortDirection).build();

            Assertions.assertEquals(sortField, sort.getField());
            Assertions.assertEquals(sortDirection, sort.getDirection());
        }

        static Stream<Arguments> sortArguments() {
            return Arrays.stream(SortField.values())
                    .flatMap(field -> Arrays.stream(SortDirection.values())
                            .map(sortDirection -> Arguments.of(field, sortDirection)));
        }

        @Test
        void shouldConvertSortToStringWhenNoOrder() {
            Sort sort = Sort.builder()
                    .field(SortField.OFFICE_CODE)
                    .direction(SortDirection.NONE)
                    .build();
            Assertions.assertNull(sort.toString());
        }
    }
}
