package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SortDirectionTest {

    @Nested
    class ToggleTests {
        @Test
        void shouldToggleAscendingToDescending() {
            SortDirection sortDirection = SortDirection.ASCENDING;
            Assertions.assertEquals(SortDirection.DESCENDING, sortDirection.toggle());
        }

        @Test
        void shouldToggleDescendingToAscending() {
            SortDirection sortDirection = SortDirection.DESCENDING;
            Assertions.assertEquals(SortDirection.ASCENDING, sortDirection.toggle());
        }

        @Test
        void shouldToggleNoOrderToAscending() {
            SortDirection sortDirection = SortDirection.NONE;
            Assertions.assertEquals(SortDirection.ASCENDING, sortDirection.toggle());
        }
    }

    @Nested
    class FromValueTests {
        @Test
        void shouldConvertAscToAscending() {
            String str = "asc";
            SortDirection result = SortDirection.fromValue(str);
            Assertions.assertEquals(SortDirection.ASCENDING, result);
        }

        @Test
        void shouldConvertDescToDescending() {
            String str = "desc";
            SortDirection result = SortDirection.fromValue(str);
            Assertions.assertEquals(SortDirection.DESCENDING, result);
        }

        @Test
        void shouldConvertNoneToNone() {
            String str = "none";
            SortDirection result = SortDirection.fromValue(str);
            Assertions.assertEquals(SortDirection.NONE, result);
        }
    }
}
