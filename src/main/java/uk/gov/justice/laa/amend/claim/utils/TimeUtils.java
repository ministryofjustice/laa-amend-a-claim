package uk.gov.justice.laa.amend.claim.utils;

import java.time.LocalTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class TimeUtils {

    public static boolean isInTimeRange(LocalTime now, LocalTime start, LocalTime end) {
        if (start.equals(end)) {
            return true;
        }
        if (start.isAfter(end)) {
            return now.isAfter(start) || now.isBefore(end);
        }
        return now.isAfter(start) && now.isBefore(end);
    }
}
