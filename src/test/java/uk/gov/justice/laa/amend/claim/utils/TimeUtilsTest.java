package uk.gov.justice.laa.amend.claim.utils;

import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TimeUtilsTest {

    @Test
    public void isInTimeRange_whenTimeIsInTimeRange_returnsTrue() {
        LocalTime now = LocalTime.of(15, 0);
        LocalTime start = LocalTime.of(14, 59);
        LocalTime end = LocalTime.of(15, 1);

        Assertions.assertTrue(TimeUtils.isInTimeRange(now, start, end));
    }

    @Test
    public void isInTimeRange_whenTimeIsSecondsIntoTimeRange_returnsTrue() {
        LocalTime now = LocalTime.of(15, 0, 1);
        LocalTime start = LocalTime.of(15, 0);
        LocalTime end = LocalTime.of(15, 1);

        Assertions.assertTrue(TimeUtils.isInTimeRange(now, start, end));
    }

    @Test
    public void isInTimeRange_whenStartAndEndAreTheSame_returnsTrue() {
        LocalTime now = LocalTime.of(12, 0);
        LocalTime start = LocalTime.of(15, 0);
        LocalTime end = LocalTime.of(15, 0);

        Assertions.assertTrue(TimeUtils.isInTimeRange(now, start, end));
    }

    @ParameterizedTest
    @ValueSource(ints = {18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4, 5, 6, 7, 8})
    public void isInTimeRange_whenInRangeAndEndsEarlierThanItStarts_returnsTrue(int hour) {
        LocalTime now = LocalTime.of(hour, 0);
        LocalTime start = LocalTime.of(17, 0);
        LocalTime end = LocalTime.of(9, 0);

        Assertions.assertTrue(TimeUtils.isInTimeRange(now, start, end));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11, 12, 13, 14, 15, 16})
    public void isInTimeRange_whenNotInRangeAndEndsEarlierThanItStarts_returnsFalse(int hour) {
        LocalTime now = LocalTime.of(hour, 0);
        LocalTime start = LocalTime.of(17, 0);
        LocalTime end = LocalTime.of(9, 0);

        Assertions.assertFalse(TimeUtils.isInTimeRange(now, start, end));
    }

    @Test
    public void isInTimeRange_returnsFalse_whenTimeIsNotInTimeRange() {
        LocalTime now = LocalTime.of(14, 58);
        LocalTime start = LocalTime.of(14, 59);
        LocalTime end = LocalTime.of(15, 1);

        Assertions.assertFalse(TimeUtils.isInTimeRange(now, start, end));
    }
}
