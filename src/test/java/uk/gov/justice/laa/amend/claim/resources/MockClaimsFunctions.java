package uk.gov.justice.laa.amend.claim.resources;

import uk.gov.justice.laa.amend.claim.models.AmendStatus;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MockClaimsFunctions {

    public static CivilClaimDetails createBaseMockCivilClaim(){
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setClaimId("test-civil-claim-123");
        claim.setSubmissionId("test-submission-456");

        return claim;
    }

    public static CivilClaimDetails createMockCivilClaim(){
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setClaimId("test-civil-claim-123");
        claim.setSubmissionId("test-submission-456");

        claim.setFixedFee(createClaimField());
        claim.setNetProfitCost(createClaimField());
        claim.setNetDisbursementAmount(createClaimField());
        claim.setDisbursementVatAmount(createClaimField());
        claim.setVatClaimed(createClaimField());
        claim.setTotalAmount(createClaimField());
        claim.setCounselsCost(createClaimField());
        claim.setDetentionTravelWaitingCosts(createClaimField());
        claim.setJrFormFillingCost(createClaimField());
        claim.setAdjournedHearing(createClaimField());
        claim.setCmrhTelephone(createClaimField());
        claim.setCmrhOral(createClaimField());
        claim.setHoInterview(createClaimField());
        claim.setSubstantiveHearing(createClaimField());
        claim.setVatClaimed(createClaimField());

        claim.setAllowedTotalInclVat(createClaimFieldWithStatus(AmendStatus.NEEDS_AMENDING));
        claim.setAllowedTotalVat(createClaimFieldWithStatus(AmendStatus.NEEDS_AMENDING));

        return claim;
    }

    public static void assertCivilAllowedTotalsAreCorrect(CivilClaimDetails claim){
        assertEquals(BigDecimal.valueOf(100), claim.getAllowedTotalInclVat().getSubmitted());
        assertEquals(BigDecimal.valueOf(200), claim.getAllowedTotalInclVat().getCalculated());
        assertNull(claim.getAllowedTotalInclVat().getAmended());
        assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

        assertEquals(BigDecimal.valueOf(100), claim.getAllowedTotalVat().getSubmitted());
        assertEquals(BigDecimal.valueOf(200), claim.getAllowedTotalVat().getCalculated());
        assertNull(claim.getAllowedTotalVat().getAmended());
        assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
    }

    public static void assertCrimeAllowedTotalsAreCorrect(CrimeClaimDetails claim){
        assertEquals(BigDecimal.valueOf(100), claim.getAllowedTotalInclVat().getSubmitted());
        assertEquals(BigDecimal.valueOf(200), claim.getAllowedTotalInclVat().getCalculated());
        assertNull(claim.getAllowedTotalInclVat().getAmended());
        assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalInclVat().getStatus());

        assertEquals(BigDecimal.valueOf(100), claim.getAllowedTotalVat().getSubmitted());
        assertEquals(BigDecimal.valueOf(200), claim.getAllowedTotalVat().getCalculated());
        assertNull(claim.getAllowedTotalVat().getAmended());
        assertEquals(AmendStatus.NEEDS_AMENDING, claim.getAllowedTotalVat().getStatus());
    }

     public static CrimeClaimDetails createMockCrimeClaim() {
        CrimeClaimDetails claim = new CrimeClaimDetails();
        claim.setClaimId("test-crime-claim-123");
        claim.setSubmissionId("test-submission-456");
        claim.setNetProfitCost(createClaimField());
        claim.setTravelCosts(createClaimField());
        claim.setWaitingCosts(createClaimField());
        claim.setVatClaimed(createClaimField());
        claim.setFixedFee(createClaimField());
        claim.setNetDisbursementAmount(createClaimField());
        claim.setDisbursementVatAmount(createClaimField());

        claim.setAllowedTotalInclVat(createClaimFieldWithStatus(AmendStatus.NEEDS_AMENDING));
        claim.setAllowedTotalVat(createClaimFieldWithStatus(AmendStatus.NEEDS_AMENDING));

        return claim;
    }

    public static ClaimField createClaimFieldWithStatus(AmendStatus status) {
        return new ClaimField(
                "foo",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(300),
                null,
                status,
                null

        );
    }


     public static ClaimField createClaimField() {
        return new ClaimField(
                "foo",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(300)
        );
    }

}
