package uk.gov.justice.laa.amend.claim.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DateUtilsTest {

    @Nested
    class ToSubmissionPeriodTests {

        @Test
        void returnsNullWhenNullMonth() {
            String year = "2007";
            String result = DateUtils.toSubmissionPeriod(null, year);
            Assertions.assertNull(result);
        }

        @Test
        void returnsNullWhenInvalidMonth() {
            String month = "foo";
            String year = "2007";
            String result = DateUtils.toSubmissionPeriod(month, year);
            Assertions.assertNull(result);
        }

        @Test
        void returnsNullWhenNullYear() {
            String month = "3";
            String result = DateUtils.toSubmissionPeriod(month, null);
            Assertions.assertNull(result);
        }

        @Test
        void returnsNullWhenInvalidMonthYear() {
            String month = "3";
            String year = "foo";
            String result = DateUtils.toSubmissionPeriod(month, year);
            Assertions.assertNull(result);
        }

        @Test
        void returnsSubmissionPeriodWhenValidMonthAndYear() {
            String month = "3";
            String year = "2007";
            String result = DateUtils.toSubmissionPeriod(month, year);
            Assertions.assertEquals("MAR-2007", result);
        }
    }
}
