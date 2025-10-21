package uk.gov.justice.laa.amend.claim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated())
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()))
                .successHandler((request, response, authentication) -> {
                    response.sendRedirect("/"); // TODO - Ensure user has correct role(s). See laa-record-link-service for an example.
                }))
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny));
        return http.build();
    }

    @Bean
    public OidcUserService oidcUserService() {
        return new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) {
                OidcUser oidcUser = super.loadUser(userRequest);
                Set<GrantedAuthority> authorities = getAuthorities(oidcUser.getAttributes());
                return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            }
        };
    }

    public Set<GrantedAuthority> getAuthorities(Map<String, Object> attributes) {
        List<String> roles = parseRawRoles(attributes.get("LAA_APP_ROLES"));
        return new SimpleAuthorityMapper()
            .mapAuthorities(
                roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList())
            );
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
