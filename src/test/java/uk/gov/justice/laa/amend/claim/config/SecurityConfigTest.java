package uk.gov.justice.laa.amend.claim.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfigTest.MockSecurityBeans.class, SecurityConfig.class})
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Shared mock beans
     */
    @TestConfiguration
    static class MockSecurityBeans {
        @Bean
        ClientRegistrationRepository clientRegistrationRepository() {
            return mock(ClientRegistrationRepository.class);
        }

        @Bean
        OAuth2AuthorizedClientRepository authorizedClientRepository() {
            return mock(OAuth2AuthorizedClientRepository.class);
        }

        @Bean
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
            return mock(OAuth2UserService.class);
        }

        @RestController
        @EnableMethodSecurity
        static class TestController {
            @GetMapping("/denied")
            @PreAuthorize("hasRole('NON_EXISTENT_ROLE')")
            public String denied() {
                return "should never reach here";
            }
        }
    }

    @Test
    void actuatorEndpointsArePermittedWithoutAuth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void authenticatedEndpointsAccessibleWithValidRole() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedUsersRedirectToLogin() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void incorrectRoleRedirectsToNotAuthorised() throws Exception {
        mockMvc.perform(get("/denied"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/not-authorised"));
    }

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

    /**
     * OAuth2 user service tests
     */
    @Nested
    class SecurityConfigOidcUserServiceTest {

        @Autowired
        private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

        @Test
        void failedOAuthRedirectsToLoginErrorPage() throws Exception {
            when(oAuth2UserService.loadUser(any()))
                .thenThrow(new OAuth2AuthenticationException(new OAuth2Error("invalid_token")));

            mockMvc.perform(get("/login/oauth2/code/id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
        }
    }
}
