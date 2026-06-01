package uk.gov.justice.laa.amend.claim.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.justice.laa.amend.claim.utils.DateWrapperUtil;

@TestConfiguration
public class ViewTestConfig {

  @Bean
  DateWrapperUtil dateWrapperUtil() {
    return new DateWrapperUtil();
  }
}
