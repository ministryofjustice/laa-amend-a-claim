package uk.gov.justice.laa.amend.claim.client.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fee-scheme-api")
@Getter
public class FeeSchemePlatformApiProperties {

  @NotBlank private final String url;
  @NotBlank private final String accessToken;

  public FeeSchemePlatformApiProperties(String url, String accessToken) {
    this.url = url;
    this.accessToken = accessToken;
  }
}
