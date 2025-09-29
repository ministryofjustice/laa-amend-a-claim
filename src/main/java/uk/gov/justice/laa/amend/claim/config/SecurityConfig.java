package uk.gov.justice.laa.amend.claim.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Profile("!local")
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final String APP_ROLES = "LAA_APP_ROLES";

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**", "/health/**").permitAll()
            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()))
            .successHandler(customSuccessHandler())
            .failureUrl("/login?error=true"))
        .exceptionHandling(ex -> ex
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.sendRedirect("/not-authorised");
            }))
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny));
    return http.build();
  }

  @Bean
  public OidcUserService oidcUserService() {
    return new OidcUserService() {
      @Override
      public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        Map<String, Object> attributes = oidcUser.getAttributes();
        List<String> roles = parseRawRoles(attributes.get(APP_ROLES));

        Set<GrantedAuthority> authorities = new SimpleAuthorityMapper()
            .mapAuthorities(
                roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList())
            );

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
      }
    };
  }

  AuthenticationSuccessHandler customSuccessHandler() {
    return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
      String redirectUrl;
      Object principal = authentication.getPrincipal();

      if (principal instanceof OidcUser oidcUser) {
        Object rawRoles = oidcUser.getAttributes().get(APP_ROLES);
        List<String> roles = parseRawRoles(rawRoles != null ? rawRoles : "");

        if (roles.stream().anyMatch(r -> r.contains("_INTERN"))) {
          redirectUrl = "/";
        } else {
          redirectUrl = "/not-authorised";
        }
      } else {
        redirectUrl = "/not-authorised";
      }

      response.sendRedirect(redirectUrl);
    };
  }

  private List<String> parseRawRoles(Object rawRoles) {
    if (rawRoles instanceof List<?> list) {
      return list.stream().map(Object::toString).toList();
    } else if (rawRoles instanceof String str) {
      return List.of(str.split(","));
    } else {
      return List.of();
    }
  }
}
