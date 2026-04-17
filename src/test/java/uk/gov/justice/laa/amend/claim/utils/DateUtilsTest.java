package uk.gov.justice.laa.amend.claim.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

  @Nested
  class DisplayDateTimeDateValueTests {

    @Test
    void returnsNullWhenOffsetDateTimeIsNull() {
      Assertions.assertNull(DateUtils.displayDateTimeDateValue(null));
    }

    @Test
    void formatsDateInGmtDuringWinter() {
      // December is GMT (UTC+0), so London time == UTC time
      OffsetDateTime utcDateTime =
          OffsetDateTime.of(LocalDateTime.of(2025, 12, 18, 16, 11, 27), ZoneOffset.UTC);
      Assertions.assertEquals("18 December 2025", DateUtils.displayDateTimeDateValue(utcDateTime));
    }

    @Test
    void formatsDateInBstDuringBstSameDay() {
      // June is BST (UTC+1): UTC 14:30 = London 15:30 — same calendar day
      OffsetDateTime utcDateTime =
          OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 14, 30, 0), ZoneOffset.UTC);
      Assertions.assertEquals("15 June 2025", DateUtils.displayDateTimeDateValue(utcDateTime));
    }

    @Test
    void formatsDateInBstDuringBstCrossingMidnight() {
      // June is BST (UTC+1): UTC 23:30 on the 15th = London 00:30 on the 16th
      OffsetDateTime utcDateTime =
          OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 23, 30, 0), ZoneOffset.UTC);
      Assertions.assertEquals("16 June 2025", DateUtils.displayDateTimeDateValue(utcDateTime));
    }
  }

  @Nested
  class DisplayDateTimeTimeValueTests {

    @Test
    void returnsNullWhenOffsetDateTimeIsNull() {
      Assertions.assertNull(DateUtils.displayDateTimeTimeValue(null));
    }

    @Test
    void formatsTimeInGmtDuringWinter() {
      // December is GMT (UTC+0), so London time == UTC time
      OffsetDateTime utcDateTime =
          OffsetDateTime.of(LocalDateTime.of(2025, 12, 18, 16, 11, 27), ZoneOffset.UTC);
      Assertions.assertEquals("16:11:27", DateUtils.displayDateTimeTimeValue(utcDateTime));
    }

    @Test
    void formatsTimeInBstDuringBst() {
      // June is BST (UTC+1): UTC 14:30:00 = London 15:30:00
      OffsetDateTime utcDateTime =
          OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 14, 30, 0), ZoneOffset.UTC);
      Assertions.assertEquals("15:30:00", DateUtils.displayDateTimeTimeValue(utcDateTime));
    }

    @Test
    void formatsTimeInBstCrossingMidnight() {
      // June is BST (UTC+1): UTC 23:30:00 on the 15th = London 00:30:00 on the 16th
      OffsetDateTime utcDateTime =
          OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 23, 30, 0), ZoneOffset.UTC);
      Assertions.assertEquals("00:30:00", DateUtils.displayDateTimeTimeValue(utcDateTime));
    }
  }
}
