package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentServiceTest {

    private AssessmentService assessmentService;

    @BeforeEach
    void setUp() {
        assessmentService = new AssessmentService();
    }

    private CivilClaimDetails createTestCivilClaim() {
        CivilClaimDetails civilClaimDetails = new CivilClaimDetails();
        civilClaimDetails.setClaimId("test-civil-claim-123");
        civilClaimDetails.setSubmissionId("test-submission-456");
        return civilClaimDetails;
    }

    private CrimeClaimDetails createTestCrimeClaim() {
        CrimeClaimDetails crimeClaimDetails = new CrimeClaimDetails();
        crimeClaimDetails.setClaimId("test-crime-claim-123");
        crimeClaimDetails.setSubmissionId("test-submission-456");
        return crimeClaimDetails;
    }

    @Test
    void testApplyNilledOutcome_SetsAllMonetaryFieldsToZero() {
        // Given: A claim with non-zero values
        CivilClaimDetails claim = createTestCivilClaim();
        claim.setFixedFee(new ClaimField("Fixed Fee", null, new BigDecimal("100.00"), new BigDecimal("100.00")));
        claim.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));
        claim.setNetDisbursementAmount(new ClaimField("Disbursement", new BigDecimal("200.00"), new BigDecimal("180.00"), new BigDecimal("180.00")));
        claim.setDisbursementVatAmount(new ClaimField("Disbursement VAT", new BigDecimal("40.00"), new BigDecimal("36.00"), new BigDecimal("36.00")));
        claim.setVatClaimed(new ClaimField("VAT", new BigDecimal("148.00"), new BigDecimal("133.20"), new BigDecimal("133.20")));
        claim.setTotalAmount(new ClaimField("Total", new BigDecimal("988.00"), new BigDecimal("899.20"), new BigDecimal("899.20")));

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Only amendable monetary fields should be set to 0 (not VAT, Total, or Fixed Fee)
        assertEquals(new BigDecimal("100.00"), claim.getFixedFee().getAmended()); // Fixed Fee unchanged (NA)
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
        assertEquals(BigDecimal.ZERO, claim.getDisbursementVatAmount().getAmended());
        assertEquals(new BigDecimal("133.20"), claim.getVatClaimed().getAmended()); // VAT unchanged (calculated)
    }

    @Test
    void testApplyAssessmentOutcome_DoesNotApplyIfOutcomeUnchanged() {
        // Given: A claim with NILLED outcome already set
        CivilClaimDetails claim = createTestCivilClaim();
        claim.setAssessmentOutcome(OutcomeType.NILLED);
        claim.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("100.00")));

        BigDecimal originalAmended = new BigDecimal("100.00");

        // When: Same outcome is applied again
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Amended value should remain unchanged
        assertEquals(originalAmended, claim.getNetProfitCost().getAmended());
    }

    @Test
    void testApplyAssessmentOutcome_AppliesWhenOutcomeChanges() {
        // Given: A claim with no outcome set
        CivilClaimDetails claim = createTestCivilClaim();
        claim.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Amended value should be set to 0
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
    }

    @Test
    void testApplyNilledOutcome_CivilClaimSpecificFields() {
        // Given: A civil claim with non-zero values
        CivilClaimDetails civilClaimDetails = createTestCivilClaim();
        civilClaimDetails.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));
        civilClaimDetails.setCounselsCost(new ClaimField("Counsel Costs", new BigDecimal("200.00"), new BigDecimal("180.00"), new BigDecimal("180.00")));
        civilClaimDetails.setDetentionTravelWaitingCosts(new ClaimField("Detention Costs", new BigDecimal("100.00"), new BigDecimal("90.00"), new BigDecimal("90.00")));
        civilClaimDetails.setJrFormFillingCost(new ClaimField("JR Form Costs", new BigDecimal("50.00"), new BigDecimal("45.00"), new BigDecimal("45.00")));
        civilClaimDetails.setAdjournedHearing(new ClaimField("Adjourned Hearing", "No", "No", true));
        civilClaimDetails.setCmrhTelephone(new ClaimField("CMRH Telephone", 0, 0, 3));
        civilClaimDetails.setCmrhOral(new ClaimField("CMRH Oral", 0, 0, 2));
        civilClaimDetails.setHoInterview(new ClaimField("HO Interview", 0, 0, 1));
        civilClaimDetails.setSubstantiveHearing(new ClaimField("Substantive Hearing", 0, 0, 5));

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(civilClaimDetails, OutcomeType.NILLED);

        // Then: All civil-specific fields should be set appropriately
        assertEquals(BigDecimal.ZERO, civilClaimDetails.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, civilClaimDetails.getCounselsCost().getAmended());
        assertEquals(BigDecimal.ZERO, civilClaimDetails.getDetentionTravelWaitingCosts().getAmended());
        assertEquals(BigDecimal.ZERO, civilClaimDetails.getJrFormFillingCost().getAmended());
        assertEquals(false, civilClaimDetails.getAdjournedHearing().getAmended());
        assertEquals(0, civilClaimDetails.getCmrhTelephone().getAmended());
        assertEquals(0, civilClaimDetails.getCmrhOral().getAmended());
        assertEquals(0, civilClaimDetails.getHoInterview().getAmended());
        assertEquals(0, civilClaimDetails.getSubstantiveHearing().getAmended());
    }

    @Test
    void testApplyNilledOutcome_CrimeClaimSpecificFields() {
        // Given: A crime claim with non-zero values
        CrimeClaimDetails crimeClaimDetails = createTestCrimeClaim();
        crimeClaimDetails.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("450.00")));
        crimeClaimDetails.setTravelCosts(new ClaimField("Travel Costs", new BigDecimal("150.00"), new BigDecimal("135.00"), new BigDecimal("135.00")));
        crimeClaimDetails.setWaitingCosts(new ClaimField("Waiting Costs", new BigDecimal("75.00"), new BigDecimal("67.50"), new BigDecimal("67.50")));

        // When: NILLED outcome is applied
        assessmentService.applyAssessmentOutcome(crimeClaimDetails, OutcomeType.NILLED);

        // Then: All crime-specific fields should be set to zero
        assertEquals(BigDecimal.ZERO, crimeClaimDetails.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, crimeClaimDetails.getTravelCosts().getAmended());
        assertEquals(BigDecimal.ZERO, crimeClaimDetails.getWaitingCosts().getAmended());
    }

    @Test
    void testApplyAssessmentOutcome_SwitchingFromReducedToNilled() {
        // Given: A claim with REDUCED outcome and custom values
        CivilClaimDetails claim = createTestCivilClaim();
        claim.setAssessmentOutcome(OutcomeType.REDUCED);
        claim.setNetProfitCost(new ClaimField("Profit Cost", new BigDecimal("500.00"), new BigDecimal("450.00"), new BigDecimal("300.00")));
        claim.setNetDisbursementAmount(new ClaimField("Disbursement", new BigDecimal("200.00"), new BigDecimal("180.00"), new BigDecimal("150.00")));

        // When: Switching to NILLED outcome
        assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

        // Then: Should apply NILLED logic and set values to zero
        assertEquals(BigDecimal.ZERO, claim.getNetProfitCost().getAmended());
        assertEquals(BigDecimal.ZERO, claim.getNetDisbursementAmount().getAmended());
    }
}