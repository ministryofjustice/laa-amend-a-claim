package uk.gov.justice.laa.amend.claim;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.config.ClaimsApiPactTestConfig;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentOutcome;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"claims-api.url=http://localhost:1243"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.PROVIDER)
@MockServerConfig(port = "1243")
@Import(ClaimsApiPactTestConfig.class)
@DisplayName("POST: /api/v1/claims/{claimId}/assessments - 404 PACT test")
public final class CreateAssessmentBadRequestPactTest extends AbstractPactTest {

    @Autowired
    ClaimsApiClient claimsApiClient;

    @Pact(consumer = CONSUMER)
    public RequestResponsePact createAssessment404(PactDslWithProvider builder) {
        return builder.given("no claim exists")
                .uponReceiving("a request to create an assessment for a non-existent claim")
                .matchPath("/api/v1/claims/(" + UUID_REGEX + ")/assessments")
                .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX)
                .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
                .method("POST")
                .body(LambdaDsl.newJsonBody(body -> {
                            body.nullValue("id");
                            body.uuid("claim_id");
                            body.uuid("claim_summary_fee_id");
                            body.stringType("assessment_outcome", "PAID_IN_FULL");
                            body.uuid("created_by_user_id");
                            body.nullValue("assessment_reason");
                            body.nullValue("assessment_type");
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
                        })
                        .build())
                .willRespondWith()
                .status(404)
                .headers(Map.of("Content-Type", "application/json"))
                .toPact();
    }

    @Test
    @DisplayName("Verify 404 response - claim does not exist")
    @PactTestFor(pactMethod = "createAssessment404")
    void verify404Response() {
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

        assertThrows(
                NotFound.class,
                () -> claimsApiClient
                        .submitAssessment(CLAIM_ID.toString(), assessment)
                        .block());
    }
}
