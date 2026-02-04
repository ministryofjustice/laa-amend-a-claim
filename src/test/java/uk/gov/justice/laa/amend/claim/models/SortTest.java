package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class SortTest {

    @Nested
    class ConstructorTests {
        @Test
        void shouldConvertStringToSortWhenAscendingOrder() {
            String str = "uniqueFileNumber,asc";
            Sort result = new Sort(str);
            Assertions.assertEquals("uniqueFileNumber", result.getField());
            Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
        }

        @Test
        void shouldConvertStringToSortWhenDescendingOrder() {
            String str = "caseReferenceNumber,desc";
            Sort result = new Sort(str);
            Assertions.assertEquals("caseReferenceNumber", result.getField());
            Assertions.assertEquals(SortDirection.DESCENDING, result.getDirection());
        }

        @Test
        void shouldThrowExceptionForInvalidDirection() {
            String str = "scheduleReference,foo";
            ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class, () -> new Sort(str));
            Assertions.assertTrue(ex.getStatusCode().isSameCodeAs(BAD_REQUEST));
        }

        @Test
        void shouldThrowExceptionForNonCommaSeparatedInput() {
            String str = "foo";
            ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class, () -> new Sort(str));
            Assertions.assertTrue(ex.getStatusCode().isSameCodeAs(BAD_REQUEST));
        }

        @Test
        void shouldThrowExceptionForInvalidInput() {
            String str = "scheduleReference,desc,foo";
            ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class, () -> new Sort(str));
            Assertions.assertTrue(ex.getStatusCode().isSameCodeAs(BAD_REQUEST));
        }
    }

    @Nested
    class ToStringTests {
        @Test
        void shouldConvertSortToStringWhenAscendingOrder() {
            Sort sort = Sort.builder().field("uniqueFileNumber").direction(SortDirection.ASCENDING).build();
            Assertions.assertEquals("uniqueFileNumber,asc", sort.toString());
        }

        @Test
        void shouldConvertSortToStringWhenDescendingOrder() {
            Sort sort = Sort.builder().field("caseReferenceNumber").direction(SortDirection.DESCENDING).build();
            Assertions.assertEquals("caseReferenceNumber,desc", sort.toString());
        }

        @Test
        void shouldConvertSortToStringWhenNoOrder() {
            Sort sort = Sort.builder().field("scheduleReference").direction(SortDirection.NONE).build();
            Assertions.assertNull(sort.toString());
        }
    }
}
