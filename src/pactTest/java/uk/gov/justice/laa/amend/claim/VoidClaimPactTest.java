package uk.gov.justice.laa.amend.claim;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.VoidClaim201Response;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.VoidClaimRequest;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"claims-api.url=http://localhost:1240"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.PROVIDER)
@MockServerConfig(port = "1240")
@DisplayName("POST: /api/v1/claims/{claimId}/void PACT tests")
public final class VoidClaimPactTest extends AbstractPactTest {

    @Autowired
    ClaimsApiClient claimsApiClient;

    @Pact(consumer = CONSUMER)
    public RequestResponsePact voidClaim201(PactDslWithProvider builder) {
        return builder.given("a voidable claim exists")
                .uponReceiving("a request to void a valid claim")
                .matchPath("/api/v1/claims/(" + UUID_REGEX + ")/void", "/api/v1/claims/" + CLAIM_ID + "/void")
                .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
                .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
                .method("POST")
                .body(LambdaDsl.newJsonBody(VoidClaimPactTest::buildVoidClaimRequestBody)
                        .build())
                .willRespondWith()
                .status(201)
                .headers(Map.of("Content-Type", "application/json"))
                .body(LambdaDsl.newJsonBody(body -> {
                            body.uuid("id", UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
                        })
                        .build())
                .toPact();
    }

    @Pact(consumer = CONSUMER)
    public RequestResponsePact voidClaim404(PactDslWithProvider builder) {
        return builder.given("no claim exists")
                .uponReceiving("a request to void a non-existent claim")
                .matchPath("/api/v1/claims/(" + UUID_REGEX + ")/void", "/api/v1/claims/" + CLAIM_ID + "/void")
                .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
                .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
                .method("POST")
                .body(LambdaDsl.newJsonBody(VoidClaimPactTest::buildVoidClaimRequestBody)
                        .build())
                .willRespondWith()
                .status(404)
                .headers(Map.of("Content-Type", "application/json"))
                .toPact();
    }

    @Test
    @DisplayName("Verify 201 response - claim voided successfully")
    @PactTestFor(pactMethod = "voidClaim201")
    void verify201Response() {
        VoidClaimRequest request = new VoidClaimRequest(UUID.randomUUID(), "Void reason");
        VoidClaim201Response response =
                claimsApiClient.voidClaim(CLAIM_ID, request).block();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
    }

    @Test
    @DisplayName("Verify 404 response - claim does not exist")
    @PactTestFor(pactMethod = "voidClaim404")
    void verify404Response() {
        VoidClaimRequest request = new VoidClaimRequest(UUID.randomUUID(), "Void reason");
        assertThrows(
                NotFound.class,
                () -> claimsApiClient.voidClaim(CLAIM_ID, request).block());
    }

    private static void buildVoidClaimRequestBody(au.com.dius.pact.consumer.dsl.LambdaDslJsonBody body) {
        body.uuid("created_by_user_id");
        body.stringType("assessment_reason", "Void reason");
    }
}
