package uk.gov.justice.laa.payments.amend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryResultSet;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"claims-api.url=http://localhost:1246"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.CLAIMS_API_PROVIDER)
@MockServerConfig(port = "1246")
@DisplayName("GET: /api/v1/claims/{claimId}/history PACT tests")
public final class ClaimsGetClaimHistoryPactTest extends AbstractPactTest {

  @Autowired ClaimsApiClient claimsApiClient;

  @Pact(consumer = CONSUMER)
  public RequestResponsePact getClaimHistory200(PactDslWithProvider builder) {
    return builder
        .given("a claim history with an amendment event exists")
        .uponReceiving("a request to get claim history for a valid claim")
        .matchPath(
            "/api/v1/claims/(" + UUID_REGEX + ")/history",
            "/api/v1/claims/" + CLAIM_ID + "/history")
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(
            LambdaDsl.newJsonBody(
                    body -> {
                      body.uuid("claim_id", CLAIM_ID);
                      body.arrayContaining(
                          "events",
                          events -> {
                            events.object(
                                amendmentEvent -> {
                                  amendmentEvent.stringMatcher(
                                      "event_type", "AMENDMENT", "AMENDMENT");
                                  amendmentEvent.datetime(
                                      "event_timestamp", "yyyy-MM-dd'T'HH:mm:ssXXX");
                                  amendmentEvent.stringType("actor_id", "user-123");
                                  amendmentEvent.uuid("source_id");
                                  amendmentEvent.object(
                                      "metadata",
                                      metadata ->
                                          metadata.arrayContaining(
                                              "changes",
                                              changes ->
                                                  changes.object(
                                                      change -> {
                                                        change.stringType(
                                                            "field_identifier", "fee.schemeId");
                                                        change.nullValue("before");
                                                        change.stringType("after", "SCHEME-TEST");
                                                        change.stringMatcher(
                                                            "change_source",
                                                            "REQUESTED|FSP",
                                                            "FSP");
                                                      })));
                                });
                            events.object(
                                submissionEvent -> {
                                  submissionEvent.stringMatcher(
                                      "event_type", "SUBMISSION", "SUBMISSION");
                                  submissionEvent.datetime(
                                      "event_timestamp", "yyyy-MM-dd'T'HH:mm:ssXXX");
                                  submissionEvent.stringType("actor_id", "user-123");
                                  submissionEvent.uuid("source_id");
                                  submissionEvent.object(
                                      "metadata",
                                      metadata -> {
                                        metadata.stringType("submission_period", "APR-2025");
                                        metadata.stringType("office_account_number", "OFF_123");
                                        metadata.stringType("area_of_law", "LEGAL HELP");
                                      });
                                });
                          });
                    })
                .build())
        .toPact();
  }

  @Pact(consumer = CONSUMER)
  public RequestResponsePact getClaimHistory404(PactDslWithProvider builder) {
    return builder
        .given("no claim exists")
        .uponReceiving("a request to get claim history for a non-existent claim")
        .matchPath(
            "/api/v1/claims/(" + UUID_REGEX + ")/history",
            "/api/v1/claims/" + CLAIM_ID + "/history")
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .method("GET")
        .willRespondWith()
        .status(404)
        .matchHeader("Content-Type", "application/(problem\\+)?json", "application/problem+json")
        .toPact();
  }

  @Test
  @DisplayName("Verify 200 response - claim history found")
  @PactTestFor(pactMethod = "getClaimHistory200")
  void verify200Response() {
    ClaimHistoryResultSet result = claimsApiClient.getClaimHistory(CLAIM_ID).block();

    assertThat(result).isNotNull();
    assertThat(result.getClaimId()).isEqualTo(CLAIM_ID);
    assertThat(result.getEvents()).isNotNull();
    assertThat(result.getEvents()).isNotEmpty();
    assertThat(result.getEvents())
        .extracting(event -> event.getEventType().getValue())
        .contains("AMENDMENT", "SUBMISSION");
    assertThat(result.getEvents())
        .allSatisfy(
            event -> {
              assertThat(event.getEventTimestamp()).isNotNull();
              assertThat(event.getActorId()).isNotBlank();
              assertThat(event.getSourceId()).isNotNull();
              assertThat(event.getMetadata()).isNotNull();
            });

    var amendmentEvent =
        result.getEvents().stream()
            .filter(event -> event.getEventType() == ClaimHistoryEventType.AMENDMENT)
            .findFirst()
            .orElseThrow();
    assertThat(amendmentEvent.getMetadata()).containsKey("changes");
    assertThat(amendmentEvent.getMetadata().get("changes")).isInstanceOf(List.class);

    @SuppressWarnings("unchecked")
    var changes = (List<Map<String, Object>>) amendmentEvent.getMetadata().get("changes");
    assertThat(changes).isNotEmpty();
    assertThat(changes)
        .anySatisfy(
            change ->
                assertThat(change)
                    .containsKeys("field_identifier", "before", "after", "change_source")
                    .containsEntry("before", null));
    assertThat(changes)
        .allSatisfy(change -> assertThat(change.get("change_source")).isIn("REQUESTED", "FSP"));

    var submissionEvent =
        result.getEvents().stream()
            .filter(event -> event.getEventType() == ClaimHistoryEventType.SUBMISSION)
            .findFirst()
            .orElseThrow();
    assertThat(submissionEvent.getMetadata())
        .containsKeys("submission_period", "office_account_number", "area_of_law");
  }

  @Test
  @DisplayName("Verify 404 response - claim does not exist")
  @PactTestFor(pactMethod = "getClaimHistory404")
  void verify404Response() {
    assertThrows(NotFound.class, () -> claimsApiClient.getClaimHistory(CLAIM_ID).block());
  }
}
