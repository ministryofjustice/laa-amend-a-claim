package uk.gov.justice.laa.amend.claim.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.amend.claim.base.WireMockSetup;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@SpringBootTest
public class MicrosoftGraphApiClientIntegrationTest extends WireMockSetup {

    @Autowired
    private MicrosoftGraphApiClient microsoftGraphApiClient;

    @Test
    void testGetUserWhenOkResponse() {
        String response = """
            {
                "id": "test-user",
                "displayName": "Dummy User",
                "userPrincipalName": "test-user@example.com"
            }""";

        stubFor(get(urlPathMatching("/v1.0/users/abc"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(response)));

        MicrosoftApiUser user = microsoftGraphApiClient.getUser("abc", "123").block();

        Assertions.assertNotNull(user);
        Assertions.assertEquals("test-user", user.getId());
        Assertions.assertEquals("Dummy User", user.getDisplayName());
    }

    @Test
    void testGetUserWhenOkResponseWithInvalidBody() {
        String response = """
            {
                "foo": "bar"
            }""";

        stubFor(get(urlPathMatching("/v1.0/users/abc"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Authorization", "123")
                .withHeader("Content-Type", "application/json")
                .withBody(response)));

        MicrosoftApiUser user = microsoftGraphApiClient.getUser("abc", "123").block();

        Assertions.assertNotNull(user);
        Assertions.assertNull(user.getId());
        Assertions.assertNull(user.getDisplayName());
    }

    @Test
    void testGetUserWhen400Response() {
        stubFor(get(urlPathMatching("/v1.0/users/abc"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Authorization", "123")
                .withHeader("Content-Type", "application/json")));

        Assertions.assertThrows(
            WebClientResponseException.class,
            () -> microsoftGraphApiClient.getUser("abc", "123").block()
        );
    }

    @Test
    void testGetUserWhen403Response() {
        stubFor(get(urlPathMatching("/v1.0/users/abc"))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Authorization", "123")
                .withHeader("Content-Type", "application/json")));

        Assertions.assertThrows(
            WebClientResponseException.class,
            () -> microsoftGraphApiClient.getUser("abc", "123").block()
        );
    }

    @Test
    void testGetUserWhen500Response() {
        stubFor(get(urlPathMatching("/v1.0/users/abc"))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Authorization", "123")
                .withHeader("Content-Type", "application/json")));

        Assertions.assertThrows(
            WebClientResponseException.class,
            () -> microsoftGraphApiClient.getUser("abc", "123").block()
        );
    }
}
