package uk.gov.justice.laa.amend.claim;

import static org.junit.jupiter.api.Assertions.assertThrows;

import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.client.VoidClaimRequest;
import uk.gov.justice.laa.amend.claim.config.ClaimsApiPactTestConfig;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"claims-api.url=http://localhost:1241"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.PROVIDER)
@MockServerConfig(port = "1241")
@Import(ClaimsApiPactTestConfig.class)
@DisplayName("POST: /api/v1/claims/{claimId}/void - 404 PACT test")
public final class VoidClaimNotFoundPactTest extends AbstractPactTest {

    @Autowired
    ClaimsApiClient claimsApiClient;

    @Pact(consumer = CONSUMER)
    public RequestResponsePact voidClaim404NoClaimExists(PactDslWithProvider builder) {
        return builder.given("no claim exists")
                .uponReceiving("a request to void a non-existent claim")
                .matchPath("/api/v1/claims/(" + UUID_REGEX + ")/void")
                .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX)
                .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
                .method("POST")
                .body(LambdaDsl.newJsonBody(body -> {
                            body.uuid("created_by_user_id");
                            body.stringType("assessment_reason", "Void reason");
                        })
                        .build())
                .willRespondWith()
                .status(404)
                .headers(Map.of("Content-Type", "application/json"))
                .toPact();
    }

    @Test
    @DisplayName("Verify 404 response - claim does not exist")
    @PactTestFor(pactMethod = "voidClaim404NoClaimExists")
    void verify404Response() {
        VoidClaimRequest request = new VoidClaimRequest(UUID.randomUUID(), "Void reason");
        assertThrows(
                NotFound.class,
                () -> claimsApiClient.voidClaim(CLAIM_ID.toString(), request).block());
    }
}
