package uk.gov.justice.laa.amend.claim.client.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "provider-api")
public record ProviderApiProperties(@NotBlank String url, @NotBlank String accessToken) {}
