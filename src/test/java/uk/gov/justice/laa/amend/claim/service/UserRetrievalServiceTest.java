package uk.gov.justice.laa.amend.claim.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;

import java.time.Instant;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRetrievalServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private WebClient.RequestHeadersSpec headersSpec;

    @Mock
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @InjectMocks
    private UserRetrievalService userRetrievalService;

    @Test
    void testGetMicrosoftApiUserReturnsUser() {
        // Arrange
        String userId = "test-user";
        String tokenValue = "dummy-token";

        // Mock OAuth2AuthorizedClient
        OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(
                ClientRegistration.withRegistrationId("test")
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .clientId("client-id")
                        .authorizationUri("https://example.com/auth")
                        .tokenUri("https://example.com/token")
                        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                        .build(),
                "principalName",
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, tokenValue, Instant.now(), Instant.now().plusSeconds(3600))
        );

        when(authorizedClientManager.authorize(any())).thenReturn(client);

        // Mock Authentication
        OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class);
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("test");

        // Mock WebClient chain
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), eq(userId))).thenReturn(headersSpec);
        when(headersSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(headersSpec);

        String json = """
            {
              "id": "test-user",
              "displayName": "Dummy User",
              "userPrincipalName": "test-user@example.com"
            }
            """;

        ClientResponse okResponse = ClientResponse
                .create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .build();

        when(headersSpec.exchangeToMono(any())).thenAnswer(invocation -> {
            Function<ClientResponse, Mono<MicrosoftApiUser>> handler = invocation.getArgument(0);
            return handler.apply(okResponse);
        });


        // Act
        MicrosoftApiUser result = userRetrievalService.getMicrosoftApiUser(authentication, userId);

        // Assert
        assertNotNull(result);
        assertEquals("Dummy User", result.getDisplayName());
    }

    @Test
    void testGetMicrosoftApiUserReturnsNullWhenNoClient() {
        // Arrange
        OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class);
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("test");

        when(authorizedClientManager.authorize(any())).thenReturn(null);

        // Act
        MicrosoftApiUser result = userRetrievalService.getMicrosoftApiUser(authentication, "test-user");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetMicrosoftApiUserReturnsUserFor403StatusCode() {
        // Arrange
        String userId = "test-user";
        String tokenValue = "dummy-token";

        // Mock OAuth2AuthorizedClient
        OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(
                ClientRegistration.withRegistrationId("test")
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .clientId("client-id")
                        .authorizationUri("https://example.com/auth")
                        .tokenUri("https://example.com/token")
                        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                        .build(),
                "principalName",
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, tokenValue, Instant.now(), Instant.now().plusSeconds(3600))
        );

        when(authorizedClientManager.authorize(any())).thenReturn(client);

        // Mock Authentication
        OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class);
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("test");

        // Mock WebClient chain
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), eq(userId))).thenReturn(headersSpec);
        when(headersSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(headersSpec);

        String json = """
            {
              "id": "test-user",
              "displayName": "TODO",
            }
            """;

        ClientResponse forbiddenResponse = ClientResponse
                .create(HttpStatus.FORBIDDEN)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .build();

        when(headersSpec.exchangeToMono(any())).thenAnswer(invocation -> {
            Function<ClientResponse, Mono<MicrosoftApiUser>> handler = invocation.getArgument(0);
            return handler.apply(forbiddenResponse);
        });

        // Act
        MicrosoftApiUser result = userRetrievalService.getMicrosoftApiUser(authentication, userId);

        // Assert
        assertNotNull(result);
        assertEquals("TODO", result.getDisplayName());
    }



}

