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
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimAmendmentPatch;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"claims-api.url=http://localhost:1248"})
@PactConsumerTest
@PactTestFor(providerName = AbstractPactTest.CLAIMS_API_PROVIDER)
@MockServerConfig(port = "1248")
@DisplayName("PATCH: /api/v1/submissions/{submissionId}/claims/{claimId} PACT tests")
public final class ClaimsUpdateClaimPactTest extends AbstractPactTest {

  @Autowired ClaimsApiClient claimsApiClient;

  @Pact(consumer = CONSUMER)
  public RequestResponsePact updateClaim204(PactDslWithProvider builder) {
    return builder
        .given("a valid claim exists")
        .uponReceiving("a request to update a valid claim")
        .matchPath(
            "/api/v1/submissions/(" + UUID_REGEX + ")/claims/(" + UUID_REGEX + ")",
            "/api/v1/submissions/" + SUBMISSION_ID + "/claims/" + CLAIM_ID)
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
        .method("PATCH")
        .body(LambdaDsl.newJsonBody(ClaimsUpdateClaimPactTest::buildUpdateRequestBody).build())
        .willRespondWith()
        .status(204)
        .toPact();
  }

  @Pact(consumer = CONSUMER)
  public RequestResponsePact updateClaim404(PactDslWithProvider builder) {
    return builder
        .given("no claim exists")
        .uponReceiving("a request to update a non-existent claim")
        .matchPath(
            "/api/v1/submissions/(" + UUID_REGEX + ")/claims/(" + UUID_REGEX + ")",
            "/api/v1/submissions/" + SUBMISSION_ID + "/claims/" + CLAIM_ID)
        .matchHeader(HttpHeaders.AUTHORIZATION, UUID_REGEX, EXAMPLE_AUTH_TOKEN)
        .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json.*", "application/json")
        .method("PATCH")
        .body(LambdaDsl.newJsonBody(ClaimsUpdateClaimPactTest::buildUpdateRequestBody).build())
        .willRespondWith()
        .status(404)
        .matchHeader("Content-Type", "application/(problem\\+)?json", "application/problem+json")
        .toPact();
  }

  @Test
  @DisplayName("Verify 204 response - claim updated successfully")
  @PactTestFor(pactMethod = "updateClaim204")
  void verify201Response() {
    var update = buildUpdatePost();

    claimsApiClient.updateClaim(SUBMISSION_ID, CLAIM_ID, update).block();
  }

  @Test
  @DisplayName("Verify 404 response - claim does not exist")
  @PactTestFor(pactMethod = "updateClaim404")
  void verify404Response() {
    var update = buildUpdatePost();

    assertThrows(
        NotFound.class, () -> claimsApiClient.updateClaim(SUBMISSION_ID, CLAIM_ID, update).block());
  }

  private static void buildUpdateRequestBody(au.com.dius.pact.consumer.dsl.LambdaDslJsonBody body) {
    body.nullValue("status");
    body.stringType("schedule_reference", "12345");
    body.stringType("case_reference_number", "123456/24");
    body.stringType("unique_file_number", "1234567AB");
    body.stringType("case_start_date", "2024-01-01");
    body.stringType("case_concluded_date", "2024-06-01");
    body.stringType("matter_type_code", "IMCA:IIOM");
    body.stringType("crime_matter_type_code", "CRIME01");
    body.stringType("fee_code", "IMRN");
    body.stringType("procurement_area_code", "PA001");
    body.stringType("access_point_code", "AP001");
    body.stringType("delivery_location", "London");
    body.stringType("representation_order_date", "2024-01-01");
    body.integerType("suspects_defendants_count", 2);
    body.integerType("police_station_court_attendances_count", 3);
    body.stringType("police_station_court_prison_id", "HMP123");
    body.stringType("dscc_number", "DSCC001");
    body.stringType("maat_id", "MAAT001");
    body.stringType("prison_law_prior_approval_number", "PLPA001");
    body.booleanType("is_duty_solicitor", false);
    body.booleanType("is_youth_court", false);
    body.stringType("scheme_id", "SCHEME01");
    body.integerType("mediation_sessions_count", 4);
    body.integerType("mediation_time_minutes", 60);
    body.stringType("outreach_location", "London");
    body.stringType("referral_source", "CITIZENS_ADVICE");
    body.stringType("client_forename", "John");
    body.stringType("client_surname", "Doe");
    body.stringType("client_date_of_birth", "1990-01-01");
    body.stringType("unique_client_number", "1234567ABC");
    body.stringType("client_postcode", "SW1A 1AA");
    body.stringType("gender_code", "M");
    body.stringType("ethnicity_code", "A");
    body.stringType("disability_code", "NONE");
    body.booleanType("is_legally_aided", true);
    body.stringType("client_type_code", "CLA");
    body.stringType("home_office_client_number", "HO123");
    body.stringType("cla_reference_number", "CLA001");
    body.stringType("cla_exemption_code", "EX001");
    body.stringType("client_2_forename", "Jane");
    body.stringType("client_2_surname", "Smith");
    body.stringType("client_2_date_of_birth", "1992-01-01");
    body.stringType("client_2_ucn", "UCN123");
    body.stringType("client_2_postcode", "SW1A 2AA");
    body.stringType("client_2_gender_code", "F");
    body.stringType("client_2_ethnicity_code", "B");
    body.stringType("client_2_disability_code", "NONE");
    body.booleanType("client_2_is_legally_aided", true);
    body.stringType("case_id", "CASE001");
    body.stringType("unique_case_id", "UCI001");
    body.stringType("case_stage_code", "STAGE01");
    body.stringType("stage_reached_code", "ZA");
    body.stringType("standard_fee_category_code", "STANDARD");
    body.stringType("outcome_code", "IA");
    body.stringType("designated_accredited_representative_code", "DAR01");
    body.booleanType("is_postal_application_accepted", false);
    body.booleanType("is_client_2_postal_application_accepted", false);
    body.stringType("mental_health_tribunal_reference", "MHT001");
    body.booleanType("is_nrm_advice", false);
    body.stringType("follow_on_work", "FOLLOW");
    body.stringType("transfer_date", "2024-03-01");
    body.stringType("exemption_criteria_satisfied", "DOMESTIC_VIOLENCE");
    body.stringType("exceptional_case_funding_reference", "ECF001");
    body.booleanType("is_legacy_case", false);
    body.integerType("advice_time", 30);
    body.integerType("travel_time", 15);
    body.integerType("waiting_time", 10);
    body.decimalType("net_profit_costs_amount", 200.00);
    body.decimalType("net_disbursement_amount", 50.00);
    body.decimalType("net_counsel_costs_amount", 150.00);
    body.decimalType("disbursements_vat_amount", 10.00);
    body.decimalType("travel_waiting_costs_amount", 30.00);
    body.decimalType("net_waiting_costs_amount", 20.00);
    body.booleanType("is_vat_applicable", true);
    body.booleanType("is_tolerance_applicable", false);
    body.stringType("prior_authority_reference", "PA123");
    body.booleanType("is_london_rate", false);
    body.integerType("adjourned_hearing_fee_amount", 1);
    body.booleanType("is_additional_travel_payment", false);
    body.decimalType("costs_damages_recovered_amount", 500.00);
    body.stringType("meetings_attended_code", "IN_PERSON");
    body.decimalType("detention_travel_waiting_costs_amount", 25.00);
    body.decimalType("jr_form_filling_amount", 75.00);
    body.booleanType("is_eligible_client", true);
    body.stringType("court_location_code", "0B1J");
    body.stringType("advice_type_code", "IMMIGRATION");
    body.integerType("medical_reports_count", 2);
    body.booleanType("is_irc_surgery", false);
    body.stringType("surgery_date", "2024-02-01");
    body.integerType("surgery_clients_count", 5);
    body.integerType("surgery_matters_count", 3);
    body.integerType("cmrh_oral_count", 1);
    body.integerType("cmrh_telephone_count", 1);
    body.stringType("ait_hearing_centre_code", "BIRMINGHAM");
    body.booleanType("is_substantive_hearing", false);
    body.integerType("ho_interview", 1);
    body.stringType("local_authority_number", "LA001");
    body.integerType("version", 1);
    body.nullValue("total_warnings");
    body.nullValue("fee_calculation_response");
    body.stringType("amendment_requested_by", "PROVIDER");
    body.uuid("amendment_user_id");
    body.stringType("amendment_reason_code", "CASE_REOPENED_REBILLED");
    body.array("validation_messages");
  }

  private static ClaimAmendmentPatch buildUpdatePost() {
    return ClaimAmendmentPatch.builder()
        .status(null)
        .scheduleReference("12345")
        .caseReferenceNumber("123456/24")
        .uniqueFileNumber("1234567AB")
        .caseStartDate("2024-01-01")
        .caseConcludedDate("2024-06-01")
        .matterTypeCode("IMCA:IIOM")
        .crimeMatterTypeCode("CRIME01")
        .feeCode("IMRN")
        .procurementAreaCode("PA001")
        .accessPointCode("AP001")
        .deliveryLocation("London")
        .representationOrderDate("2024-01-01")
        .suspectsDefendantsCount(2)
        .policeStationCourtAttendancesCount(3)
        .policeStationCourtPrisonId("HMP123")
        .dsccNumber("DSCC001")
        .maatId("MAAT001")
        .prisonLawPriorApprovalNumber("PLPA001")
        .isDutySolicitor(false)
        .isYouthCourt(false)
        .schemeId("SCHEME01")
        .mediationSessionsCount(4)
        .mediationTimeMinutes(60)
        .outreachLocation("London")
        .referralSource("CITIZENS_ADVICE")
        .clientForename("John")
        .clientSurname("Doe")
        .clientDateOfBirth("1990-01-01")
        .uniqueClientNumber("1234567ABC")
        .clientPostcode("SW1A 1AA")
        .genderCode("M")
        .ethnicityCode("A")
        .disabilityCode("NONE")
        .isLegallyAided(true)
        .clientTypeCode("CLA")
        .homeOfficeClientNumber("HO123")
        .claReferenceNumber("CLA001")
        .claExemptionCode("EX001")
        .client2Forename("Jane")
        .client2Surname("Smith")
        .client2DateOfBirth("1992-01-01")
        .client2Ucn("UCN123")
        .client2Postcode("SW1A 2AA")
        .client2GenderCode("F")
        .client2EthnicityCode("B")
        .client2DisabilityCode("NONE")
        .client2IsLegallyAided(true)
        .caseId("CASE001")
        .uniqueCaseId("UCI001")
        .caseStageCode("STAGE01")
        .stageReachedCode("ZA")
        .standardFeeCategoryCode("STANDARD")
        .outcomeCode("IA")
        .designatedAccreditedRepresentativeCode("DAR01")
        .isPostalApplicationAccepted(false)
        .isClient2PostalApplicationAccepted(false)
        .mentalHealthTribunalReference("MHT001")
        .isNrmAdvice(false)
        .followOnWork("FOLLOW")
        .transferDate("2024-03-01")
        .exemptionCriteriaSatisfied("DOMESTIC_VIOLENCE")
        .exceptionalCaseFundingReference("ECF001")
        .isLegacyCase(false)
        .adviceTime(30)
        .travelTime(15)
        .waitingTime(10)
        .netProfitCostsAmount(BigDecimal.valueOf(200.00))
        .netDisbursementAmount(BigDecimal.valueOf(50.00))
        .netCounselCostsAmount(BigDecimal.valueOf(150.00))
        .disbursementsVatAmount(BigDecimal.valueOf(10.00))
        .travelWaitingCostsAmount(BigDecimal.valueOf(30.00))
        .netWaitingCostsAmount(BigDecimal.valueOf(20.00))
        .isVatApplicable(true)
        .isToleranceApplicable(false)
        .priorAuthorityReference("PA123")
        .isLondonRate(false)
        .adjournedHearingFeeAmount(1)
        .isAdditionalTravelPayment(false)
        .costsDamagesRecoveredAmount(BigDecimal.valueOf(500.00))
        .meetingsAttendedCode("IN_PERSON")
        .detentionTravelWaitingCostsAmount(BigDecimal.valueOf(25.00))
        .jrFormFillingAmount(BigDecimal.valueOf(75.00))
        .isEligibleClient(true)
        .courtLocationCode("0B1J")
        .adviceTypeCode("IMMIGRATION")
        .medicalReportsCount(2)
        .isIrcSurgery(false)
        .surgeryDate("2024-02-01")
        .surgeryClientsCount(5)
        .surgeryMattersCount(3)
        .cmrhOralCount(1)
        .cmrhTelephoneCount(1)
        .aitHearingCentreCode("BIRMINGHAM")
        .isSubstantiveHearing(false)
        .hoInterview(1)
        .localAuthorityNumber("LA001")
        .version(1L)
        .totalWarnings(null)
        .feeCalculationResponse(null)
        .amendmentRequestedBy("PROVIDER")
        .amendmentUserId(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
        .amendmentReasonCode("CASE_REOPENED_REBILLED")
        .build();
  }
}
