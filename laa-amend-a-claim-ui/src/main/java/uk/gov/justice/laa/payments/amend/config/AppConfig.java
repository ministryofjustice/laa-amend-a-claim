package uk.gov.justice.laa.payments.amend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppConfig(String laaUrl, String feedbackUrl, String email) {}
