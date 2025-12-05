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
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;


@RequiredArgsConstructor
@Service
public class UserRetrievalService {

    private final WebClient.Builder webClientBuilder;

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private Mono<MicrosoftApiUser> callMicrosoftGraphApi(String upn, String token) {
        return webClientBuilder.baseUrl("https://graph.microsoft.com/v1.0")
                .build()
                .get()
                .uri("/users/{upn}", upn)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(MicrosoftApiUser.class);
                    } else if (response.statusCode().value() == 403) {
                        return Mono.just(
                                buildDummyUser(upn));
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });

    }

    private MicrosoftApiUser buildDummyUser(String upn) {
        return new MicrosoftApiUser(upn, "TODO");
    }

    public MicrosoftApiUser getMicrosoftApiUser(Authentication authentication, String userId) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken && userId != null) {

            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(oauthToken.getAuthorizedClientRegistrationId())
                    .principal(authentication)
                    .build();

            OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

            if (authorizedClient != null) {
                String accessToken = authorizedClient.getAccessToken().getTokenValue();
                return callMicrosoftGraphApi(userId, accessToken).block();
            }
        }
        return null;
    }
}
