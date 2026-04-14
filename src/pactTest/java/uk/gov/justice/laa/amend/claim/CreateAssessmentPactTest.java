package uk.gov.justice.laa.amend.claim;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.ASSESSMENT_REASON_ESCAPE_CASE;

import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentOutcome;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"claims-api.url=http://localhost:1242"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.PROVIDER)
@MockServerConfig(port = "1242")
@DisplayName("POST: /api/v1/claims/{claimId}/assessments PACT tests")
public final class CreateAssessmentPactTest extends AbstractPactTest {

  @Autowired ClaimsApiClient claimsApiClient;

  @Pact(consumer = CONSUMER)
  public RequestResponsePact createAssessment201(PactDslWithProvider builder) {
    return builder
        .given("a valid claim exists")
        .uponReceiving("a request to create an assessment for a valid claim")
        .matchPath(
            "/api/v1/claims/(" + UUID_REGEX + ")/assessments",
            "/api/v1/claims/" + CLAIM_ID + "/assessments")
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
        .method("POST")
        .body(LambdaDsl.newJsonBody(CreateAssessmentPactTest::buildAssessmentRequestBody).build())
        .willRespondWith()
        .status(201)
        .headers(Map.of("Content-Type", "application/json"))
        .body(
            LambdaDsl.newJsonBody(
                    body -> {
                      body.uuid("id", UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
                    })
                .build())
        .toPact();
  }

  @Pact(consumer = CONSUMER)
  public RequestResponsePact createAssessment404(PactDslWithProvider builder) {
    return builder
        .given("no claim exists")
        .uponReceiving("a request to create an assessment for a non-existent claim")
        .matchPath(
            "/api/v1/claims/(" + UUID_REGEX + ")/assessments",
            "/api/v1/claims/" + CLAIM_ID + "/assessments")
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
        .method("POST")
        .body(LambdaDsl.newJsonBody(CreateAssessmentPactTest::buildAssessmentRequestBody).build())
        .willRespondWith()
        .status(404)
        .matchHeader("Content-Type", "application/(problem\\+)?json", "application/problem+json")
        .toPact();
  }

  @Test
  @DisplayName("Verify 201 response - assessment created successfully")
  @PactTestFor(pactMethod = "createAssessment201")
  void verify201Response() {
    AssessmentPost assessment = buildAssessmentPost();

    ResponseEntity<CreateAssessment201Response> response =
        claimsApiClient.submitAssessment(CLAIM_ID, assessment).block();

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isNotNull();
  }

  @Test
  @DisplayName("Verify 404 response - claim does not exist")
  @PactTestFor(pactMethod = "createAssessment404")
  void verify404Response() {
    AssessmentPost assessment = buildAssessmentPost();

    assertThrows(
        NotFound.class, () -> claimsApiClient.submitAssessment(CLAIM_ID, assessment).block());
  }

  private static void buildAssessmentRequestBody(
      au.com.dius.pact.consumer.dsl.LambdaDslJsonBody body) {
    body.nullValue("id");
    body.uuid("claim_id");
    body.uuid("claim_summary_fee_id");
    body.stringType("assessment_outcome", "PAID_IN_FULL");
    body.uuid("created_by_user_id");
    body.stringType("assessment_reason", ASSESSMENT_REASON_ESCAPE_CASE);
    body.stringType("assessment_type", "ESCAPE_CASE_ASSESSMENT");
    body.booleanType("is_vat_applicable", true);
    body.decimalType("fixed_fee_amount", 100.00);
    body.decimalType("net_profit_costs_amount", 200.00);
    body.decimalType("disbursement_amount", 50.00);
    body.decimalType("disbursement_vat_amount", 10.00);
    body.decimalType("assessed_total_vat", 60.00);
    body.decimalType("assessed_total_incl_vat", 360.00);
    body.decimalType("allowed_total_vat", 60.00);
    body.decimalType("allowed_total_incl_vat", 360.00);
    body.nullValue("net_cost_of_counsel_amount");
    body.nullValue("net_travel_costs_amount");
    body.nullValue("net_waiting_costs_amount");
    body.nullValue("detention_travel_and_waiting_costs_amount");
    body.nullValue("jr_form_filling_amount");
    body.nullValue("bolt_on_adjourned_hearing_fee");
    body.nullValue("bolt_on_cmrh_oral_fee");
    body.nullValue("bolt_on_cmrh_telephone_fee");
    body.nullValue("bolt_on_home_office_interview_fee");
    body.nullValue("bolt_on_substantive_hearing_fee");
  }

  private static AssessmentPost buildAssessmentPost() {
    AssessmentPost assessment = new AssessmentPost();
    assessment.setClaimId(UUID.randomUUID());
    assessment.setClaimSummaryFeeId(UUID.randomUUID());
    assessment.setAssessmentOutcome(AssessmentOutcome.PAID_IN_FULL);
    assessment.setCreatedByUserId(UUID.randomUUID().toString());
    assessment.setIsVatApplicable(true);
    assessment.setFixedFeeAmount(new BigDecimal("100.00"));
    assessment.setNetProfitCostsAmount(new BigDecimal("200.00"));
    assessment.setDisbursementAmount(new BigDecimal("50.00"));
    assessment.setDisbursementVatAmount(new BigDecimal("10.00"));
    assessment.setAssessedTotalVat(new BigDecimal("60.00"));
    assessment.setAssessedTotalInclVat(new BigDecimal("360.00"));
    assessment.setAllowedTotalVat(new BigDecimal("60.00"));
    assessment.setAllowedTotalInclVat(new BigDecimal("360.00"));
    assessment.setAssessmentReason(ASSESSMENT_REASON_ESCAPE_CASE);
    assessment.setAssessmentType(AssessmentType.ESCAPE_CASE_ASSESSMENT);
    return assessment;
  }
}
