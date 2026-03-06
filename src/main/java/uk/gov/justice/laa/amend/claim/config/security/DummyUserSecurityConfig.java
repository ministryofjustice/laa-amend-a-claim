package uk.gov.justice.laa.amend.claim.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import lombok.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class DummyUserSecurityConfig extends CommonSecurityConfig {

    public static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    public abstract String email();

    public abstract String name();

    public abstract String tokenValue();

    public OncePerRequestFilter oidcUserService() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    @NonNull HttpServletRequest request,
                    @NonNull HttpServletResponse response,
                    @NonNull FilterChain filterChain)
                    throws ServletException, IOException {

                Map<String, Object> claims = Map.of(
                        "oid", USER_ID,
                        "email", email(),
                        "name", name());

                OidcIdToken token = new OidcIdToken(
                        tokenValue(), Instant.now(), Instant.now().plusSeconds(3600), claims);

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
