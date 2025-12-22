package uk.gov.justice.laa.amend.claim.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.LinkedHashMap;
import java.util.Map;

import static uk.gov.justice.laa.amend.claim.config.security.SecurityConstants.AUTHENTICATED;

/**
 * Wraps Spring's DefaultOAuth2AuthorizationRequestResolver and conditionally injects "prompt=login"
 * (or "select_account") into the authorization request based on the current HttpServletRequest.
 */
public class ForceLoginAuthorizationResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver clientRegistrationRepository;
    private final String authorizationRequestBaseUri;

    private final String promptValue; // "login" or "select_account"

    /**
     * * @param clients ClientRegistrationRepository bean
     *
     * @param authorizationRequestBaseUri usually "/oauth2/authorization"
     * @param promptValue                 "login" to force credential prompt, or "select_account" to force account
     *                                    chooser
     */
    public ForceLoginAuthorizationResolver(ClientRegistrationRepository clientRegistrationRepository, String authorizationRequestBaseUri, String promptValue) {
        this.clientRegistrationRepository = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestBaseUri);
        this.promptValue = promptValue == null ? "login" : promptValue;
        this.authorizationRequestBaseUri = authorizationRequestBaseUri;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest base = clientRegistrationRepository.resolve(request);
        return maybeAddPrompt(request, base);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest base = clientRegistrationRepository.resolve(request, clientRegistrationId);
        return maybeAddPrompt(request, base);
    }

    private OAuth2AuthorizationRequest maybeAddPrompt(HttpServletRequest request, OAuth2AuthorizationRequest base) {
        if (base == null) {
            return null;
        }

        if (!request.getRequestURI().startsWith(authorizationRequestBaseUri)) {
            return base;
        }

        boolean force = shouldForcePrompt(request);

        Map<String, Object> extra = new LinkedHashMap<>();
        Map<String, Object> baseParams = base.getAdditionalParameters();
        if (baseParams != null && !baseParams.isEmpty()) {
            extra.putAll(baseParams);
        }
        if (force) {
            extra.put("prompt", promptValue);
            return OAuth2AuthorizationRequest.from(base).additionalParameters(extra).build();
        }
        return base;
    }

    /**
     * Core policy for forcing prompt
     */
    private boolean shouldForcePrompt(HttpServletRequest request) {
        var session = request.getSession(false);

        if (session == null) {
            return true;
        }

        var auth = request.getSession().getAttribute(AUTHENTICATED);
        return auth == null || !Boolean.parseBoolean(String.valueOf(auth));
    }
}
