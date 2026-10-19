package uk.gov.justice.laa.payments.amend;

import static org.assertj.core.api.Assertions.assertThat;

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
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReferenceList;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"claims-api.url=http://localhost:1249"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.CLAIMS_API_PROVIDER)
@MockServerConfig(port = "1249")
@DisplayName("GET: /api/v1/system/references/amendment-requested-by PACT tests")
public final class ClaimsGetAmendmentRequestedByPactTest extends AbstractPactTest {

  private static final String PATH = "/api/v1/system/references/amendment-requested-by";

  @Autowired ClaimsApiClient claimsApiClient;

  @Pact(consumer = CONSUMER)
  public RequestResponsePact getAmendmentRequestedBy200(PactDslWithProvider builder) {
    return builder
        .given("amendment reference data exists")
        .uponReceiving("a request to get the amendment requested by reference list")
        .matchPath(PATH)
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(
            LambdaDsl.newJsonBody(
                    body ->
                        body.minArrayLike(
                            "requested_by",
                            1,
                            requestedBy -> {
                              requestedBy.stringType("code", "PROVIDER");
                              requestedBy.stringType("display_label", "Provider");
                              requestedBy.integerType("display_order", 10);
                              requestedBy.booleanType("is_active", true);
                              requestedBy.minArrayLike(
                                  "reasons",
                                  1,
                                  reason -> {
                                    reason.stringType("code", "PROVIDER_ERROR");
                                    reason.stringType("display_label", "Provider error");
                                    reason.integerType("display_order", 10);
                                    reason.booleanType("is_active", true);
                                  });
                            }))
                .build())
        .toPact();
  }

  @Test
  @DisplayName("Verify 200 response - reference data found")
  @PactTestFor(pactMethod = "getAmendmentRequestedBy200")
  void verify200Response() {
    AmendmentRequestedByReferenceList result =
        claimsApiClient.getAmendmentRequestedByReferenceList().block();

    assertThat(result).isNotNull();
    assertThat(result.getRequestedBy()).isNotEmpty();
    var requestedBy = result.getRequestedBy().get(0);
    assertThat(requestedBy.getCode()).isNotBlank();
    assertThat(requestedBy.getDisplayLabel()).isNotBlank();
    assertThat(requestedBy.getIsActive()).isNotNull();
    assertThat(requestedBy.getReasons()).isNotEmpty();
    assertThat(requestedBy.getReasons().get(0).getCode()).isNotBlank();
  }
}
