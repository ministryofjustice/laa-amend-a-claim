package uk.gov.justice.laa.amend.claim.service;

import static org.mockito.Mockito.mockStatic;

import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TimeServiceTest {

    @InjectMocks
    private TimeService timeService;

    @Test
    public void isInTimeRange_returnsTrue_whenTimeIsInTimeRange() {
        LocalTime now = LocalTime.of(15, 0);
        LocalTime start = LocalTime.of(14, 59);
        LocalTime end = LocalTime.of(15, 1);

        try (MockedStatic<LocalTime> mockedLocalTime = mockStatic(LocalTime.class)) {

            mockedLocalTime.when(LocalTime::now).thenReturn(now);

            Assertions.assertTrue(timeService.isInTimeRange(start, end));
        }
    }

    @Test
    public void isInTimeRange_returnsFalse_whenTimeIsNotInTimeRange() {
        LocalTime now = LocalTime.of(14, 58);
        LocalTime start = LocalTime.of(14, 59);
        LocalTime end = LocalTime.of(15, 1);

        try (MockedStatic<LocalTime> mockedLocalTime = mockStatic(LocalTime.class)) {

            mockedLocalTime.when(LocalTime::now).thenReturn(now);

            Assertions.assertFalse(timeService.isInTimeRange(start, end));
        }
    }
}
