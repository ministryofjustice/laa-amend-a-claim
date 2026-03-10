package uk.gov.justice.laa.amend.claim.client.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "provider-api")
@Getter
public class ProviderApiProperties {

    @NotBlank
    private final String url;

    @NotBlank
    private final String accessToken;

    @NestedConfigurationProperty
    private final TimeProperties start;

    @NestedConfigurationProperty
    private final TimeProperties end;

    public ProviderApiProperties(String url, String accessToken, TimeProperties start, TimeProperties end) {
        this.url = url;
        this.accessToken = accessToken;
        this.start = start;
        this.end = end;
    }
}
