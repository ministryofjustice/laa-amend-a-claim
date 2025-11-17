package uk.gov.justice.laa.amend.claim.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.justice.laa.amend.claim.utils.RedirectUrlUtils;
import uk.gov.justice.laa.amend.claim.utils.ThymeleafUtils;

@Configuration
public class ThymeleafConfig {

    @Bean(name = "ThymeleafUtils")
    public ThymeleafUtils thymeleafUtils(MessageSource messageSource) {
        return new ThymeleafUtils(messageSource);
    }

    @Bean(name = "RedirectUrlUtils")
    public RedirectUrlUtils redirectUrlUtils() {
        return new RedirectUrlUtils();
    }
}
