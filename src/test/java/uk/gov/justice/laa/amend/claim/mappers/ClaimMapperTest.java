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
import uk.gov.justice.laa.amend.claim.models.ClaimFieldType;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AreaOfLaw;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.BoltOnPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringJUnitConfig
@ContextConfiguration(classes = {ClaimMapperImpl.class, ClaimMapperHelper.class})
class ClaimMapperTest {

    @Autowired
    private ClaimMapper mapper;

    private final String submissionId = "foo";
    private final String claimId = "bar";

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
        assertEquals(AmendClaimConstants.Label.TOTAL, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.CALCULATED_TOTAL, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.FIXED_FEE, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertNull(claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.FIXED_FEE, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.NET_PROFIT_COST, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
    }

    @Test
    void mapVatClaimedWhenFeeCalculationResponseIsNull() {
        ClaimResponse response = new ClaimResponse();
        response.setIsVatApplicable(false);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        ClaimDetails claim = mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getVatClaimed();
        assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
        assertEquals(false, claimField.getSubmitted());
        assertEquals(false, claimField.getCalculated());
        assertEquals(false, claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
    }

    @Test
    void mapVatClaimedWhenVatIndicatorIsNull() {
        ClaimResponse response = new ClaimResponse();
        response.setIsVatApplicable(true);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        ClaimDetails claim = mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getVatClaimed();
        assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
        assertEquals(true, claimField.getSubmitted());
        assertEquals(false, claimField.getCalculated());
        assertEquals(true, claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
    }

    @Test
    void mapVatClaimedWhenVatIndicatorIsFalse() {
        ClaimResponse response = new ClaimResponse();
        response.setIsVatApplicable(true);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setVatIndicator(false);
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        ClaimDetails claim = mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getVatClaimed();
        assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
        assertEquals(true, claimField.getSubmitted());
        assertEquals(false, claimField.getCalculated());
        assertEquals(true, claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
    }

    @Test
    void mapVatClaimedWhenVatIndicatorIsTrue() {
        ClaimResponse response = new ClaimResponse();
        response.setIsVatApplicable(true);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setVatIndicator(true);
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        ClaimDetails claim = mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getVatClaimed();
        assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
        assertEquals(true, claimField.getSubmitted());
        assertEquals(true, claimField.getCalculated());
        assertEquals(true, claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.NET_DISBURSEMENTS_COST, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.DISBURSEMENT_VAT, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.COUNSELS_COST, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.DETENTION_TRAVEL_COST, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.JR_FORM_FILLING, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.ADJOURNED_FEE, claimField.getKey());
        assertEquals(100, claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(100, claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.BOLT_ON, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.CMRH_TELEPHONE, claimField.getKey());
        assertEquals(100, claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(100, claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.BOLT_ON, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.CMRH_ORAL, claimField.getKey());
        assertEquals(100, claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(100, claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.BOLT_ON, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.HO_INTERVIEW, claimField.getKey());
        assertEquals(100, claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(100, claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.BOLT_ON, claimField.getType());
    }

    @Test
    void mapSubstantiveHearing() {
        ClaimResponse response = new ClaimResponse();
        response.setHoInterview(100);
        response.setIsSubstantiveHearing(true);
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        BoltOnPatch bolt = new BoltOnPatch();
        bolt.setBoltOnHomeOfficeInterviewFee(BigDecimal.valueOf(120));
        bolt.setBoltOnSubstantiveHearingFee(BigDecimal.valueOf(120));
        feeCalc.setBoltOnDetails(bolt);
        response.setFeeCalculationResponse(feeCalc);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.LEGAL_HELP);

        CivilClaimDetails claim = (CivilClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getSubstantiveHearing();
        assertEquals(AmendClaimConstants.Label.SUBSTANTIVE_HEARING, claimField.getKey());
        assertEquals(true, claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(true, claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.BOLT_ON, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.TRAVEL_COSTS, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
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
        assertEquals(AmendClaimConstants.Label.WAITING_COSTS, claimField.getKey());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
        assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
        assertNull(claimField.getChangeUrl(submissionId, claimId));
        assertEquals(ClaimFieldType.OTHER, claimField.getType());
    }

    @Test
    void mapAssessedTotalVat() {
        ClaimResponse response = new ClaimResponse();

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getAssessedTotalVat();
        assertEquals(AmendClaimConstants.Label.ASSESSED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
        assertNull(claimField.getStatus());
        assertEquals(ClaimFieldType.ASSESSED_TOTAL, claimField.getType());
    }

    @Test
    void mapAssessedTotalInclVat() {
        ClaimResponse response = new ClaimResponse();

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getAssessedTotalInclVat();
        assertEquals(AmendClaimConstants.Label.ASSESSED_TOTAL_INCL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
        assertNull(claimField.getStatus());
        assertEquals(ClaimFieldType.ASSESSED_TOTAL, claimField.getType());
    }

    @Test
    void mapAllowedTotalVat() {
        ClaimResponse response = new ClaimResponse();
        FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
        feeCalculationPatch.setCalculatedVatAmount(BigDecimal.valueOf(100));
        feeCalculationPatch.setDisbursementVatAmount(BigDecimal.valueOf(200));
        response.setFeeCalculationResponse(feeCalculationPatch);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getAllowedTotalVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(300), claimField.getCalculated());
        assertNull(claimField.getAssessed());
        assertNull(claimField.getStatus());
        assertEquals(ClaimFieldType.ALLOWED_TOTAL, claimField.getType());
    }

    @Test
    void mapAllowedTotalVatWhenFeeCalculationIsNull() {
        ClaimResponse response = new ClaimResponse();

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getAllowedTotalVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
        assertNull(claimField.getStatus());
        assertEquals(ClaimFieldType.ALLOWED_TOTAL, claimField.getType());
    }

    @Test
    void mapAllowedTotalVatWhenCalculatedVatAmountAndDisbursementVatAmountAreNull() {
        ClaimResponse response = new ClaimResponse();
        FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
        response.setFeeCalculationResponse(feeCalculationPatch);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getAllowedTotalVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
        assertNull(claimField.getStatus());
        assertEquals(ClaimFieldType.ALLOWED_TOTAL, claimField.getType());
    }

    @Test
    void mapAllowedTotalVatWhenCalculatedVatAmountIsNull() {
        ClaimResponse response = new ClaimResponse();
        FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
        feeCalculationPatch.setCalculatedVatAmount(BigDecimal.valueOf(100));
        response.setFeeCalculationResponse(feeCalculationPatch);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getAllowedTotalVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(100), claimField.getCalculated());
        assertNull(claimField.getAssessed());
        assertNull(claimField.getStatus());
        assertEquals(ClaimFieldType.ALLOWED_TOTAL, claimField.getType());
    }

    @Test
    void mapAllowedTotalVatWhenDisbursementVatAmountIsNull() {
        ClaimResponse response = new ClaimResponse();
        FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
        feeCalculationPatch.setDisbursementVatAmount(BigDecimal.valueOf(100));
        response.setFeeCalculationResponse(feeCalculationPatch);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getAllowedTotalVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(100), claimField.getCalculated());
        assertNull(claimField.getAssessed());
        assertNull(claimField.getStatus());
        assertEquals(ClaimFieldType.ALLOWED_TOTAL, claimField.getType());
    }

    @Test
    void mapAllowedTotalInclVat() {
        ClaimResponse response = new ClaimResponse();
        FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
        feeCalculationPatch.setTotalAmount(BigDecimal.valueOf(100));
        response.setFeeCalculationResponse(feeCalculationPatch);

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getAllowedTotalInclVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(100), claimField.getCalculated());
        assertNull(claimField.getAssessed());
        assertNull(claimField.getStatus());
        assertEquals(ClaimFieldType.ALLOWED_TOTAL, claimField.getType());
    }

    @Test
    void mapAllowedTotalInclVatWhenFeeCalculationIsNull() {
        ClaimResponse response = new ClaimResponse();

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getAllowedTotalInclVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
        assertNull(claimField.getStatus());
        assertEquals(ClaimFieldType.ALLOWED_TOTAL, claimField.getType());
    }

    @Test
    void mapAllowedTotalInclVatWhenTotalAmountIsNull() {
        ClaimResponse response = new ClaimResponse();

        SubmissionResponse submissionResponse = new SubmissionResponse().submissionId(UUID.randomUUID()).areaOfLaw(AreaOfLaw.CRIME_LOWER);

        CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response, submissionResponse);

        ClaimField claimField = claim.getAllowedTotalInclVat();
        assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT, claimField.getKey());
        assertNull(claimField.getSubmitted());
        assertNull(claimField.getCalculated());
        assertNull(claimField.getAssessed());
        assertNull(claimField.getStatus());
        assertEquals(ClaimFieldType.ALLOWED_TOTAL, claimField.getType());
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

        SubmissionResponse submissionResponse = new SubmissionResponse();
        submissionResponse.setSubmissionId(UUID.randomUUID());
        submissionResponse.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
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
        assertEquals("FeeCode", claim.getFeeCode());
        assertEquals("FeeCodeDesc", claim.getFeeCodeDescription());
        assertEquals("Civil", claim.getCategoryOfLaw());
        assertTrue(claim.getEscaped());
        assertEquals("MT1+MT2", claim.getMatterTypeCode());
        assertEquals(claimSummaryFeeId.toString(), claim.getClaimSummaryFeeId());
        assertEquals("LEGAL HELP", claim.getAreaOfLaw());
        assertEquals("User ID", claim.getProviderName());
        assertEquals(LocalDateTime.of(2025, 1, 10, 14, 30, 0), claim.getSubmittedDate());
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

        response.setCrimeMatterTypeCode("CRIME123");
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
        assertEquals("CRIME123", claim.getMatterTypeCode());
        assertEquals("CRIME123", claim.getMatterTypeCode());
        assertEquals("PrisonCode", claim.getPoliceStationCourtPrisonId());
        assertEquals("SchemeId", claim.getSchemeId());

        assertEquals("User ID", claim.getProviderName());
        assertEquals(LocalDateTime.of(2025, 1, 10, 14, 30, 0), claim.getSubmittedDate());
    }
}
