package uk.gov.justice.laa.amend.claim.client.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "claims-api")
@Getter
public class ClaimsApiProperties {
    private final String url;
    private final String accessToken;

    public ClaimsApiProperties(String url, String accessToken) {
        this.url = url;
        this.accessToken = accessToken;
    }
}
