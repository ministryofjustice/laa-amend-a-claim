package uk.gov.justice.laa.amend.claim.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String laaUrl;
    private String feedbackUrl;
    private String email;
}
