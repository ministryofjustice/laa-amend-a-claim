package uk.gov.justice.laa.amend.claim.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.amend.claim.base.RedisSetup;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityConfigIntegrationTest extends RedisSetup {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private OAuth2AuthorizedClientRepository authorizedClientRepository;

    @MockitoBean
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;

    @TestConfiguration
    static class TestControllerConfig {
        @RestController
        @EnableMethodSecurity
        static class TestController {
            @GetMapping("/test-only")
            public String root() {
                return "ok";
            }

            @GetMapping("/test-only/denied")
            @PreAuthorize("hasRole('NON_EXISTENT_ROLE')")
            public String denied() {
                return "should never reach here";
            }
        }
    }

    @Test
    void actuatorEndpointsArePermittedWithoutAuth() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void authenticatedEndpointsAccessibleWithValidRole() throws Exception {
        mockMvc.perform(get("/test-only")).andExpect(status().isOk());
    }

    @Test
    void unauthenticatedUsersRedirectToLogin() throws Exception {
        mockMvc.perform(get("/test-only"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void incorrectRoleReturnsForbidden() throws Exception {
        mockMvc.perform(get("/test-only/denied")).andExpect(status().isForbidden());
    }

    @Test
    void failedOauthRedirectsToLoginErrorPage() throws Exception {
        when(oauth2UserService.loadUser(any()))
                .thenThrow(new OAuth2AuthenticationException(new OAuth2Error("invalid_token")));

        mockMvc.perform(get("/login/oauth2/code/id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }
}
