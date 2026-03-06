package uk.gov.justice.laa.amend.claim.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;

@Profile({"local", "ephemeral"})
@Configuration
@EnableWebSecurity
public class LocalSecurityConfig extends DummyUserSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChainLocal(final HttpSecurity http) {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(oidcUserService(), AnonymousAuthenticationFilter.class)
                .addFilterAfter(securityHeadersFilter(), HeaderWriterFilter.class);
        return http.build();
    }

    @Override
    public String email() {
        return "dummy-email@example.com";
    }

    @Override
    public String name() {
        return "Dummy Name";
    }

    @Override
    public String tokenValue() {
        return "dummy-token";
    }
}
