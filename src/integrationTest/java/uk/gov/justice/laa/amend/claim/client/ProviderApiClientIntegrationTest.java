package uk.gov.justice.laa.amend.claim.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.contributor.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.amend.claim.base.WireMockSetup;
import uk.gov.justice.laa.amend.claim.models.HealthDto;

@SpringBootTest
public class ProviderApiClientIntegrationTest extends WireMockSetup {

    @Autowired
    private ProviderApiClient providerApiClient;

    @Test
    void testPingWhenUpResponse() {
        setupGetProviderDetailsApiHealthStub();

        Assertions.assertDoesNotThrow(() -> providerApiClient.ping().block());
    }

    @Test
    void testPingWhenDownResponse() {
        String response = """
            {
                "status": "DOWN"
            }\
            """;

        stubFor(get(urlPathMatching("/api/v1/actuator/health"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/vnd.spring-boot.actuator.v3+json")
                        .withBody(response)));

        HealthDto health = providerApiClient.ping().block();

        Assertions.assertNotNull(health);
        Assertions.assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    void testPingWhenServiceUnavailableResponse() {
        stubFor(get(urlPathMatching("/api/v1/actuator/health"))
                .willReturn(aResponse().withStatus(503)));

        Assertions.assertThrows(
                WebClientResponseException.class, () -> providerApiClient.ping().block());
        ;
    }
}
