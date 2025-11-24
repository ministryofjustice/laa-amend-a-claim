package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentOutcome;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        claim.setAdjournedHearing(createClaimField(6));
        claim.setJrFormFillingCost(createClaimField(BigDecimal.valueOf(7)));
        claim.setCmrhOral(createClaimField(8));
        claim.setCmrhTelephone(createClaimField(9));
        claim.setHoInterview(createClaimField(10));
        claim.setVatApplicable(true);

        AssessmentPost assessment = mapper.mapCivilClaimToAssessment(claim, userId);

        assertEquals(claimId, assessment.getClaimId());
        assertEquals(claimSummaryFeeId, assessment.getClaimSummaryFeeId());
        assertEquals(AssessmentOutcome.NILLED, assessment.getAssessmentOutcome());
        assertEquals(BigDecimal.valueOf(1), assessment.getFixedFeeAmount());
        assertEquals(BigDecimal.valueOf(2), assessment.getNetProfitCostsAmount());
        assertEquals(BigDecimal.valueOf(3), assessment.getDisbursementAmount());
        assertEquals(BigDecimal.valueOf(4), assessment.getDisbursementVatAmount());
        assertEquals(BigDecimal.valueOf(5), assessment.getNetCostOfCounselAmount());
        assertEquals(6, assessment.getAdjournedHearingFeeAmount());
        assertEquals(BigDecimal.valueOf(7), assessment.getJrFormFillingAmount());
        assertEquals(8, assessment.getCmrhOralCount());
        assertEquals(9, assessment.getCmrhTelephoneCount());
        assertEquals(10, assessment.getHoInterview());
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
        assertEquals(true, assessment.getIsVatApplicable());
        assertEquals(userId, assessment.getCreatedByUserId());
    }

    private ClaimField createClaimField(Object value) {
        ClaimField claimField = new ClaimField();
        claimField.setAmended(value);
        return claimField;
    }
}
