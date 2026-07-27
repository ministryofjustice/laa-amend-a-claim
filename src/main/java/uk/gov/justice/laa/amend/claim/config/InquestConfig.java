package uk.gov.justice.laa.amend.claim.config;

import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.inquest")
public class InquestConfig {

  private Set<String> matterTypeCodes;
}
