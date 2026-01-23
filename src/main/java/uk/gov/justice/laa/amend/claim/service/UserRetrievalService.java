package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.MicrosoftGraphApiClient;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserRetrievalService {

    private final MicrosoftGraphApiClient client;

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public MicrosoftApiUser getMicrosoftApiUser(Authentication authentication, String userId) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken && userId != null) {

            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(oauthToken.getAuthorizedClientRegistrationId())
                .principal(authentication)
                .build();

            try {
                OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
                if (authorizedClient != null) {
                    String accessToken = authorizedClient.getAccessToken().getTokenValue();
                    return client.getUser(userId, "Bearer " + accessToken).block();
                }
            } catch (Exception ex) {
                log.warn("Error retrieving user {}", userId);
                return null;
            }
        }
        return null;
    }
}
