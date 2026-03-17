package uk.gov.justice.laa.amend.claim.config.security;

import static uk.gov.justice.laa.amend.claim.config.security.SecurityConstants.PUBLIC_PATHS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;

@Profile({"local", "ephemeral"})
@Configuration
@EnableWebSecurity
public class LocalSecurityConfig extends CommonSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChainLocal(
            HttpSecurity http, DummyUserSecurityService dummyUserSecurityService) {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_PATHS)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .addFilterBefore(dummyUserSecurityService, AnonymousAuthenticationFilter.class)
                .addFilterAfter(securityHeadersFilter(), HeaderWriterFilter.class);
        return http.build();
    }
}
