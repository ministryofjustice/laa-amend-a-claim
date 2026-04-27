package uk.gov.justice.laa.amend.claim.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.gov.justice.laa.amend.claim.models.Role;

@Profile({"local", "ephemeral", "e2e"})
@Service
public class DummyUserSecurityService extends OncePerRequestFilter {

  public static final String USER_ID = "00000000-0000-0000-0000-000000000000";
  public static final String EMAIL = "dummy-email@example.com";
  public static final String NAME = "Dummy Name";
  public static final String TOKEN_VALUE = "dummy-token";

  @Getter @Setter
  // Default to all roles, but allow this to be configured
  private Set<Role> roles = Set.of(Role.values());

  @Override
  public void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    SecurityContextHolder.getContext().setAuthentication(createAuthToken(roles));
    filterChain.doFilter(request, response);
  }

  public static OAuth2AuthenticationToken createAuthToken(Set<Role> roles) {
    Map<String, Object> claims =
        Map.of(
            "oid", USER_ID,
            "email", EMAIL,
            "name", NAME);

    OidcIdToken token =
        new OidcIdToken(TOKEN_VALUE, Instant.now(), Instant.now().plusSeconds(3600), claims);

    var authorities =
        new SimpleAuthorityMapper()
            .mapAuthorities(
                roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).toList());

    DefaultOidcUser oidcUser = new DefaultOidcUser(authorities, token, "email");
    return new OAuth2AuthenticationToken(
        oidcUser, authorities, "test" // registrationId
        );
  }
}
