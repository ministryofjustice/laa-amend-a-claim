package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentGet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentOutcome;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        claim.setAllowedTotalVat(ClaimField.builder().assessed(BigDecimal.TWO).build());
        assertEquals(BigDecimal.TWO, mapper.mapAssessedTotalVat(claim));
    }

    @Test
    void testMapAssessedTotalVat_whenAmendedValueIsNotNull() {
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setAssessedTotalVat(ClaimField.builder().assessed(BigDecimal.ONE).build());
        claim.setAllowedTotalVat(ClaimField.builder().assessed(BigDecimal.TWO).build());
        assertEquals(BigDecimal.ONE, mapper.mapAssessedTotalVat(claim));
    }

    @Test
    void testMapAssessedTotalInclVat_whenAmendedValueIsNull() {
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setAssessedTotalInclVat(ClaimField.builder().build());
        claim.setAllowedTotalInclVat(ClaimField.builder().assessed(BigDecimal.TWO).build());
        assertEquals(BigDecimal.TWO, mapper.mapAssessedTotalInclVat(claim));
    }

    @Test
    void testMapAssessedTotalInclVat_whenAmendedValueIsNotNull() {
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setAssessedTotalInclVat(ClaimField.builder().assessed(BigDecimal.ONE).build());
        claim.setAllowedTotalInclVat(ClaimField.builder().assessed(BigDecimal.TWO).build());
        assertEquals(BigDecimal.ONE, mapper.mapAssessedTotalInclVat(claim));
    }

    @Test
    void shouldMapAssessmentToCivilClaimDetails() {
        // Arrange
        AssessmentInfo assessmentGet = new AssessmentInfo();
        assessmentGet.setIsVatApplicable(true);
        assessmentGet.setFixedFeeAmount(BigDecimal.valueOf(100));
        assessmentGet.setDisbursementAmount(BigDecimal.valueOf(50));
        assessmentGet.setDisbursementVatAmount(BigDecimal.valueOf(10));
        assessmentGet.setNetProfitCostsAmount(BigDecimal.valueOf(200));
        assessmentGet.setAssessedTotalVat(BigDecimal.valueOf(6));
        assessmentGet.setAssessedTotalInclVat(BigDecimal.valueOf(7));
        assessmentGet.setAllowedTotalInclVat(BigDecimal.valueOf(300));
        assessmentGet.setAllowedTotalVat(BigDecimal.valueOf(20));
        assessmentGet.setLastAssessedBy("user123");
        assessmentGet.setLastAssessmentDate(OffsetDateTime.now());
        assessmentGet.setJrFormFillingAmount(BigDecimal.valueOf(100));
        assessmentGet.setLastAssessmentOutcome(OutcomeType.REDUCED_TO_FIXED_FEE);

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
    }

    @Test
    void updateClaimShouldUpdateLastAssessment() {
        // Given
        var assessmentDate = OffsetDateTime.now();
        ClaimDetails target = new CivilClaimDetails();
        AssessmentInfo existing = new AssessmentInfo();
        existing.setLastAssessmentOutcome(OutcomeType.PAID_IN_FULL);
        existing.setLastAssessmentDate(assessmentDate);
        existing.setLastAssessedBy("u1");
        target.setLastAssessment(existing);

        AssessmentGet source = new AssessmentGet();
        source.setCreatedByUserId("u1");
        source.setCreatedOn(assessmentDate);
        source.setAssessmentOutcome(AssessmentOutcome.PAID_IN_FULL);

        // When
        mapper.updateClaim(source, target);

        // Then: confirm lastAssessment reference is same (in-place update)
        assertThat(target.getLastAssessment()).usingRecursiveComparison().ignoringActualNullFields().isEqualTo(existing);
    }

    @Test
    void shouldMapAssessmentToCrimeClaimDetails() {
        // Arrange
        AssessmentInfo assessmentGet = new AssessmentInfo();
        assessmentGet.setIsVatApplicable(true);
        assessmentGet.setFixedFeeAmount(BigDecimal.valueOf(100));
        assessmentGet.setDisbursementAmount(BigDecimal.valueOf(50));
        assessmentGet.setDisbursementVatAmount(BigDecimal.valueOf(10));
        assessmentGet.setNetProfitCostsAmount(BigDecimal.valueOf(200));
        assessmentGet.setAssessedTotalVat(BigDecimal.valueOf(6));
        assessmentGet.setAssessedTotalInclVat(BigDecimal.valueOf(7));
        assessmentGet.setAllowedTotalInclVat(BigDecimal.valueOf(300));
        assessmentGet.setAllowedTotalVat(BigDecimal.valueOf(20));
        assessmentGet.setLastAssessedBy("user123");
        assessmentGet.setLastAssessmentDate(OffsetDateTime.now());
        assessmentGet.setLastAssessmentOutcome(OutcomeType.REDUCED_TO_FIXED_FEE);
        assessmentGet.setNetTravelCostsAmount(BigDecimal.valueOf(100));
        assessmentGet.setNetWaitingCostsAmount(BigDecimal.valueOf(200));


        ClaimDetails claimDetails = new CrimeClaimDetails();
        claimDetails.setAreaOfLaw("CRIME_LOWER");
        ClaimField allowedTotalVatField = ClaimField.builder().key(ALLOWED_TOTAL_VAT).submitted(345).calculated(345).build();
        ClaimField allowedTotalInclVatField = ClaimField.builder().key(ALLOWED_TOTAL_INCL_VAT).submitted(348).calculated(348).build();
        claimDetails.setAllowedTotalVat(allowedTotalVatField);
        claimDetails.setAllowedTotalInclVat(allowedTotalInclVatField);
        claimDetails.setLastAssessment(assessmentGet);
        // Act
        ClaimDetails result = mapper.mapAssessmentToClaimDetails(claimDetails);

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
        claimField.setAssessed(value);
        return claimField;
    }
}
