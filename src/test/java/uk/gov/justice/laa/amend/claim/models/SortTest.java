package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SortTest {

    @Nested
    class ConstructorTests {
        @Test
        void shouldConvertStringToSortWhenAscendingOrder() {
            String str = "uniqueFileNumber,asc";
            Sort result = new Sort(str);
            Assertions.assertEquals(SortField.UNIQUE_FILE_NUMBER, result.getField());
            Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
        }

        @Test
        void shouldConvertStringToSortWhenDescendingOrder() {
            String str = "caseReferenceNumber,desc";
            Sort result = new Sort(str);
            Assertions.assertEquals(SortField.CASE_REFERENCE_NUMBER, result.getField());
            Assertions.assertEquals(SortDirection.DESCENDING, result.getDirection());
        }

        @Test
        void shouldThrowExceptionForInvalidDirection() {
            String str = "scheduleReference,foo";
            Assertions.assertThrows(IllegalArgumentException.class, () -> new Sort(str));
        }

        @Test
        void shouldThrowExceptionForNonCommaSeparatedInput() {
            String str = "foo";
            Assertions.assertThrows(IllegalArgumentException.class, () -> new Sort(str));
        }

        @Test
        void shouldThrowExceptionForInvalidInput() {
            String str = "scheduleReference,desc,foo";
            Assertions.assertThrows(IllegalArgumentException.class, () -> new Sort(str));
        }
    }

    @Nested
    class ToStringTests {
        @Test
        void shouldConvertSortToStringWhenAscendingOrder() {
            Sort sort = Sort.builder()
                    .field(SortField.UNIQUE_FILE_NUMBER)
                    .direction(SortDirection.ASCENDING)
                    .build();
            Assertions.assertEquals("uniqueFileNumber,asc", sort.toString());
        }

        @Test
        void shouldConvertSortToStringWhenDescendingOrder() {
            Sort sort = Sort.builder()
                    .field(SortField.CASE_REFERENCE_NUMBER)
                    .direction(SortDirection.DESCENDING)
                    .build();
            Assertions.assertEquals("caseReferenceNumber,desc", sort.toString());
        }

        @Test
        void shouldConvertSortToStringWhenNoOrder() {
            Sort sort = Sort.builder()
                    .field(SortField.SCHEDULE_REFERENCE)
                    .direction(SortDirection.NONE)
                    .build();
            Assertions.assertNull(sort.toString());
        }
    }
}
