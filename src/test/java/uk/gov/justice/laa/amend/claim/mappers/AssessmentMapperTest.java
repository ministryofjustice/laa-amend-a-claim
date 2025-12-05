package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentGet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentOutcome;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ALLOWED_TOTAL_VAT;

@SpringJUnitConfig
@ContextConfiguration(classes = AssessmentMapperImpl.class)
class AssessmentMapperTest {

    @Autowired
    private AssessmentMapper mapper;

    @Test
    void testMapCivilClaimToAssessment() {
        UUID claimId = UUID.randomUUID();
        UUID claimSummaryFeeId = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();

        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setClaimId(claimId.toString());
        claim.setClaimSummaryFeeId(claimSummaryFeeId.toString());
        claim.setAssessmentOutcome(OutcomeType.NILLED);
        claim.setFixedFee(createClaimField(BigDecimal.valueOf(1)));
        claim.setNetProfitCost(createClaimField(BigDecimal.valueOf(2)));
        claim.setNetDisbursementAmount(createClaimField(BigDecimal.valueOf(3)));
        claim.setDisbursementVatAmount(createClaimField(BigDecimal.valueOf(4)));
        claim.setCounselsCost(createClaimField(BigDecimal.valueOf(5)));
        claim.setAdjournedHearing(createClaimField(BigDecimal.valueOf(6)));
        claim.setJrFormFillingCost(createClaimField(BigDecimal.valueOf(7)));
        claim.setCmrhOral(createClaimField(BigDecimal.valueOf(8)));
        claim.setCmrhTelephone(createClaimField(BigDecimal.valueOf(9)));
        claim.setHoInterview(createClaimField(BigDecimal.valueOf(10)));
        claim.setDetentionTravelWaitingCosts(createClaimField(BigDecimal.valueOf(11)));
        claim.setSubstantiveHearing(createClaimField(BigDecimal.valueOf(12)));
        claim.setVatApplicable(true);
        claim.setAssessedTotalVat(createClaimField(BigDecimal.valueOf(13)));
        claim.setAssessedTotalInclVat(createClaimField(BigDecimal.valueOf(14)));
        claim.setAllowedTotalVat(createClaimField(BigDecimal.valueOf(15)));
        claim.setAllowedTotalInclVat(createClaimField(BigDecimal.valueOf(16)));

        AssessmentPost assessment = mapper.mapCivilClaimToAssessment(claim, userId);

        assertEquals(claimId, assessment.getClaimId());
        assertEquals(claimSummaryFeeId, assessment.getClaimSummaryFeeId());
        assertEquals(AssessmentOutcome.NILLED, assessment.getAssessmentOutcome());
        assertEquals(BigDecimal.valueOf(1), assessment.getFixedFeeAmount());
        assertEquals(BigDecimal.valueOf(2), assessment.getNetProfitCostsAmount());
        assertEquals(BigDecimal.valueOf(3), assessment.getDisbursementAmount());
        assertEquals(BigDecimal.valueOf(4), assessment.getDisbursementVatAmount());
        assertEquals(BigDecimal.valueOf(5), assessment.getNetCostOfCounselAmount());
        assertEquals(BigDecimal.valueOf(6), assessment.getBoltOnAdjournedHearingFee());
        assertEquals(BigDecimal.valueOf(7), assessment.getJrFormFillingAmount());
        assertEquals(BigDecimal.valueOf(8), assessment.getBoltOnCmrhOralFee());
        assertEquals(BigDecimal.valueOf(9), assessment.getBoltOnCmrhTelephoneFee());
        assertEquals(BigDecimal.valueOf(10), assessment.getBoltOnHomeOfficeInterviewFee());
        assertEquals(BigDecimal.valueOf(11), assessment.getDetentionTravelAndWaitingCostsAmount());
        assertEquals(BigDecimal.valueOf(12), assessment.getBoltOnSubstantiveHearingFee());
        assertEquals(BigDecimal.valueOf(13), assessment.getAssessedTotalVat());
        assertEquals(BigDecimal.valueOf(14), assessment.getAssessedTotalInclVat());
        assertEquals(BigDecimal.valueOf(15), assessment.getAllowedTotalVat());
        assertEquals(BigDecimal.valueOf(16), assessment.getAllowedTotalInclVat());
        assertEquals(true, assessment.getIsVatApplicable());
        assertEquals(userId, assessment.getCreatedByUserId());
    }

    @Test
    void testMapCrimeClaimToAssessment() {
        UUID claimId = UUID.randomUUID();
        UUID claimSummaryFeeId = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();

        CrimeClaimDetails claim = new CrimeClaimDetails();
        claim.setClaimId(claimId.toString());
        claim.setClaimSummaryFeeId(claimSummaryFeeId.toString());
        claim.setAssessmentOutcome(OutcomeType.NILLED);
        claim.setFixedFee(createClaimField(BigDecimal.valueOf(1)));
        claim.setNetProfitCost(createClaimField(BigDecimal.valueOf(2)));
        claim.setNetDisbursementAmount(createClaimField(BigDecimal.valueOf(3)));
        claim.setDisbursementVatAmount(createClaimField(BigDecimal.valueOf(4)));
        claim.setTravelCosts(createClaimField(BigDecimal.valueOf(5)));
        claim.setWaitingCosts(createClaimField(BigDecimal.valueOf(6)));
        claim.setVatApplicable(true);
        claim.setAssessedTotalVat(createClaimField(BigDecimal.valueOf(7)));
        claim.setAssessedTotalInclVat(createClaimField(BigDecimal.valueOf(8)));
        claim.setAllowedTotalVat(createClaimField(BigDecimal.valueOf(9)));
        claim.setAllowedTotalInclVat(createClaimField(BigDecimal.valueOf(10)));

        AssessmentPost assessment = mapper.mapCrimeClaimToAssessment(claim, userId);

        assertEquals(claimId, assessment.getClaimId());
        assertEquals(claimSummaryFeeId, assessment.getClaimSummaryFeeId());
        assertEquals(AssessmentOutcome.NILLED, assessment.getAssessmentOutcome());
        assertEquals(BigDecimal.valueOf(1), assessment.getFixedFeeAmount());
        assertEquals(BigDecimal.valueOf(2), assessment.getNetProfitCostsAmount());
        assertEquals(BigDecimal.valueOf(3), assessment.getDisbursementAmount());
        assertEquals(BigDecimal.valueOf(4), assessment.getDisbursementVatAmount());
        assertEquals(BigDecimal.valueOf(5), assessment.getNetTravelCostsAmount());
        assertEquals(BigDecimal.valueOf(6), assessment.getNetWaitingCostsAmount());
        assertEquals(BigDecimal.valueOf(7), assessment.getAssessedTotalVat());
        assertEquals(BigDecimal.valueOf(8), assessment.getAssessedTotalInclVat());
        assertEquals(BigDecimal.valueOf(9), assessment.getAllowedTotalVat());
        assertEquals(BigDecimal.valueOf(10), assessment.getAllowedTotalInclVat());
        assertEquals(true, assessment.getIsVatApplicable());
        assertEquals(userId, assessment.getCreatedByUserId());
    }

    @Test
    void testMapAssessedTotalVat_whenAmendedValueIsNull() {
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setAssessedTotalVat(ClaimField.builder().build());
        assertEquals(BigDecimal.ZERO, mapper.mapAssessedTotalVat(claim));
    }

    @Test
    void testMapAssessedTotalVat_whenAmendedValueIsNotNull() {
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setAssessedTotalVat(ClaimField.builder().amended(BigDecimal.ONE).build());
        assertEquals(BigDecimal.ONE, mapper.mapAssessedTotalVat(claim));
    }

    @Test
    void testMapAssessedTotalInclVat_whenAmendedValueIsNull() {
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setAssessedTotalInclVat(ClaimField.builder().build());
        assertEquals(BigDecimal.ZERO, mapper.mapAssessedTotalInclVat(claim));
    }

    @Test
    void testMapAssessedTotalInclVat_whenAmendedValueIsNotNull() {
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setAssessedTotalInclVat(ClaimField.builder().amended(BigDecimal.ONE).build());
        assertEquals(BigDecimal.ONE, mapper.mapAssessedTotalInclVat(claim));
    }

    @Test
    void shouldMapAssessmentToCivilClaimDetails() {
        // Arrange
        AssessmentGet assessmentGet = new AssessmentGet();
        assessmentGet.setIsVatApplicable(true);
        assessmentGet.setFixedFeeAmount(BigDecimal.valueOf(100));
        assessmentGet.setDisbursementAmount(BigDecimal.valueOf(50));
        assessmentGet.setDisbursementVatAmount(BigDecimal.valueOf(10));
        assessmentGet.setNetProfitCostsAmount(BigDecimal.valueOf(200));
        assessmentGet.setAssessedTotalVat(BigDecimal.valueOf(6));
        assessmentGet.setAssessedTotalInclVat(BigDecimal.valueOf(7));
        assessmentGet.setAllowedTotalInclVat(BigDecimal.valueOf(300));
        assessmentGet.setAllowedTotalVat(BigDecimal.valueOf(20));
        assessmentGet.setCreatedByUserId("user123");
        assessmentGet.setCreatedOn(OffsetDateTime.now());
        assessmentGet.setJrFormFillingAmount(BigDecimal.valueOf(100));
        assessmentGet.setAssessmentOutcome(AssessmentOutcome.REDUCED_TO_FIXED_FEE);

        CivilClaimDetails claimDetails = new CivilClaimDetails();
        claimDetails.setAreaOfLaw("LEGAL_HELP");

        // Act
        CivilClaimDetails result = mapper.mapToCivilClaim(assessmentGet, claimDetails);
        // Assert
        assertNotNull(result.getVatClaimed().getAssessed());
        assertEquals(new BigDecimal("100"), result.getFixedFee().getAssessed());
        assertEquals(new BigDecimal("50"), result.getNetDisbursementAmount().getAssessed());
        assertEquals(new BigDecimal("10"), result.getDisbursementVatAmount().getAssessed());
        assertEquals(new BigDecimal("200"),result.getNetProfitCost().getAssessed());
        assertEquals(new BigDecimal("6"),result.getAssessedTotalVat().getAssessed());
        assertEquals(new BigDecimal("7"),result.getAssessedTotalInclVat().getAssessed());
        assertEquals(new BigDecimal("20"),result.getAllowedTotalVat().getAssessed());
        assertEquals(new BigDecimal("300"),result.getAllowedTotalInclVat().getAssessed());
        assertEquals(new BigDecimal("100"),result.getJrFormFillingCost().getAssessed());
        assertEquals("user123",result.getLastAssessment().getLastAssessedBy());
        assertEquals(OutcomeType.REDUCED_TO_FIXED_FEE, result.getLastAssessment().getLastAssessmentOutcome());
    }

    @Test
    void shouldMapAssessmentToCrimeClaimDetails() {
        // Arrange
        AssessmentGet assessmentGet = new AssessmentGet();
        assessmentGet.setIsVatApplicable(true);
        assessmentGet.setFixedFeeAmount(BigDecimal.valueOf(100));
        assessmentGet.setDisbursementAmount(BigDecimal.valueOf(50));
        assessmentGet.setDisbursementVatAmount(BigDecimal.valueOf(10));
        assessmentGet.setNetProfitCostsAmount(BigDecimal.valueOf(200));
        assessmentGet.setAssessedTotalVat(BigDecimal.valueOf(6));
        assessmentGet.setAssessedTotalInclVat(BigDecimal.valueOf(7));
        assessmentGet.setAllowedTotalInclVat(BigDecimal.valueOf(300));
        assessmentGet.setAllowedTotalVat(BigDecimal.valueOf(20));
        assessmentGet.setCreatedByUserId("user123");
        assessmentGet.setCreatedOn(OffsetDateTime.now());
        assessmentGet.setAssessmentOutcome(AssessmentOutcome.REDUCED_TO_FIXED_FEE);
        assessmentGet.setNetTravelCostsAmount(BigDecimal.valueOf(100));
        assessmentGet.setNetWaitingCostsAmount(BigDecimal.valueOf(200));

        ClaimDetails claimDetails = new CrimeClaimDetails();
        claimDetails.setAreaOfLaw("CRIME_LOWER");
        claimDetails.setAllowedTotalInclVat(new ClaimField(ALLOWED_TOTAL_VAT, BigDecimal.valueOf(345), BigDecimal.valueOf(345), null, null, null, null));
        claimDetails.setAllowedTotalInclVat(new ClaimField(ALLOWED_TOTAL_INCL_VAT, BigDecimal.valueOf(348), BigDecimal.valueOf(348), null, null, null, null));

        // Act
        ClaimDetails result = mapper.mapAssessmentToClaimDetails(assessmentGet, claimDetails);

        // Assert
        assertNotNull(result.getVatClaimed().getAssessed());
        assertEquals(new BigDecimal("100"), result.getFixedFee().getAssessed());
        assertEquals(new BigDecimal("50"), result.getNetDisbursementAmount().getAssessed());
        assertEquals(new BigDecimal("10"), result.getDisbursementVatAmount().getAssessed());
        assertEquals(new BigDecimal("200"),result.getNetProfitCost().getAssessed());
        assertNotNull(result.getVatClaimed().getAssessed());
        assertEquals("user123",result.getLastAssessment().getLastAssessedBy());
        assertEquals(new BigDecimal("6"),result.getAssessedTotalVat().getAssessed());
        assertEquals(new BigDecimal("7"),result.getAssessedTotalInclVat().getAssessed());
        assertEquals(BigDecimal.valueOf(300), result.getAllowedTotalInclVat().getAssessed());
        assertEquals(BigDecimal.valueOf(20), result.getAllowedTotalVat().getAssessed());
        assertEquals(BigDecimal.valueOf(100), ((CrimeClaimDetails)result).getTravelCosts().getAssessed());
        assertEquals(BigDecimal.valueOf(200), ((CrimeClaimDetails)result).getWaitingCosts().getAssessed());
        assertEquals(OutcomeType.REDUCED_TO_FIXED_FEE, result.getLastAssessment().getLastAssessmentOutcome());
    }
    private ClaimField createClaimField(Object value) {
        ClaimField claimField = new ClaimField();
        claimField.setAmended(value);
        return claimField;
    }
}
