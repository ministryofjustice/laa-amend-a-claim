package uk.gov.justice.laa.amend.claim.config.security;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.amend.claim.config.security.SecurityConstants.AUTHENTICATED;

class ForceLoginAuthorizationResolverTest {

    private static final String BASE_URI = "/oauth2/authorization";
    private static final String REGISTRATION_ID = "test-client";
    private static final String CLIENT_ID = "test-client";

    private InMemoryClientRegistrationRepository clientRepo;
    private ForceLoginAuthorizationResolver resolverLoginPrompt;
    private ForceLoginAuthorizationResolver resolverSelectAccount;

    @BeforeEach
    void setUp() {
        ClientRegistration registration = ClientRegistration.withRegistrationId(REGISTRATION_ID)
            .clientId(CLIENT_ID)
            .clientSecret("secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("openid", "profile", "email")
            .authorizationUri("https://auth.example.com/oauth2/v2/auth")
            .tokenUri("https://auth.example.com/oauth2/v2/token")
            .userInfoUri("https://auth.example.com/userinfo")
            .userNameAttributeName("sub")
            .clientName("Test Client")
            .build();

        clientRepo = new InMemoryClientRegistrationRepository(registration);

        resolverLoginPrompt = new ForceLoginAuthorizationResolver(clientRepo, BASE_URI, "login");
        resolverSelectAccount = new ForceLoginAuthorizationResolver(clientRepo, BASE_URI, "select_account");
    }

    @Test
    void whenSessionAuthenticatedTrue_thenNoPromptAdded() {
        MockHttpServletRequest req = authRequestPath();
        HttpSession session = req.getSession(true);
        session.setAttribute(AUTHENTICATED, true);

        OAuth2AuthorizationRequest result = resolverLoginPrompt.resolve(req);

        assertThat(result).isNotNull();
        assertThat(result.getAdditionalParameters()).doesNotContainKey("prompt");
    }

    @Test
    void whenSessionAuthenticatedFalse_thenPromptAdded_login() {
        MockHttpServletRequest req = authRequestPath();
        req.getSession(true).setAttribute(AUTHENTICATED, false);

        OAuth2AuthorizationRequest result = resolverLoginPrompt.resolve(req);

        assertThat(result).isNotNull();
        assertThat(result.getAdditionalParameters()).containsEntry("prompt", "login");
    }

    @Test
    void whenSessionAuthenticatedFalse_thenPromptAdded_selectAccount() {
        MockHttpServletRequest req = authRequestPath();
        req.getSession(true).setAttribute(AUTHENTICATED, false);

        OAuth2AuthorizationRequest result = resolverSelectAccount.resolve(req);

        assertThat(result).isNotNull();
        assertThat(result.getAdditionalParameters()).containsEntry("prompt", "select_account");
    }

    @Test
    void whenNoSession_thenPromptAdded_login() {
        MockHttpServletRequest req = authRequestPath();
        // no session created

        OAuth2AuthorizationRequest result = resolverLoginPrompt.resolve(req);

        assertThat(result).isNotNull();
        assertThat(result.getAdditionalParameters()).containsEntry("prompt", "login");
    }

    @Test
    void whenAuthenticatedAttrIsMissing_thenPromptAdded() {
        MockHttpServletRequest req = authRequestPath();
        req.getSession(true); // create session but don't set AUTHENTICATED

        OAuth2AuthorizationRequest result = resolverLoginPrompt.resolve(req);

        assertThat(result).isNotNull();
        assertThat(result.getAdditionalParameters()).containsEntry("prompt", "login");
    }

    @Test
    void whenAuthenticatedAttrNonBoolean_thenPromptAdded() {
        MockHttpServletRequest req = authRequestPath();
        req.getSession(true).setAttribute(AUTHENTICATED, "not-boolean");

        OAuth2AuthorizationRequest result = resolverLoginPrompt.resolve(req);

        assertThat(result).isNotNull();
        assertThat(result.getAdditionalParameters()).containsEntry("prompt", "login");
    }

    @Test
    void whenRequestUriDoesNotStartWithBaseUri_thenNoPromptAndPossiblyNull() {
        MockHttpServletRequest req = nonAuthPath();

        OAuth2AuthorizationRequest result = resolverLoginPrompt.resolve(req);

        // Default resolver returns null for non-matching path
        assertThat(result).isNull();
    }

    // ----------------- Tests for resolve(request, registrationId) -----------

    @Test
    void resolveWithClientRegistrationId_authenticatedTrue_noPrompt() {
        MockHttpServletRequest req = authRequestPath();
        req.getSession(true).setAttribute(AUTHENTICATED, true);

        OAuth2AuthorizationRequest result =
            resolverLoginPrompt.resolve(req, REGISTRATION_ID);

        assertThat(result).isNotNull();
        assertThat(result.getAdditionalParameters()).doesNotContainKey("prompt");
    }

    @Test
    void resolveWithClientRegistrationId_authenticatedFalse_promptAdded() {
        MockHttpServletRequest req = authRequestPath();
        req.getSession(true).setAttribute(AUTHENTICATED, false);

        OAuth2AuthorizationRequest result =
            resolverLoginPrompt.resolve(req, REGISTRATION_ID);

        assertThat(result).isNotNull();
        assertThat(result.getAdditionalParameters()).containsEntry("prompt", "login");
    }

    @Test
    void resolveWithClientRegistrationId_noSession_promptAdded() {
        MockHttpServletRequest req = authRequestPath();

        OAuth2AuthorizationRequest result =
            resolverLoginPrompt.resolve(req, REGISTRATION_ID);

        assertThat(result).isNotNull();
        assertThat(result.getAdditionalParameters()).containsEntry("prompt", "login");
    }

    private MockHttpServletRequest authRequestPath() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        // Must match DefaultOAuth2AuthorizationRequestResolver’s expected pattern
        req.setRequestURI(BASE_URI + "/" + REGISTRATION_ID);
        req.setServletPath(BASE_URI + "/" + REGISTRATION_ID);
        return req;
    }

    private MockHttpServletRequest nonAuthPath() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setRequestURI("/not/oauth2/authorization");
        req.setServletPath("/not/oauth2/authorization");
        return req;
    }
}
