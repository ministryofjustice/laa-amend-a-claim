package uk.gov.justice.laa.payments.amend.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "claims-api")
public record ClaimsApiProperties(String url, String accessToken) {}
