package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

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
        void shouldConvertStringToSortWhenNoOrder() {
            String str = "scheduleReference,none";
            Sort result = new Sort(str);
            Assertions.assertEquals("scheduleReference", result.getField());
            Assertions.assertEquals(SortDirection.NONE, result.getDirection());
        }

        @Test
        void shouldDefaultToNoOrderForInvalidDirection() {
            String str = "scheduleReference,foo";
            Sort result = new Sort(str);
            Assertions.assertEquals("scheduleReference", result.getField());
            Assertions.assertEquals(SortDirection.NONE, result.getDirection());
        }

        @Test
        void shouldDefaultToAscendingUniqueFileNumberForNonCommaSeparatedInput() {
            String str = "foo";
            Sort result = new Sort(str);
            Assertions.assertEquals("uniqueFileNumber", result.getField());
            Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
        }

        @Test
        void shouldDefaultToAscendingUniqueFileNumberForInvalidInput() {
            String str = "scheduleReference,desc,foo";
            Sort result = new Sort(str);
            Assertions.assertEquals("uniqueFileNumber", result.getField());
            Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
        }
    }

    @Nested
    class ToStringTests {
        @Test
        void shouldConvertSortToStringWhenAscendingOrder() {
            Sort sort = new Sort();
            sort.setField("uniqueFileNumber");
            sort.setDirection(SortDirection.ASCENDING);
            Assertions.assertEquals("uniqueFileNumber,asc", sort.toString());
        }

        @Test
        void shouldConvertSortToStringWhenDescendingOrder() {
            Sort sort = new Sort();
            sort.setField("caseReferenceNumber");
            sort.setDirection(SortDirection.DESCENDING);
            Assertions.assertEquals("caseReferenceNumber,desc", sort.toString());
        }

        @Test
        void shouldConvertSortToStringWhenNoOrder() {
            Sort sort = new Sort();
            sort.setField("scheduleReference");
            sort.setDirection(SortDirection.NONE);
            Assertions.assertNull(sort.toString());
        }
    }
}
