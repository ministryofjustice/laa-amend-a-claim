package uk.gov.justice.laa.amend.claim.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache")
@Getter
public class CacheProperties {
    private final long timeToLiveInSeconds;

    public CacheProperties(long timeToLiveInSeconds) {
        this.timeToLiveInSeconds = timeToLiveInSeconds;
    }
}
