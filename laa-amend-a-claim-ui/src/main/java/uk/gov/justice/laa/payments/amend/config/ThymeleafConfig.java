package uk.gov.justice.laa.payments.amend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.justice.laa.payments.amend.utils.ThymeleafUtils;

@Configuration
public class ThymeleafConfig {

  @Bean(name = "ThymeleafUtils")
  public ThymeleafUtils thymeleafUtils() {
    return new ThymeleafUtils();
  }
}
