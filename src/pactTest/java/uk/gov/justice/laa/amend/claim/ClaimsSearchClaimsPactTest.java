package uk.gov.justice.laa.amend.claim;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSetV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"claims-api.url=http://localhost:1243"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.CLAIMS_API_PROVIDER)
@MockServerConfig(port = "1243")
@DisplayName("GET: /api/v2/claims PACT tests")
public final class ClaimsSearchClaimsPactTest extends AbstractPactTest {

  @Autowired ClaimsApiClient claimsApiClient;

  @Pact(consumer = CONSUMER)
  public RequestResponsePact searchClaims200(PactDslWithProvider builder) {
    return builder
        .given("claims exist for the office code")
        .uponReceiving("a request to search claims for a valid office code")
        .matchPath("/api/v2/claims", "/api/v2/claims")
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .matchQuery("office_code", OFFICE_CODE_REGEX, "1A234B")
        .matchQuery("unique_file_number", ".*", "")
        .matchQuery("case_reference_number", ".*", "")
        .matchQuery("submission_period", ".*", "")
        .matchQuery("area_of_law", ".*", "LEGAL_HELP")
        .matchQuery("page", "\\d+", "0")
        .matchQuery("size", "\\d+", "25")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(
            LambdaDsl.newJsonBody(
                    body -> {
                      body.integerType("total_elements", 1);
                      body.integerType("total_pages", 1);
                      body.integerType("number", 0);
                      body.integerType("size", 25);
                      body.minArrayLike(
                          "content",
                          1,
                          claim -> {
                            claim.stringMatcher("id", UUID_REGEX, CLAIM_ID.toString());
                            claim.stringMatcher("submission_id", UUID_REGEX, CLAIM_ID.toString());
                            claim.stringType("office_code", "1A234B");
                            claim.stringType("unique_file_number", "1A234B/001");
                            claim.stringType("case_reference_number", "REF-001");
                            claim.stringType("client_forename", "John");
                            claim.stringType("client_surname", "Smith");
                            claim.stringType("submission_period", "Jan-2024");
                            claim.stringMatcher(
                                "status", "READY_TO_PROCESS|VALID|INVALID|VOID", "VALID");
                            claim.stringMatcher(
                                "area_of_law", "CRIME LOWER|LEGAL HELP|MEDIATION", "LEGAL HELP");
                            claim.booleanType("has_assessment", false);
                            claim.booleanType("is_vat_applicable", true);
                            claim.object(
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
                          });
                    })
                .build())
        .toPact();
  }

  @Pact(consumer = CONSUMER)
  public RequestResponsePact searchClaims200Empty(PactDslWithProvider builder) {
    return builder
        .given("no claims exist for the office code")
        .uponReceiving("a request to search claims for an office code with no results")
        .matchPath("/api/v2/claims", "/api/v2/claims")
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .matchQuery("office_code", OFFICE_CODE_REGEX, "1A234B")
        .matchQuery("unique_file_number", ".*", "")
        .matchQuery("case_reference_number", ".*", "")
        .matchQuery("submission_period", ".*", "")
        .matchQuery("area_of_law", ".*", "LEGAL_HELP")
        .matchQuery("claim_statuses", "READY_TO_PROCESS|VALID|INVALID|VOID", "VALID")
        .matchQuery("page", "\\d+", "0")
        .matchQuery("size", "\\d+", "25")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(
            LambdaDsl.newJsonBody(
                    body -> {
                      body.integerType("total_elements", 0);
                      body.integerType("total_pages", 0);
                      body.integerType("number", 0);
                      body.integerType("size", 25);
                      body.array("content");
                    })
                .build())
        .toPact();
  }

  @Test
  @DisplayName("Verify 200 response - claims found")
  @PactTestFor(pactMethod = "searchClaims200")
  void verify200ResponseWithResults() {
    ClaimResultSetV2 result =
        claimsApiClient
            .searchClaims(
                "1A234B",
                null,
                null,
                null,
                AreaOfLaw.LEGAL_HELP,
                null,
                List.of(),
                null,
                0,
                25,
                null)
            .block();

    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isGreaterThan(0);
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getContent()).isNotEmpty();
    assertThat(result.getContent().get(0).getId()).isNotNull();
    assertThat(result.getContent().get(0).getSubmissionId()).isNotNull();
    assertThat(result.getContent().get(0).getOfficeCode()).isNotNull();
    assertThat(result.getContent().get(0).getFeeCalculationResponse()).isNotNull();
  }

  @Test
  @DisplayName("Verify 200 response - no claims found")
  @PactTestFor(pactMethod = "searchClaims200Empty")
  void verify200ResponseEmpty() {
    ClaimResultSetV2 result =
        claimsApiClient
            .searchClaims(
                "1A234B",
                null,
                null,
                null,
                AreaOfLaw.LEGAL_HELP,
                null,
                List.of(ClaimStatus.VALID),
                null,
                0,
                25,
                null)
            .block();

    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(0);
    assertThat(result.getContent()).isNullOrEmpty();
  }
}
