package uk.gov.justice.laa.amend.claim.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import uk.gov.justice.laa.amend.claim.config.security.SecurityConfig;
import uk.gov.justice.laa.amend.claim.models.Role;

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
            Map<String, Object> attributes = Map.of(
                    "LAA_APP_ROLES",
                    "Amend a Claim - Claim Amendments Caseworker,Amend a Claim - Escape Case Caseworker");

            Set<GrantedAuthority> result = config.getAuthorities(attributes);

            assertThat(result)
                    .extracting(GrantedAuthority::getAuthority)
                    .containsExactlyInAnyOrder(
                            Role.ROLE_CLAIM_AMENDMENTS_CASEWORKER.name(), Role.ROLE_ESCAPE_CASE_CASEWORKER.name());
        }

        @Test
        void mapRolesToAuthoritiesWhenRolesAreInList() {
            Map<String, Object> attributes = Map.of(
                    "LAA_APP_ROLES",
                    List.of("Amend a Claim - Claim Amendments Caseworker", "Amend a Claim - Escape Case Caseworker"));

            Set<GrantedAuthority> result = config.getAuthorities(attributes);

            assertThat(result)
                    .extracting(GrantedAuthority::getAuthority)
                    .containsExactlyInAnyOrder(
                            Role.ROLE_CLAIM_AMENDMENTS_CASEWORKER.name(), Role.ROLE_ESCAPE_CASE_CASEWORKER.name());
        }

        @Test
        void ignoresUnknownRoles() {
            Map<String, Object> attributes = Map.of("LAA_APP_ROLES", List.of("A role that's not in the Role enum"));

            Set<GrantedAuthority> result = config.getAuthorities(attributes);

            assertThat(result).isEmpty();
        }
    }
}
