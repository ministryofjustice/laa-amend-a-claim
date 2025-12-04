package uk.gov.justice.laa.amend.claim.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityConfigTest {


    /**
     * Authority mapping tests
     */
    @Nested
    class SecurityConfigGetAuthoritiesTest {
        private SecurityConfig config;

        @BeforeEach
        void setUp() {
            config = new SecurityConfig();
        }

        @Test
        void mapRolesToAuthoritiesWhenRolesAreCommaSeparated() {
            Map<String, Object> attributes = Map.of("LAA_APP_ROLES", "USER,ADMIN");

            Set<GrantedAuthority> result = config.getAuthorities(attributes);

            assertThat(result).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        }

        @Test
        void mapRolesToAuthoritiesWhenRolesAreInAList() {
            Map<String, Object> attributes = Map.of("LAA_APP_ROLES", List.of("USER", "ADMIN"));

            Set<GrantedAuthority> result = config.getAuthorities(attributes);

            assertThat(result).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        }
    }
}