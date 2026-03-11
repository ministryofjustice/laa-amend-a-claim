package uk.gov.justice.laa.amend.claim.client.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "provider-api")
@Getter
public class ProviderApiProperties {

    @NotBlank
    private final String url;

    @NotBlank
    private final String accessToken;

    public ProviderApiProperties(String url, String accessToken) {
        this.url = url;
        this.accessToken = accessToken;
    }
}
