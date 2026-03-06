package uk.gov.justice.laa.amend.claim;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.config.ClaimsApiPactTestConfig;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentOutcome;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"claims-api.url=http://localhost:1242"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.PROVIDER)
@MockServerConfig(port = "1242")
@Import(ClaimsApiPactTestConfig.class)
@DisplayName("POST: /api/v1/claims/{claimId}/assessments - 201 PACT test")
public final class CreateAssessmentSuccessPactTest extends AbstractPactTest {

    @Autowired
    ClaimsApiClient claimsApiClient;

    @Pact(consumer = CONSUMER)
    public RequestResponsePact createAssessment201(PactDslWithProvider builder) {
        return builder.given("a claim exists")
                .uponReceiving("a request to create an assessment for a valid claim")
                .matchPath("/api/v1/claims/(" + UUID_REGEX + ")/assessments")
                .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX)
                .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
                .method("POST")
                .body(LambdaDsl.newJsonBody(body -> {
                            body.uuid("claimId");
                            body.uuid("claimSummaryFeeId");
                            body.stringType("assessmentOutcome", "PAID_IN_FULL");
                            body.stringType("createdByUserId", "user-123");
                            body.booleanType("isVatApplicable", true);
                            body.decimalType("fixedFeeAmount", 100.00);
                            body.decimalType("netProfitCostsAmount", 200.00);
                            body.decimalType("disbursementAmount", 50.00);
                            body.decimalType("disbursementVatAmount", 10.00);
                            body.decimalType("assessedTotalVat", 60.00);
                            body.decimalType("assessedTotalInclVat", 360.00);
                            body.decimalType("allowedTotalVat", 60.00);
                            body.decimalType("allowedTotalInclVat", 360.00);
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
    @DisplayName("Verify 201 response - assessment created successfully")
    @PactTestFor(pactMethod = "createAssessment201")
    void verify201Response() {
        AssessmentPost assessment = new AssessmentPost();
        assessment.setClaimId(UUID.randomUUID());
        assessment.setClaimSummaryFeeId(UUID.randomUUID());
        assessment.setAssessmentOutcome(AssessmentOutcome.PAID_IN_FULL);
        assessment.setCreatedByUserId("user-123");
        assessment.setIsVatApplicable(true);
        assessment.setFixedFeeAmount(new BigDecimal("100.00"));
        assessment.setNetProfitCostsAmount(new BigDecimal("200.00"));
        assessment.setDisbursementAmount(new BigDecimal("50.00"));
        assessment.setDisbursementVatAmount(new BigDecimal("10.00"));
        assessment.setAssessedTotalVat(new BigDecimal("60.00"));
        assessment.setAssessedTotalInclVat(new BigDecimal("360.00"));
        assessment.setAllowedTotalVat(new BigDecimal("60.00"));
        assessment.setAllowedTotalInclVat(new BigDecimal("360.00"));

        ResponseEntity<CreateAssessment201Response> response =
                claimsApiClient.submitAssessment(CLAIM_ID.toString(), assessment).block();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }
}
