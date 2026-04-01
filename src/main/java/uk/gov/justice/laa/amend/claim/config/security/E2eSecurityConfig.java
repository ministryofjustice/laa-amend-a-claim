package uk.gov.justice.laa.amend.claim.config.security;

import static uk.gov.justice.laa.amend.claim.config.security.SecurityConstants.PUBLIC_PATHS;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;

/**
 * Security configuration for E2E testing environments.
 * This configuration disables CSP to allow Playwright tests to interact with the UI without
 * Content Security Policy restrictions interfering with test automation.
 */
@Profile("e2e")
@Configuration
@EnableWebSecurity
public class E2eSecurityConfig extends CommonSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChainE2e(
            HttpSecurity http, DummyUserSecurityService dummyUserSecurityService) {
        http.authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_PATHS)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(dummyUserSecurityService, AnonymousAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> securityHeadersFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(createSecurityHeadersFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
