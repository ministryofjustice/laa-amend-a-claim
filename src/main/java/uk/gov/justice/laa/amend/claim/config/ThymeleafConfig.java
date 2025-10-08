package uk.gov.justice.laa.amend.claim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.justice.laa.amend.claim.forms.helpers.StringUtils;

@Configuration
public class ThymeleafConfig {

    @Bean(name = "StringUtils")
    public StringUtils stringUtils() {
        return new StringUtils();
    }
}
