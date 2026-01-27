package uk.gov.justice.laa.amend.claim.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.MicrosoftGraphApiClient;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRetrievalServiceTest {

    @Mock
    private MicrosoftGraphApiClient microsoftGraphApiClient;

    @Mock
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @InjectMocks
    private UserRetrievalService userRetrievalService;

    @Test
    void testGetMicrosoftApiUserReturnsUser() {
        // Arrange
        String userId = "test-user";
        String tokenValue = "dummy-token";

        MicrosoftApiUser user = new MicrosoftApiUser(userId, "User, Dummy", "Dummy", "User");

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

        // Mock WebClient chain
        when(microsoftGraphApiClient.getUser(anyString(), anyString())).thenReturn(Mono.just(user));

        // Act
        MicrosoftApiUser result = userRetrievalService.getMicrosoftApiUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(user, result);

        verify(microsoftGraphApiClient).getUser("test-user", "dummy-token");
    }

    @Test
    void testGetMicrosoftApiUserReturnsNullWhenNoClient() {
        // Arrange
        when(authorizedClientManager.authorize(any())).thenReturn(null);

        // Act
        MicrosoftApiUser result = userRetrievalService.getMicrosoftApiUser("test-user");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetMicrosoftApiUserWhenClientManagerThrowsException() {
        // Arrange
        when(authorizedClientManager.authorize(any())).thenThrow(new IllegalArgumentException("Could not find ClientRegistration with id 'test'"));

        // Act
        MicrosoftApiUser result = userRetrievalService.getMicrosoftApiUser("test-user");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetMicrosoftApiUserWhenClientThrowsException() {
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

        // Mock WebClient chain
        when(microsoftGraphApiClient.getUser(anyString(), anyString())).thenReturn(Mono.error(new Exception("Error")));

        // Act
        MicrosoftApiUser result = userRetrievalService.getMicrosoftApiUser(userId);

        // Assert
        assertNull(result);

        verify(microsoftGraphApiClient).getUser("test-user", "dummy-token");
    }

    @Test
    void testGetMicrosoftApiUserWhenClientReturnsEmptyResponse() {
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

        // Mock WebClient chain
        when(microsoftGraphApiClient.getUser(anyString(), anyString())).thenReturn(Mono.empty());

        // Act
        MicrosoftApiUser result = userRetrievalService.getMicrosoftApiUser(userId);

        // Assert
        assertNull(result);

        verify(microsoftGraphApiClient).getUser("test-user", "dummy-token");
    }
}

