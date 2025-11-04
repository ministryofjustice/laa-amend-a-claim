package uk.gov.justice.laa.amend.claim.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CacheProperties.class})
public class AppConfig {
}
