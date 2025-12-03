package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.models.User;


@RequiredArgsConstructor
@Service
public class UserRetrievalService {

    private final WebClient.Builder webClientBuilder;

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private Mono<User> callGraphApi(String upn, String token) {
        return webClientBuilder.baseUrl("https://graph.microsoft.com/v1.0")
                .build()
                .get()
                .uri("/users/{upn}", upn)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(User.class);
    }

    public User getGraphUser(Authentication authentication, String userId) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken && userId != null) {

            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(oauthToken.getAuthorizedClientRegistrationId())
                    .principal(authentication)
                    .build();

            OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

            if (authorizedClient != null) {
                String accessToken = authorizedClient.getAccessToken().getTokenValue();
                return callGraphApi(userId, accessToken).block();
            }
        }
        return null;
    }
}
