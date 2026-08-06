package uk.gov.justice.laa.amend.claim.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.justice.laa.amend.claim.utils.DateWrapperUtil;
import uk.gov.justice.laa.amend.claim.viewmodels.AmendmentsHeaderViewFactory;

@TestConfiguration
public class ViewTestConfig {

  @Bean
  DateWrapperUtil dateWrapperUtil() {
    return new DateWrapperUtil();
  }

  @Bean
  AmendmentsHeaderViewFactory amendmentsHeaderViewFactory() {
    return new AmendmentsHeaderViewFactory(userId -> null);
  }
}
