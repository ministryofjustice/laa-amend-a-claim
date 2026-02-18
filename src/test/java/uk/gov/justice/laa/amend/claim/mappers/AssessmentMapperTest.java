package uk.gov.justice.laa.amend.claim.mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ALLOWED_TOTAL_VAT;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.justice.laa.amend.claim.models.AllowedClaimField;
import uk.gov.justice.laa.amend.claim.models.AssessedClaimField;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AreaOfLaw;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentGet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentOutcome;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

@SpringJUnitConfig
@ContextConfiguration(classes = AssessmentMapperImpl.class)
class AssessmentMapperTest {

    @Autowired
    private AssessmentMapper mapper;

    @Test
    void testMapCivilClaimToAssessment() {
        String userId = UUID.randomUUID().toString();

        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setVatApplicable(true);

        AssessmentPost assessment = mapper.mapCivilClaimToAssessment(claim, userId);

        assertEquals(claim.getClaimId(), assessment.getClaimId().toString());
        assertEquals(
                claim.getClaimSummaryFeeId(), assessment.getClaimSummaryFeeId().toString());
        assertEquals(AssessmentOutcome.REDUCED_STILL_ESCAPED, assessment.getAssessmentOutcome());
        assertEquals(BigDecimal.valueOf(300), assessment.getFixedFeeAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getNetProfitCostsAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getDisbursementAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getDisbursementVatAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getNetCostOfCounselAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getBoltOnAdjournedHearingFee());
        assertEquals(BigDecimal.valueOf(300), assessment.getJrFormFillingAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getBoltOnCmrhOralFee());
        assertEquals(BigDecimal.valueOf(300), assessment.getBoltOnCmrhTelephoneFee());
        assertEquals(BigDecimal.valueOf(300), assessment.getBoltOnHomeOfficeInterviewFee());
        assertEquals(BigDecimal.valueOf(300), assessment.getDetentionTravelAndWaitingCostsAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getBoltOnSubstantiveHearingFee());
        assertEquals(BigDecimal.valueOf(300), assessment.getAssessedTotalVat());
        assertEquals(BigDecimal.valueOf(300), assessment.getAssessedTotalInclVat());
        assertEquals(BigDecimal.valueOf(300), assessment.getAllowedTotalVat());
        assertEquals(BigDecimal.valueOf(300), assessment.getAllowedTotalInclVat());
        assertEquals(true, assessment.getIsVatApplicable());
        assertEquals(userId, assessment.getCreatedByUserId());
    }

    @Test
    void testMapCrimeClaimToAssessment() {
        String userId = UUID.randomUUID().toString();

        CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();
        claim.setVatApplicable(true);

        AssessmentPost assessment = mapper.mapCrimeClaimToAssessment(claim, userId);

        assertEquals(claim.getClaimId(), assessment.getClaimId().toString());
        assertEquals(
                claim.getClaimSummaryFeeId(), assessment.getClaimSummaryFeeId().toString());
        assertEquals(AssessmentOutcome.REDUCED_TO_FIXED_FEE, assessment.getAssessmentOutcome());
        assertEquals(BigDecimal.valueOf(300), assessment.getFixedFeeAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getNetProfitCostsAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getDisbursementAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getDisbursementVatAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getNetTravelCostsAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getNetWaitingCostsAmount());
        assertEquals(BigDecimal.valueOf(300), assessment.getAssessedTotalVat());
        assertEquals(BigDecimal.valueOf(300), assessment.getAssessedTotalInclVat());
        assertEquals(BigDecimal.valueOf(300), assessment.getAllowedTotalVat());
        assertEquals(BigDecimal.valueOf(300), assessment.getAllowedTotalInclVat());
        assertEquals(true, assessment.getIsVatApplicable());
        assertEquals(userId, assessment.getCreatedByUserId());
    }

    @Test
    void testMapAssessedTotalVat() {
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setAssessedTotalVat(
                AssessedClaimField.builder().assessed(BigDecimal.ONE).build());
        claim.setAllowedTotalVat(
                AllowedClaimField.builder().assessed(BigDecimal.TWO).build());
        assertEquals(BigDecimal.ONE, mapper.mapAssessedTotalVat(claim));
    }

    @Test
    void testMapAssessedTotalInclVat() {
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setAssessedTotalInclVat(
                AssessedClaimField.builder().assessed(BigDecimal.ONE).build());
        claim.setAllowedTotalInclVat(
                AllowedClaimField.builder().assessed(BigDecimal.TWO).build());
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

        CivilClaimDetails claimDetails = MockClaimsFunctions.createMockCivilClaim();
        claimDetails.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);

        // Act
        CivilClaimDetails result = mapper.mapToCivilClaim(assessmentGet, claimDetails);
        // Assert
        assertNotNull(result.getVatClaimed().getAssessed());
        assertEquals(new BigDecimal("100"), result.getFixedFee().getAssessed());
        assertEquals(new BigDecimal("50"), result.getNetDisbursementAmount().getAssessed());
        assertEquals(new BigDecimal("10"), result.getDisbursementVatAmount().getAssessed());
        assertEquals(new BigDecimal("200"), result.getNetProfitCost().getAssessed());
        assertEquals(new BigDecimal("6"), result.getAssessedTotalVat().getAssessed());
        assertEquals(new BigDecimal("7"), result.getAssessedTotalInclVat().getAssessed());
        assertEquals(new BigDecimal("20"), result.getAllowedTotalVat().getAssessed());
        assertEquals(new BigDecimal("300"), result.getAllowedTotalInclVat().getAssessed());
        assertEquals(new BigDecimal("100"), result.getJrFormFillingCost().getAssessed());
    }

    @Test
    void updateClaimShouldUpdateLastAssessment() {
        // Given
        var assessmentDate = OffsetDateTime.now();
        AssessmentInfo existing = new AssessmentInfo();
        existing.setLastAssessmentOutcome(OutcomeType.PAID_IN_FULL);
        existing.setLastAssessmentDate(assessmentDate);
        existing.setLastAssessedBy("u1");
        ClaimDetails target = new CivilClaimDetails();
        target.setLastAssessment(existing);

        AssessmentGet source = new AssessmentGet();
        source.setCreatedByUserId("u1");
        source.setCreatedOn(assessmentDate);
        source.setAssessmentOutcome(AssessmentOutcome.PAID_IN_FULL);

        // When
        mapper.updateClaim(source, target);

        // Then: confirm lastAssessment reference is same (in-place update)
        assertThat(target.getLastAssessment())
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(existing);
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

        ClaimDetails claimDetails = MockClaimsFunctions.createMockCrimeClaim();
        claimDetails.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
        ClaimField allowedTotalVatField = AllowedClaimField.builder()
                .key(ALLOWED_TOTAL_VAT)
                .submitted(345)
                .calculated(345)
                .build();
        ClaimField allowedTotalInclVatField = AllowedClaimField.builder()
                .key(ALLOWED_TOTAL_INCL_VAT)
                .submitted(348)
                .calculated(348)
                .build();
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
        assertEquals(new BigDecimal("200"), result.getNetProfitCost().getAssessed());
        assertNotNull(result.getVatClaimed().getAssessed());
        assertEquals("user123", result.getLastAssessment().getLastAssessedBy());
        assertEquals(new BigDecimal("6"), result.getAssessedTotalVat().getAssessed());
        assertEquals(new BigDecimal("7"), result.getAssessedTotalInclVat().getAssessed());
        assertEquals(BigDecimal.valueOf(300), result.getAllowedTotalInclVat().getAssessed());
        assertEquals(BigDecimal.valueOf(20), result.getAllowedTotalVat().getAssessed());
        assertEquals(
                BigDecimal.valueOf(100),
                ((CrimeClaimDetails) result).getTravelCosts().getAssessed());
        assertEquals(
                BigDecimal.valueOf(200),
                ((CrimeClaimDetails) result).getWaitingCosts().getAssessed());
        assertEquals(
                OutcomeType.REDUCED_TO_FIXED_FEE, result.getLastAssessment().getLastAssessmentOutcome());
    }
}
