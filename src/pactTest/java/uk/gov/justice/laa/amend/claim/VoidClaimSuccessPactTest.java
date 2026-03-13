package uk.gov.justice.laa.amend.claim;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.client.VoidClaim201Response;
import uk.gov.justice.laa.amend.claim.client.VoidClaimRequest;
import uk.gov.justice.laa.amend.claim.config.ClaimsApiPactTestConfig;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"claims-api.url=http://localhost:1240"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.PROVIDER)
@MockServerConfig(port = "1240")
@Import(ClaimsApiPactTestConfig.class)
@DisplayName("POST: /api/v1/claims/{claimId}/void - 201 PACT test")
public final class VoidClaimSuccessPactTest extends AbstractPactTest {

    @Autowired
    ClaimsApiClient claimsApiClient;

    @Pact(consumer = CONSUMER)
    public RequestResponsePact voidClaim201(PactDslWithProvider builder) {
        return builder.given("a claim exists")
                .uponReceiving("a request to void a valid claim")
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
                .status(201)
                .headers(Map.of("Content-Type", "application/json"))
                .body(LambdaDsl.newJsonBody(body -> {
                            body.uuid("id", UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
                        })
                        .build())
                .toPact();
    }

    @Test
    @DisplayName("Verify 201 response - claim voided successfully")
    @PactTestFor(pactMethod = "voidClaim201")
    void verify201Response() {
        VoidClaimRequest request = new VoidClaimRequest(UUID.randomUUID(), "Void reason");
        ResponseEntity<VoidClaim201Response> response =
                claimsApiClient.voidClaim(CLAIM_ID.toString(), request).block();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }
}
