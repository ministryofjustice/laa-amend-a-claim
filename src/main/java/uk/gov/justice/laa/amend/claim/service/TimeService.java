package uk.gov.justice.laa.amend.claim.service;

import java.time.LocalTime;
import org.springframework.stereotype.Service;

@Service
public class TimeService {

    public boolean isInTimeRange(LocalTime start, LocalTime end) {
        LocalTime now = LocalTime.now();
        return now.isAfter(start) && now.isBefore(end);
    }
}
