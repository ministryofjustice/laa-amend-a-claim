package uk.gov.justice.laa.payments.amend;

import static org.junit.jupiter.api.Assertions.assertThrows;

import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.LambdaDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimAmendmentPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"claims-api.url=http://localhost:1248"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.CLAIMS_API_PROVIDER)
@MockServerConfig(port = "1248")
@DisplayName("PATCH: /api/v1/submissions/{submissionId}/claims/{claimId} PACT tests")
public final class ClaimsUpdateClaimPactTest extends AbstractPactTest {

  @Autowired ClaimsApiClient claimsApiClient;

  @Pact(consumer = CONSUMER)
  public RequestResponsePact updateClaim204(PactDslWithProvider builder) {
    return builder
        .given("the system is ready to update a claim")
        .given("amendment reference data exists")
        .uponReceiving("a request to update a valid claim")
        .matchPath(
            "/api/v1/submissions/(" + UUID_REGEX + ")/claims/(" + UUID_REGEX + ")",
            "/api/v1/submissions/" + SUBMISSION_ID + "/claims/" + CLAIM_ID)
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
        .method("PATCH")
        .body(LambdaDsl.newJsonBody(ClaimsUpdateClaimPactTest::buildUpdateRequestBody).build())
        .willRespondWith()
        .status(204)
        .toPact();
  }

  @Pact(consumer = CONSUMER)
  public RequestResponsePact updateClaim404(PactDslWithProvider builder) {
    return builder
        .given("no claim exists")
        .uponReceiving("a request to update a non-existent claim")
        .matchPath(
            "/api/v1/submissions/(" + UUID_REGEX + ")/claims/(" + UUID_REGEX + ")",
            "/api/v1/submissions/" + SUBMISSION_ID + "/claims/" + CLAIM_ID)
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
        .method("PATCH")
        .body(LambdaDsl.newJsonBody(ClaimsUpdateClaimPactTest::buildUpdateRequestBody).build())
        .willRespondWith()
        .status(404)
        .matchHeader("Content-Type", "application/(problem\\+)?json", "application/problem+json")
        .toPact();
  }

  @Pact(consumer = CONSUMER)
  public RequestResponsePact updateClaim204WithClearedAmendmentField(PactDslWithProvider builder) {
    return builder
        .given("the system is ready to update a claim")
        .uponReceiving("a request to update a valid claim with a cleared amendment field")
        .matchPath(
            "/api/v1/submissions/(" + UUID_REGEX + ")/claims/(" + UUID_REGEX + ")",
            "/api/v1/submissions/" + SUBMISSION_ID + "/claims/" + CLAIM_ID)
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
        .method("PATCH")
        .body(
            LambdaDsl.newJsonBody(ClaimsUpdateClaimPactTest::buildUpdateWithClearedFieldRequestBody)
                .build())
        .willRespondWith()
        .status(204)
        .toPact();
  }

  @Test
  @DisplayName("Verify 204 response - claim updated successfully")
  @PactTestFor(pactMethod = "updateClaim204")
  void verify204Response() {
    var update = buildUpdatePost();

    claimsApiClient.updateClaim(SUBMISSION_ID, CLAIM_ID, update).block();
  }

  @Test
  @DisplayName("Verify 404 response - claim does not exist")
  @PactTestFor(pactMethod = "updateClaim404")
  void verify404Response() {
    var update = buildUpdatePost();

    assertThrows(
        NotFound.class, () -> claimsApiClient.updateClaim(SUBMISSION_ID, CLAIM_ID, update).block());
  }

  @Test
  @DisplayName("Verify 204 response - claim updated with cleared amendment field")
  @PactTestFor(pactMethod = "updateClaim204WithClearedAmendmentField")
  void verify204ResponseWithClearedAmendmentField() {
    var update = buildUpdatePostWithClearedField();

    claimsApiClient.updateClaim(SUBMISSION_ID, CLAIM_ID, update).block();
  }

  private static void buildUpdateRequestBody(LambdaDslJsonBody body) {
    body.stringType("status", "VALID");
    body.nullValue("total_warnings");
    body.nullValue("fee_calculation_response");
    body.array("validation_messages");
  }

  private static void buildUpdateWithClearedFieldRequestBody(LambdaDslJsonBody body) {
    buildUpdateRequestBody(body);
    body.nullValue("delivery_location");
  }

  private static ClaimAmendmentPatch buildUpdatePost() {
    return ClaimAmendmentPatch.builder().status(ClaimStatus.VALID).build();
  }

  private static ClaimAmendmentPatch buildUpdatePostWithClearedField() {
    return ClaimAmendmentPatch.builder()
        .status(ClaimStatus.VALID)
        .deliveryLocation(JsonNullable.of(null))
        .build();
  }
}
