package uk.gov.justice.laa.amend.claim.client.config;

import java.time.LocalTime;

public record TimeProperties(int hour, int minute) {

    public LocalTime toLocalTime() {
        return LocalTime.of(hour, minute);
    }
}
