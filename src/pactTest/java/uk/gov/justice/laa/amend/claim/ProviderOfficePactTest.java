package uk.gov.justice.laa.amend.claim;

import static org.assertj.core.api.Assertions.assertThat;
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
import uk.gov.justice.laa.amend.claim.client.ProviderApiClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"provider-api.url=http://localhost:1247"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.PROVIDER_API_PROVIDER)
@MockServerConfig(port = "1247")
@DisplayName("GET: /api/v1/provider-offices/{officeCode} PACT tests")
public class ProviderOfficePactTest extends AbstractPactTest {

  @Autowired ProviderApiClient providerApiClient;

  protected static final LambdaDslJsonBody EXPECTED_PROVIDER_RESULT =
      LambdaDsl.newJsonBody(
          body -> {
            body.object(
                "firm",
                firm -> {
                  firm.stringType("firmName", "A valid office name");
                });
          });

  @Pact(consumer = CONSUMER)
  public RequestResponsePact getProviderOffice(PactDslWithProvider builder) {
    return builder
        .given("provider office exists for the given office code")
        .uponReceiving("a request to get a provider office for a given office code")
        .matchPath("/api/v1/provider-offices/(%s)".formatted(OFFICE_CODE_REGEX))
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(EXPECTED_PROVIDER_RESULT.build())
        .toPact();
  }

  @Test
  @DisplayName("Verify 200 response - provider office exists for the office code")
  @PactTestFor(pactMethod = "getProviderOffice")
  void verify200Response() {
    String officeCode = "0P322F";
    var result = providerApiClient.getProviderOffice(officeCode).block();

    assertThat(result).isNotNull();
    assertThat(result.getFirm()).isNotNull();
    assertThat(result.getFirm().getFirmName()).isEqualTo("A valid office name");
  }
}
