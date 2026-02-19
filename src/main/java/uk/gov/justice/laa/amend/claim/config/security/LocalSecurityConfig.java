package uk.gov.justice.laa.amend.claim.config.security;

import static uk.gov.justice.laa.amend.claim.config.security.SecurityConstants.POLICY_DIRECTIVES;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Profile({"local", "ephemeral"})
@Configuration
@EnableWebSecurity
public class LocalSecurityConfig {

    public static String userId = "dummy-oid-12345";

    @Bean
    public SecurityFilterChain securityFilterChainLocal(final HttpSecurity http) {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .contentSecurityPolicy(csp -> csp.policyDirectives(POLICY_DIRECTIVES)))
                .addFilterBefore(oidcUserService(), AnonymousAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of());
        configuration.setAllowedMethods(List.of("GET", "POST"));
        configuration.setAllowedHeaders(List.of());
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(0L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public OncePerRequestFilter oidcUserService() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    @NonNull HttpServletRequest request,
                    @NonNull HttpServletResponse response,
                    @NonNull FilterChain filterChain)
                    throws ServletException, IOException {

                Map<String, Object> claims = Map.of(
                        "oid", userId,
                        "email", "dummy-email@example.com",
                        "name", "Dummy Name");

                OidcIdToken token = new OidcIdToken(
                        "dummy-token", Instant.now(), Instant.now().plusSeconds(3600), claims);

                DefaultOidcUser oidcUser = new DefaultOidcUser(Collections.emptyList(), token, "email");

                OAuth2AuthenticationToken oauthToken = new OAuth2AuthenticationToken(
                        oidcUser, oidcUser.getAuthorities(), "test" // registrationId
                        );

                SecurityContextHolder.getContext().setAuthentication(oauthToken);

                filterChain.doFilter(request, response);
            }
        };
    }
}
