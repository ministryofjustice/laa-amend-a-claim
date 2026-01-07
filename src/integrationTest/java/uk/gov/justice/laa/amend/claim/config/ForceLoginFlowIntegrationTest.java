package uk.gov.justice.laa.amend.claim.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.security.ForceLoginAuthorizationResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.amend.claim.config.security.SecurityConstants.AUTHENTICATED;

@SpringBootTest
@AutoConfigureMockMvc
class ForceLoginFlowIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void authenticatedShouldShowNoPromptParameterOnRedirect() throws Exception {

        var mvcResult = mockMvc.perform(get("/oauth2/authorization/test-client")).andReturn();

        MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(true);
        session.setAttribute(AUTHENTICATED, true);

        mockMvc.perform(get("/oauth2/authorization/test-client").session(session)).andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("prompt="))));
    }

    @Test
    void nonAuthenticatedShouldShowPromptParameterOnRedirect() throws Exception {
        var mvcResult = mockMvc.perform(get("/oauth2/authorization/test-client")).andReturn();

        MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(true);
        session.setAttribute(AUTHENTICATED, false);

        mockMvc.perform(get("/oauth2/authorization/test-client").session(session)).andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("prompt=login")));
    }

    @Configuration
    static class TestClientConfig {
        @Bean
        ClientRegistrationRepository clientRegistrationRepository() {
            ClientRegistration registration = ClientRegistration
                .withRegistrationId("test-client")
                .clientId("test-client")
                .clientSecret("secret")
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email")
                .authorizationUri("https://auth.example.com/oauth2/v2/auth")
                .tokenUri("https://auth.example.com/oauth2/v2/token")
                .userInfoUri("https://auth.example.com/userinfo")
                .userNameAttributeName("sub").clientName("Test Client").build();
            return new InMemoryClientRegistrationRepository(registration);
        }

        @Bean
        ForceLoginAuthorizationResolver forceLoginAuthorizationResolver(ClientRegistrationRepository repo) {
            return new ForceLoginAuthorizationResolver(repo, "/oauth2/authorization", "login");
        }
    }
}
