package uk.gov.justice.laa.amend.claim.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

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

        @Test
        void mapRolesToAuthoritiesWhenRolesAreCommaSeparated() {
            SecurityConfig config = new SecurityConfig();
            Map<String, Object> attributes = Map.of("LAA_APP_ROLES", "USER,ADMIN");

            Set<GrantedAuthority> result = config.getAuthorities(attributes);

            assertThat(result).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        }

        @Test
        void mapRolesToAuthoritiesWhenRolesAreInAList() {
            SecurityConfig config = new SecurityConfig();
            Map<String, Object> attributes = Map.of("LAA_APP_ROLES", List.of("USER", "ADMIN"));

            Set<GrantedAuthority> result = config.getAuthorities(attributes);

            assertThat(result).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        }
    }
}