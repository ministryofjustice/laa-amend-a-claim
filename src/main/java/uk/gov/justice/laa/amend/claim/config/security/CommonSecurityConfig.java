package uk.gov.justice.laa.amend.claim.config.security;

import static uk.gov.justice.laa.amend.claim.config.security.SecurityConstants.PERMISSIONS_POLICY;
import static uk.gov.justice.laa.amend.claim.config.security.SecurityConstants.POLICY_DIRECTIVES;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@EnableMethodSecurity
public abstract class CommonSecurityConfig {

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

    public OncePerRequestFilter createSecurityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    @NonNull HttpServletRequest request,
                    @NonNull HttpServletResponse response,
                    @NonNull FilterChain filterChain)
                    throws ServletException, IOException {
                response.setHeader("Content-Security-Policy", POLICY_DIRECTIVES);
                response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
                response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
                response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
                response.setHeader("Permissions-Policy", PERMISSIONS_POLICY);
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setHeader("X-Frame-Options", "DENY");
                filterChain.doFilter(request, response);
            }
        };
    }
}
