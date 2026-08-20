package uk.gov.justice.laa.amend.claim.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "claims-api")
public record ClaimsApiProperties(String url, String accessToken) {}
