package uk.gov.justice.laa.amend.claim.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants;
import uk.gov.justice.laa.amend.claim.models.AssessedClaimField;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AreaOfLaw;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.BoltOnPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;

@SpringJUnitConfig
@ContextConfiguration(classes = {ClaimMapperImpl.class, ClaimMapperHelper.class})
class ClaimMapperTest {

    @Autowired
    private ClaimMapper mapper;

    @Test
    void testMapTotalAmount() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setTotalAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        var claim = mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getTotalAmount();
        assertEquals(AmendClaimConstants.Label.TOTAL, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void testMapFixedFee() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setFixedFeeAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        var claim = mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getFixedFee();
        assertEquals(AmendClaimConstants.Label.FIXED_FEE, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void mapNetProfitCost() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        response.setNetProfitCostsAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setNetProfitCostsAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        var claim = mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getNetProfitCost();
        assertEquals(AmendClaimConstants.Label.NET_PROFIT_COST, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
    }

    @Test
    void mapVatClaimedWhenFeeCalculationResponseIsNull() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        response.setIsVatApplicable(false);

        var claim = mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getVatClaimed();
        assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
        assertEquals(false, claimField.getSubmitted());
        assertEquals(false, claimField.getCalculated());
        assertEquals(false, claimField.getAssessed());
    }

    @Test
    void mapVatClaimedWhenVatIndicatorIsNull() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        response.setIsVatApplicable(true);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        response.setFeeCalculationResponse(feeCalc);

        var claim = mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getVatClaimed();
        assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
        assertEquals(true, claimField.getSubmitted());
        assertEquals(false, claimField.getCalculated());
        assertEquals(true, claimField.getAssessed());
    }

    @Test
    void mapVatClaimedWhenVatIndicatorIsFalse() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        response.setIsVatApplicable(true);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setVatIndicator(false);
        response.setFeeCalculationResponse(feeCalc);

        var claim = mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getVatClaimed();
        assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
        assertEquals(true, claimField.getSubmitted());
        assertEquals(false, claimField.getCalculated());
        assertEquals(true, claimField.getAssessed());
    }

    @Test
    void mapVatClaimedWhenVatIndicatorIsTrue() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        response.setIsVatApplicable(true);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setVatIndicator(true);
        response.setFeeCalculationResponse(feeCalc);

        var claim = mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getVatClaimed();
        assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
        assertEquals(true, claimField.getSubmitted());
        assertEquals(true, claimField.getCalculated());
        assertEquals(true, claimField.getAssessed());
    }

    @Test
    void mapNetDisbursementAmount() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        response.setNetDisbursementAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setDisbursementAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        var claim = mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getNetDisbursementAmount();
        assertEquals(AmendClaimConstants.Label.NET_DISBURSEMENTS_COST, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
    }

    @Test
    void mapDisbursementVatAmount() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        response.setDisbursementsVatAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setDisbursementVatAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        var claim = mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getDisbursementVatAmount();
        assertEquals(AmendClaimConstants.Label.DISBURSEMENT_VAT, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
    }

    @Test
    void mapCounselsCost() {
        var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
        response.setNetCounselCostsAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setNetCostOfCounselAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getCounselsCost();
        assertEquals(AmendClaimConstants.Label.COUNSELS_COST, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
    }

    @Test
    void mapDetentionTravelWaitingCosts() {
        var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
        response.setDetentionTravelWaitingCostsAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setDetentionTravelAndWaitingCostsAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getDetentionTravelWaitingCosts();
        assertEquals(AmendClaimConstants.Label.DETENTION_TRAVEL_COST, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
    }

    @Test
    void mapJrFormFillingCost() {
        var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
        response.setJrFormFillingAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setJrFormFillingAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getJrFormFillingCost();
        assertEquals(AmendClaimConstants.Label.JR_FORM_FILLING, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
    }

    @Test
    void mapAdjournedHearingFee() {
        var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
        response.setAdjournedHearingFeeAmount(100);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        BoltOnPatch bolt = new BoltOnPatch();
        bolt.setBoltOnAdjournedHearingFee(BigDecimal.valueOf(120));
        feeCalc.setBoltOnDetails(bolt);
        response.setFeeCalculationResponse(feeCalc);

        var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getAdjournedHearing();
        assertEquals(AmendClaimConstants.Label.ADJOURNED_FEE, claimField.getKey());
        assertEquals(100, claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(100, claimField.getAssessed());
    }

    @Test
    void mapCmrhTelephone() {
        var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
        response.setCmrhTelephoneCount(100);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        BoltOnPatch bolt = new BoltOnPatch();
        bolt.setBoltOnCmrhTelephoneFee(BigDecimal.valueOf(120));
        feeCalc.setBoltOnDetails(bolt);
        response.setFeeCalculationResponse(feeCalc);

        var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getCmrhTelephone();
        assertEquals(AmendClaimConstants.Label.CMRH_TELEPHONE, claimField.getKey());
        assertEquals(100, claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(100, claimField.getAssessed());
    }

    @Test
    void mapCmrhOral() {
        var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
        response.setCmrhOralCount(100);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        BoltOnPatch bolt = new BoltOnPatch();
        bolt.setBoltOnCmrhOralFee(BigDecimal.valueOf(120));
        feeCalc.setBoltOnDetails(bolt);
        response.setFeeCalculationResponse(feeCalc);

        var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getCmrhOral();
        assertEquals(AmendClaimConstants.Label.CMRH_ORAL, claimField.getKey());
        assertEquals(100, claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(100, claimField.getAssessed());
    }

    @Test
    void mapHoInterview() {
        var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
        response.setHoInterview(100);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        BoltOnPatch bolt = new BoltOnPatch();
        bolt.setBoltOnHomeOfficeInterviewFee(BigDecimal.valueOf(120));
        feeCalc.setBoltOnDetails(bolt);
        response.setFeeCalculationResponse(feeCalc);

        var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getHoInterview();
        assertEquals(AmendClaimConstants.Label.HO_INTERVIEW, claimField.getKey());
        assertEquals(100, claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(100, claimField.getAssessed());
    }

    @Test
    void mapSubstantiveHearing() {
        var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
        response.setIsSubstantiveHearing(true);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        BoltOnPatch bolt = new BoltOnPatch();
        bolt.setBoltOnSubstantiveHearingFee(BigDecimal.valueOf(120));
        feeCalc.setBoltOnDetails(bolt);
        response.setFeeCalculationResponse(feeCalc);

        var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getSubstantiveHearing();
        assertEquals(AmendClaimConstants.Label.SUBSTANTIVE_HEARING, claimField.getKey());
        assertEquals(true, claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(true, claimField.getAssessed());
    }

    @Test
    void mapTravelCosts() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        response.setTravelWaitingCostsAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setNetTravelCostsAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getTravelCosts();
        assertEquals(AmendClaimConstants.Label.TRAVEL_COSTS, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
    }

    @Test
    void mapWaitingCosts() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        response.setNetWaitingCostsAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setNetWaitingCostsAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getWaitingCosts();
        assertEquals(AmendClaimConstants.Label.WAITING_COSTS, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
    }

    @Test
    void mapAssessedTotalVat() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);

        var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        AssessedClaimField claimField = (AssessedClaimField) claim.getAssessedTotalVat();
        assertEquals(AmendClaimConstants.Label.ASSESSED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void mapAssessedTotalInclVat() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);

        var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        AssessedClaimField claimField = (AssessedClaimField) claim.getAssessedTotalInclVat();
        assertEquals(AmendClaimConstants.Label.ASSESSED_TOTAL_INCL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void mapAllowedTotalVat() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
        feeCalculationPatch.setCalculatedVatAmount(BigDecimal.valueOf(100));
        feeCalculationPatch.setDisbursementVatAmount(BigDecimal.valueOf(200));
        response.setFeeCalculationResponse(feeCalculationPatch);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getAllowedTotalVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(300), claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void mapAllowedTotalVatWhenFeeCalculationIsNull() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);

        var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getAllowedTotalVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void mapAllowedTotalVatWhenCalculatedVatAmountAndDisbursementVatAmountAreNull() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
        response.setFeeCalculationResponse(feeCalculationPatch);

        var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getAllowedTotalVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void mapAllowedTotalVatWhenCalculatedVatAmountIsNull() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
        feeCalculationPatch.setCalculatedVatAmount(BigDecimal.valueOf(100));
        response.setFeeCalculationResponse(feeCalculationPatch);

        var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getAllowedTotalVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(100), claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void mapAllowedTotalVatWhenDisbursementVatAmountIsNull() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
        feeCalculationPatch.setDisbursementVatAmount(BigDecimal.valueOf(100));
        response.setFeeCalculationResponse(feeCalculationPatch);

        var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getAllowedTotalVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(100), claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void mapAllowedTotalInclVat() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
        feeCalculationPatch.setTotalAmount(BigDecimal.valueOf(100));
        response.setFeeCalculationResponse(feeCalculationPatch);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getAllowedTotalInclVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(100), claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void mapAllowedTotalInclVatWhenFeeCalculationIsNull() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);

        var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getAllowedTotalInclVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void mapAllowedTotalInclVatWhenTotalAmountIsNull() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);

        var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, new SubmissionResponse());

        ClaimField claimField = claim.getAllowedTotalInclVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
    }

    @Test
    void testMapToCivilClaim() {
        var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
        response.setUniqueFileNumber("UFN123");
        response.setCaseReferenceNumber("CASE456");
        response.setClientSurname("Doe");
        response.setClientForename("John");
        response.setUniqueClientNumber("21121985/J/DOE");
        response.setCaseStartDate("2025-01-01");
        response.setCaseConcludedDate("2025-02-01");
        response.setStatus(ClaimStatus.VALID);

        SubmissionResponse submissionResponse = new SubmissionResponse();
        submissionResponse.setSubmissionId(UUID.randomUUID());
        submissionResponse.setProviderUserId("User ID");
        submissionResponse.setSubmitted(OffsetDateTime.parse("2025-01-10T14:30:00+02:00"));

        UUID claimSummaryFeeId = UUID.randomUUID();

        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setClaimSummaryFeeId(claimSummaryFeeId);
        feeCalc.setFeeCode("FeeCode");
        feeCalc.setFeeCodeDescription("FeeCodeDesc");
        feeCalc.setCategoryOfLaw("Civil");
        BoltOnPatch boltOn = new BoltOnPatch();
        boltOn.setEscapeCaseFlag(true);
        feeCalc.setBoltOnDetails(boltOn);
        response.setFeeCalculationResponse(feeCalc);

        response.setMatterTypeCode("MT1+MT2");

        CivilClaimDetails claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        assertEquals("UFN123", claim.getUniqueFileNumber());
        assertEquals("CASE456", claim.getCaseReferenceNumber());
        assertEquals("Doe", claim.getClientSurname());
        assertEquals("John", claim.getClientForename());
        assertEquals("21121985/J/DOE", claim.getUniqueClientNumber());
        assertEquals("FeeCode", claim.getFeeCode());
        assertEquals("FeeCodeDesc", claim.getFeeCodeDescription());
        assertEquals("Civil", claim.getCategoryOfLaw());
        assertTrue(claim.getEscaped());
        assertEquals("MT1+MT2", claim.getMatterTypeCode());
        assertEquals(claimSummaryFeeId.toString(), claim.getClaimSummaryFeeId());
        assertEquals(uk.gov.justice.laa.amend.claim.models.AreaOfLaw.LEGAL_HELP, claim.getAreaOfLaw());
        assertNull(claim.getProviderName());
        assertEquals(LocalDateTime.of(2025, 1, 10, 14, 30, 0), claim.getSubmittedDate());
        assertEquals(ClaimStatus.VALID, claim.getStatus());
    }

    @Test
    void testMapToCrimeClaim() {
        var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
        response.setUniqueFileNumber("UFN123");
        response.setCaseReferenceNumber("CASE456");
        response.setClientSurname("Doe");
        response.setClientForename("John");
        response.setCaseStartDate("2025-01-01");
        response.setCaseConcludedDate("2025-02-01");
        response.setStatus(ClaimStatus.VALID);

        SubmissionResponse submissionResponse = new SubmissionResponse();
        submissionResponse.setSubmissionId(UUID.randomUUID());
        submissionResponse.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
        submissionResponse.setProviderUserId("User ID");
        submissionResponse.setSubmitted(OffsetDateTime.parse("2025-01-10T14:30:00+02:00"));

        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setFeeCode("FeeCode");
        feeCalc.setFeeCodeDescription("FeeCodeDesc");
        feeCalc.setCategoryOfLaw("Civil");
        BoltOnPatch boltOn = new BoltOnPatch();
        boltOn.setEscapeCaseFlag(true);
        feeCalc.setBoltOnDetails(boltOn);
        response.setFeeCalculationResponse(feeCalc);

        response.setMatterTypeCode("INVC");
        response.setPoliceStationCourtPrisonId("PrisonCode");
        response.setSchemeId("SchemeId");
        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        assertEquals("UFN123", claim.getUniqueFileNumber());
        assertEquals("CASE456", claim.getCaseReferenceNumber());
        assertEquals("Doe", claim.getClientSurname());
        assertEquals("John", claim.getClientForename());
        assertEquals("FeeCode", claim.getFeeCode());
        assertEquals("FeeCodeDesc", claim.getFeeCodeDescription());
        assertEquals("Civil", claim.getCategoryOfLaw());
        assertTrue(claim.getEscaped());
        assertEquals("INVC", claim.getMatterTypeCode());
        assertEquals("PrisonCode", claim.getPoliceStationCourtPrisonId());
        assertEquals("SchemeId", claim.getSchemeId());
        assertEquals(ClaimStatus.VALID, claim.getStatus());

        assertEquals(LocalDateTime.of(2025, 1, 10, 14, 30, 0), claim.getSubmittedDate());
    }

    private static ClaimResponseV2 createClaimResponse(AreaOfLaw areaOfLaw) {
        var response = new ClaimResponseV2();
        response.setAreaOfLaw(areaOfLaw);
        return response;
    }
}
