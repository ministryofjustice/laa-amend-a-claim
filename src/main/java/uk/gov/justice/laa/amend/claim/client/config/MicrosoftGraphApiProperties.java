package uk.gov.justice.laa.amend.claim.client.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "microsoft-graph-api")
public record MicrosoftGraphApiProperties(@NotBlank String url) {}
