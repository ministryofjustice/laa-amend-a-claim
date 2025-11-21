package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AreaOfLaw;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.BoltOnPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringJUnitConfig
@ContextConfiguration(classes = {ClaimMapperImpl.class, ClaimMapperHelper.class})
class ClaimMapperTest {

    @Autowired
    private ClaimMapper mapper;

    @Test
    void testMapTotalAmount() {
        ClaimResponse response = new ClaimResponse();
        response.setTotalValue(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setTotalAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        ClaimDetails claim = mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getTotalAmount();
        assertEquals(AmendClaimConstants.Label.TOTAL, claimField.getLabel());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(BigDecimal.valueOf(100), claimField.getAmended().getValue());
        assertNull(claimField.getCost());
    }

    @Test
    void testMapFixedFee() {
        ClaimResponse response = new ClaimResponse();
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setFixedFeeAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        ClaimDetails claim = mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getFixedFee();
        assertEquals(AmendClaimConstants.Label.FIXED_FEE, claimField.getLabel());
        assertNull(claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertNull(claimField.getAmended().getValue());
        assertNull(claimField.getCost());
    }

    @Test
    void mapNetProfitCost() {
        ClaimResponse response = new ClaimResponse();
        response.setNetProfitCostsAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setNetProfitCostsAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        ClaimDetails claim = mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getNetProfitCost();
        assertEquals(AmendClaimConstants.Label.NET_PROFIT_COST, claimField.getLabel());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(BigDecimal.valueOf(100), claimField.getAmended().getValue());
        assertEquals(Cost.PROFIT_COSTS, claimField.getCost());
    }

    @Test
    void mapVatClaimed() {
        ClaimResponse response = new ClaimResponse();
        response.setIsVatApplicable(true);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setVatIndicator(false);
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        ClaimDetails claim = mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getVatClaimed();
        assertEquals(AmendClaimConstants.Label.VAT, claimField.getLabel());
        assertEquals(true, claimField.getSubmitted().getValue());
        assertEquals(false, claimField.getCalculated().getValue());
        assertEquals(true, claimField.getAmended().getValue());
        assertNull(claimField.getCost());
    }

    @Test
    void mapNetDisbursementAmount() {
        ClaimResponse response = new ClaimResponse();
        response.setNetDisbursementAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setDisbursementAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        ClaimDetails claim = mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getNetDisbursementAmount();
        assertEquals(AmendClaimConstants.Label.NET_DISBURSEMENTS_COST, claimField.getLabel());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(BigDecimal.valueOf(100), claimField.getAmended().getValue());
        assertEquals(Cost.DISBURSEMENTS, claimField.getCost());
    }

    @Test
    void mapDisbursementVatAmount() {
        ClaimResponse response = new ClaimResponse();
        response.setDisbursementsVatAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setDisbursementVatAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        ClaimDetails claim = mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getDisbursementVatAmount();
        assertEquals(AmendClaimConstants.Label.DISBURSEMENT_VAT, claimField.getLabel());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(BigDecimal.valueOf(100), claimField.getAmended().getValue());
        assertEquals(Cost.DISBURSEMENTS_VAT, claimField.getCost());
    }

    @Test
    void mapCounselsCost() {
        ClaimResponse response = new ClaimResponse();
        response.setNetCounselCostsAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setNetCostOfCounselAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        CivilClaimDetails claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getCounselsCost();
        assertEquals(AmendClaimConstants.Label.COUNSELS_COST, claimField.getLabel());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(BigDecimal.valueOf(100), claimField.getAmended().getValue());
        assertEquals(Cost.COUNSEL_COSTS, claimField.getCost());
    }

    @Test
    void mapDetentionTravelWaitingCosts() {
        ClaimResponse response = new ClaimResponse();
        response.setDetentionTravelWaitingCostsAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setDetentionTravelAndWaitingCostsAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        CivilClaimDetails claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getDetentionTravelWaitingCosts();
        assertEquals(AmendClaimConstants.Label.DETENTION_TRAVEL_COST, claimField.getLabel());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(BigDecimal.valueOf(100), claimField.getAmended().getValue());
        assertEquals(Cost.DETENTION_TRAVEL_AND_WAITING_COSTS, claimField.getCost());
    }

    @Test
    void mapJrFormFillingCost() {
        ClaimResponse response = new ClaimResponse();
        response.setJrFormFillingAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setJrFormFillingAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        CivilClaimDetails claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getJrFormFillingCost();
        assertEquals(AmendClaimConstants.Label.JR_FORM_FILLING, claimField.getLabel());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(BigDecimal.valueOf(100), claimField.getAmended().getValue());
        assertEquals(Cost.JR_FORM_FILLING_COSTS, claimField.getCost());
    }

    @Test
    void mapAdjournedHearingFee() {
        ClaimResponse response = new ClaimResponse();
        response.setAdjournedHearingFeeAmount(100);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        BoltOnPatch bolt = new BoltOnPatch();
        bolt.setBoltOnAdjournedHearingFee(BigDecimal.valueOf(120));
        feeCalc.setBoltOnDetails(bolt);
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        CivilClaimDetails claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getAdjournedHearing();
        assertEquals(AmendClaimConstants.Label.ADJOURNED_FEE, claimField.getLabel());
        assertEquals(100, claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(100, claimField.getAmended().getValue());
        assertNull(claimField.getCost());
    }

    @Test
    void mapCmrhTelephone() {
        ClaimResponse response = new ClaimResponse();
        response.setCmrhTelephoneCount(100);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        BoltOnPatch bolt = new BoltOnPatch();
        bolt.setBoltOnCmrhTelephoneFee(BigDecimal.valueOf(120));
        feeCalc.setBoltOnDetails(bolt);
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        CivilClaimDetails claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getCmrhTelephone();
        assertEquals(AmendClaimConstants.Label.CMRH_TELEPHONE, claimField.getLabel());
        assertEquals(100, claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(100, claimField.getAmended().getValue());
        assertNull(claimField.getCost());
    }

    @Test
    void mapCmrhOral() {
        ClaimResponse response = new ClaimResponse();
        response.setCmrhOralCount(100);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        BoltOnPatch bolt = new BoltOnPatch();
        bolt.setBoltOnCmrhOralFee(BigDecimal.valueOf(120));
        feeCalc.setBoltOnDetails(bolt);
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        CivilClaimDetails claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getCmrhOral();
        assertEquals(AmendClaimConstants.Label.CMRH_ORAL, claimField.getLabel());
        assertEquals(100, claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(100, claimField.getAmended().getValue());
        assertNull(claimField.getCost());
    }

    @Test
    void mapHoInterview() {
        ClaimResponse response = new ClaimResponse();
        response.setHoInterview(100);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        BoltOnPatch bolt = new BoltOnPatch();
        bolt.setBoltOnHomeOfficeInterviewFee(BigDecimal.valueOf(120));
        feeCalc.setBoltOnDetails(bolt);
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        CivilClaimDetails claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getHoInterview();
        assertEquals(AmendClaimConstants.Label.HO_INTERVIEW, claimField.getLabel());
        assertEquals(100, claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(100, claimField.getAmended().getValue());
        assertNull(claimField.getCost());
    }

    @Test
    void mapSubstantiveHearing() {
        ClaimResponse response = new ClaimResponse();
        response.setHoInterview(100);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        BoltOnPatch bolt = new BoltOnPatch();
        bolt.setBoltOnHomeOfficeInterviewFee(BigDecimal.valueOf(120));
        feeCalc.setBoltOnDetails(bolt);
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        CivilClaimDetails claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getSubstantiveHearing();
        assertEquals(AmendClaimConstants.Label.SUBSTANTIVE_HEARING, claimField.getLabel());
        assertEquals(100, claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(100, claimField.getAmended().getValue());
        assertNull(claimField.getCost());
    }

    @Test
    void mapTravelCosts() {
        ClaimResponse response = new ClaimResponse();
        response.setTravelWaitingCostsAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setNetTravelCostsAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getTravelCosts();
        assertEquals(AmendClaimConstants.Label.TRAVEL_COSTS, claimField.getLabel());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(BigDecimal.valueOf(100), claimField.getAmended().getValue());
        assertEquals(Cost.TRAVEL_COSTS, claimField.getCost());
    }

    @Test
    void mapWaitingCosts() {
        ClaimResponse response = new ClaimResponse();
        response.setNetWaitingCostsAmount(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setNetWaitingCostsAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getWaitingCosts();
        assertEquals(AmendClaimConstants.Label.WAITING_COSTS, claimField.getLabel());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted().getValue());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated().getValue());
        assertEquals(BigDecimal.valueOf(100), claimField.getAmended().getValue());
        assertEquals(Cost.WAITING_COSTS, claimField.getCost());
    }

    @ParameterizedTest(name = "Map to Civil Claim when Area of Law: {0}")
    @EnumSource(value = AreaOfLaw.class, names = {"CRIME_LOWER", "LEGAL_HELP"})
    void testMapToCivilClaim() {
        ClaimResponse response = new ClaimResponse();
        response.setUniqueFileNumber("UFN123");
        response.setCaseReferenceNumber("CASE456");
        response.setClientSurname("Doe");
        response.setClientForename("John");
        response.setCaseStartDate("2025-01-01");
        response.setCaseConcludedDate("2025-02-01");
        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setFeeCodeDescription("FeeSchemeX");
        feeCalc.setCategoryOfLaw("Civil");
        BoltOnPatch boltOn = new BoltOnPatch();
        boltOn.setEscapeCaseFlag(true);
        feeCalc.setBoltOnDetails(boltOn);
        response.setFeeCalculationResponse(feeCalc);

        response.setMatterTypeCode("MT1+MT2");

        CivilClaimDetails claim = mapper.mapToCivilClaimDetails(response, submissionResponse);

        assertEquals("UFN123", claim.getUniqueFileNumber());
        assertEquals("CASE456", claim.getCaseReferenceNumber());
        assertEquals("Doe", claim.getClientSurname());
        assertEquals("John", claim.getClientForename());
        assertEquals("FeeSchemeX", claim.getFeeScheme());
        assertEquals("Civil", claim.getCategoryOfLaw());
        assertTrue(claim.getEscaped());
        assertEquals("MT1+MT2", claim.getMatterTypeCode());
    }

    @Test
    void testMapToCrimeClaim() {
        ClaimResponse response = new ClaimResponse();
        response.setUniqueFileNumber("UFN123");
        response.setCaseReferenceNumber("CASE456");
        response.setClientSurname("Doe");
        response.setClientForename("John");
        response.setCaseStartDate("2025-01-01");
        response.setCaseConcludedDate("2025-02-01");
        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setFeeCodeDescription("FeeSchemeX");
        feeCalc.setCategoryOfLaw("Civil");
        BoltOnPatch boltOn = new BoltOnPatch();
        boltOn.setEscapeCaseFlag(true);
        feeCalc.setBoltOnDetails(boltOn);
        response.setFeeCalculationResponse(feeCalc);

        response.setCrimeMatterTypeCode("CRIME123");
        CrimeClaimDetails claim = mapper.mapToCrimeClaimDetails(response, submissionResponse);

        assertEquals("UFN123", claim.getUniqueFileNumber());
        assertEquals("CASE456", claim.getCaseReferenceNumber());
        assertEquals("Doe", claim.getClientSurname());
        assertEquals("John", claim.getClientForename());
        assertEquals("FeeSchemeX", claim.getFeeScheme());
        assertEquals("Civil", claim.getCategoryOfLaw());
        assertTrue(claim.getEscaped());
        assertEquals("CRIME123", claim.getMatterTypeCode());
    }
}
