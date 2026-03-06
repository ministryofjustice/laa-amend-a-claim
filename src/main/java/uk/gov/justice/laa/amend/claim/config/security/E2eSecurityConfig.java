package uk.gov.justice.laa.amend.claim.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;

/**
 * Security configuration for E2E testing environments.
 * This configuration disables CSP to allow Playwright tests to interact with the UI without
 * Content Security Policy restrictions interfering with test automation.
 */
@Profile("e2e")
@Configuration
@EnableWebSecurity
public class E2eSecurityConfig extends DummyUserSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChainE2e(final HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(oidcUserService(), AnonymousAuthenticationFilter.class)
                .addFilterAfter(securityHeadersFilter(), HeaderWriterFilter.class);
        return http.build();
    }

    @Override
    public String email() {
        return "e2e-test@example.com";
    }

    @Override
    public String name() {
        return "E2E Test User";
    }

    @Override
    public String tokenValue() {
        return "e2e-test-token";
    }
}
