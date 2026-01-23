package uk.gov.justice.laa.amend.claim.client.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "microsoft-graph-api")
@Getter
public class MicrosoftGraphApiProperties {
    private final String url;

    public MicrosoftGraphApiProperties(String url) {
        this.url = url;
    }
}
