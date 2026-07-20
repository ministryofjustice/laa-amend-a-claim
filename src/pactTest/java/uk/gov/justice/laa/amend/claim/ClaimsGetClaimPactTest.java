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
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"claims-api.url=http://localhost:1244"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.CLAIMS_API_PROVIDER)
@MockServerConfig(port = "1244")
@DisplayName("GET: /api/v2/submissions/{submissionId}/claims/{claimId} PACT tests")
public final class ClaimsGetClaimPactTest extends AbstractPactTest {

  @Autowired ClaimsApiClient claimsApiClient;

  private static final UUID SUBMISSION_ID = UUID.fromString("4fa85f64-5717-4562-b3fc-2c963f66afa6");

  @Pact(consumer = CONSUMER)
  public RequestResponsePact getClaim200(PactDslWithProvider builder) {
    return builder
        .given("a claim exists for the submission and claim IDs")
        .uponReceiving("a request to get a valid claim")
        .matchPath(
            "/api/v2/submissions/(" + UUID_REGEX + ")/claims/(" + UUID_REGEX + ")",
            "/api/v2/submissions/" + SUBMISSION_ID + "/claims/" + CLAIM_ID)
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(
            LambdaDsl.newJsonBody(
                    body -> {
                      body.stringMatcher("id", UUID_REGEX, CLAIM_ID.toString());
                      body.stringMatcher("submission_id", UUID_REGEX, SUBMISSION_ID.toString());
                      body.stringMatcher("status", "READY_TO_PROCESS|VALID|INVALID|VOID", "VALID");
                      body.stringMatcher(
                          "area_of_law", "CRIME LOWER|LEGAL HELP|MEDIATION", "LEGAL HELP");
                      body.stringType("office_code", "1A234B");
                      body.stringType("unique_file_number", "1A234B/001");
                      body.stringType("case_reference_number", "REF-001");
                      body.stringType("client_forename", "John");
                      body.stringType("client_surname", "Smith");
                      body.stringType("submission_period", "Jan-2024");
                      body.booleanType("has_assessment", true);
                      body.booleanType("is_vat_applicable", true);
                      body.datetime("date_submitted", "yyyy-MM-dd'T'HH:mm:ssXXX");
                      body.object(
                          "fee_calculation_response",
                          fee -> {
                            fee.stringMatcher(
                                "claim_summary_fee_id", UUID_REGEX, CLAIM_ID.toString());
                            fee.stringType("fee_code", "XXXX");
                            fee.stringType("fee_code_description", "A fee code description");
                            fee.stringType("category_of_law", "Legal Help");
                            fee.object(
                                "bolt_on_details",
                                bolt -> bolt.booleanType("escape_case_flag", false));
                          });
                    })
                .build())
        .toPact();
  }

  @Pact(consumer = CONSUMER)
  public RequestResponsePact getClaim404(PactDslWithProvider builder) {
    return builder
        .given("no claim exists for the submission and claim IDs")
        .uponReceiving("a request to get a non-existent claim")
        .matchPath(
            "/api/v2/submissions/(" + UUID_REGEX + ")/claims/(" + UUID_REGEX + ")",
            "/api/v2/submissions/" + SUBMISSION_ID + "/claims/" + CLAIM_ID)
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .method("GET")
        .willRespondWith()
        .status(404)
        .matchHeader("Content-Type", "application/(problem\\+)?json", "application/problem+json")
        .toPact();
  }

  @Test
  @DisplayName("Verify 200 response - claim found")
  @PactTestFor(pactMethod = "getClaim200")
  void verify200Response() {
    ClaimResponseV2 response = claimsApiClient.getClaim(SUBMISSION_ID, CLAIM_ID).block();

    assertThat(response).isNotNull();
    assertThat(response.getId()).isNotNull();
    assertThat(response.getSubmissionId()).isNotNull();
    assertThat(response.getAreaOfLaw()).isNotNull();
    assertThat(response.getOfficeCode()).isNotNull();
    assertThat(response.getFeeCalculationResponse()).isNotNull();
    assertThat(response.getFeeCalculationResponse().getClaimSummaryFeeId()).isNotNull();
  }

  @Test
  @DisplayName("Verify 404 response - claim does not exist")
  @PactTestFor(pactMethod = "getClaim404")
  void verify404Response() {
    assertThrows(NotFound.class, () -> claimsApiClient.getClaim(SUBMISSION_ID, CLAIM_ID).block());
  }
}
