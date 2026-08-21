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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentResultSet;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"claims-api.url=http://localhost:1242"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.CLAIMS_API_PROVIDER)
@MockServerConfig(port = "1242")
@DisplayName("GET: /api/v1/claims/{claimId}/assessments PACT tests")
public final class ClaimsGetAssessmentsPactTest extends AbstractPactTest {

  @Autowired ClaimsApiClient claimsApiClient;

  @Pact(consumer = CONSUMER)
  public RequestResponsePact getAssessments200(PactDslWithProvider builder) {
    return builder
        .given("assessments exist for the claim")
        .uponReceiving("a request to get assessments for a valid claim")
        .matchPath(
            "/api/v1/claims/(" + UUID_REGEX + ")/assessments",
            "/api/v1/claims/" + CLAIM_ID + "/assessments")
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .matchQuery("page", "\\d+", "0")
        .matchQuery("size", "\\d+", "5")
        .matchQuery("sort", ".*", "createdOn,desc")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(
            LambdaDsl.newJsonBody(
                    body -> {
                      body.integerType("total_elements", 1);
                      body.minArrayLike(
                          "assessments",
                          1,
                          assessment -> {
                            assessment.uuid("id");
                            assessment.stringMatcher(
                                "assessment_type",
                                "ESCAPE_CASE_ASSESSMENT|STAGE_DISBURSEMENT_ASSESSMENT|VOID",
                                "ESCAPE_CASE_ASSESSMENT");
                            assessment.stringMatcher(
                                "assessment_outcome",
                                "PAID_IN_FULL|REDUCED_STILL_ESCAPED|REDUCED_TO_FIXED_FEE|NILLED",
                                "PAID_IN_FULL");
                            assessment.stringType("assessment_reason", "ESCAPE_CASE");
                            assessment.stringType("created_by_user_id", "user-123");
                            assessment.datetime("created_on", "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
                          });
                    })
                .build())
        .toPact();
  }

  @Pact(consumer = CONSUMER)
  public RequestResponsePact getAssessments404(PactDslWithProvider builder) {
    return builder
        .given("no claim exists")
        .uponReceiving("a request to get assessments for a non-existent claim")
        .matchPath(
            "/api/v1/claims/(" + UUID_REGEX + ")/assessments",
            "/api/v1/claims/" + CLAIM_ID + "/assessments")
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .matchQuery("page", "\\d+", "0")
        .matchQuery("size", "\\d+", "5")
        .matchQuery("sort", ".*", "createdOn,desc")
        .method("GET")
        .willRespondWith()
        .status(404)
        .matchHeader("Content-Type", "application/(problem\\+)?json", "application/problem+json")
        .toPact();
  }

  @Test
  @DisplayName("Verify 200 response - assessments found")
  @PactTestFor(pactMethod = "getAssessments200")
  void verify200Response() {
    AssessmentResultSet result =
        claimsApiClient.getAssessments(CLAIM_ID, 0, 5, "createdOn,desc").block();

    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isGreaterThan(0);
    assertThat(result.getAssessments()).isNotNull();
    assertThat(result.getAssessments()).isNotEmpty();
    assertThat(result.getAssessments().get(0).getId()).isNotNull();
    assertThat(result.getAssessments().get(0).getAssessmentType()).isNotNull();
    assertThat(result.getAssessments().get(0).getAssessmentOutcome()).isNotNull();
    assertThat(result.getAssessments().get(0).getCreatedByUserId()).isNotNull();
    assertThat(result.getAssessments().get(0).getCreatedOn()).isNotNull();
  }

  @Test
  @DisplayName("Verify 404 response - claim does not exist")
  @PactTestFor(pactMethod = "getAssessments404")
  void verify404Response() {
    assertThrows(
        NotFound.class,
        () -> claimsApiClient.getAssessments(CLAIM_ID, 0, 5, "createdOn,desc").block());
  }
}
