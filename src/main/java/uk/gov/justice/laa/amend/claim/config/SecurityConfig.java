package uk.gov.justice.laa.amend.claim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

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
            .requestMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()))
            .successHandler((request, response, authentication) -> {
              response.sendRedirect("/");
            }))
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
