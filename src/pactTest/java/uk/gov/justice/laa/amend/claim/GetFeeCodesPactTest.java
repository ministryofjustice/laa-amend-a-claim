package uk.gov.justice.laa.amend.claim;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.LambdaDslJsonBody;
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
import uk.gov.justice.laa.amend.claim.client.FeeSchemePlatformApiClient;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"fee-scheme-api.url=http://localhost:1242"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.FSP_API_PROVIDER)
@MockServerConfig(port = "1242")
@DisplayName("POST: /api/v1/fee-codes/{areaOfLaw} PACT tests")
public class GetFeeCodesPactTest extends AbstractPactTest {

  @Autowired FeeSchemePlatformApiClient feeSchemePlatformApiClient;

  protected static final LambdaDslJsonBody EXPECTED_FEE_CODES_RESULT =
      LambdaDsl.newJsonBody(
          body -> {
            body.minArrayLike(
                "feeCodes",
                1,
                feeCode -> {
                  feeCode.stringType("feeCode", "ABC");
                  feeCode.stringType("feeCodeDescription", "A valid fee code description");
                });
          });

  @Pact(consumer = CONSUMER)
  public RequestResponsePact getFeeCodes(PactDslWithProvider builder) {
    return builder
        .given("fee codes exist for the area of law")
        .uponReceiving("a request to get fee codes for a given area of law")
        .matchPath("/api/v1/fee-codes/(LEGAL_HELP|CRIME_LOWER|MEDIATION)")
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(EXPECTED_FEE_CODES_RESULT.build())
        .toPact();
  }

  @Test
  @DisplayName("Verify 200 response - fee codes exist for the area of law")
  @PactTestFor(pactMethod = "getFeeCodes")
  void verify200Response() {
    AreaOfLaw areaOfLaw = AreaOfLaw.LEGAL_HELP;

    var result = feeSchemePlatformApiClient.getFeeCodes(areaOfLaw.name()).block();

    assertThat(result).isNotNull();
    assertThat(result.feeCodes().size()).isGreaterThan(0);
    assertThat(result.feeCodes().get(0).feeCode()).isNotNull();
    assertThat(result.feeCodes().get(0).feeCodeDescription()).isNotNull();
    assertThat(result.feeCodes().get(0).feeCode()).isEqualTo("ABC");
    assertThat(result.feeCodes().get(0).feeCodeDescription())
        .isEqualTo("A valid fee code description");
  }
}
