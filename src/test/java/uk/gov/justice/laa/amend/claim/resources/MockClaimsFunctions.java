package uk.gov.justice.laa.amend.claim.resources;

import uk.gov.justice.laa.amend.claim.models.AmendStatus;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MockClaimsFunctions {

    public static CivilClaimDetails createMockCivilClaim(){
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setClaimId("test-civil-claim-123");
        claim.setSubmissionId("test-submission-456");
        claim.setEscaped(true);

        claim.setFixedFee(createClaimField());
        claim.setNetProfitCost(createClaimField());
        claim.setNetDisbursementAmount(createClaimField());
        claim.setDisbursementVatAmount(createClaimField());
        claim.setTotalAmount(createClaimField());
        claim.setCounselsCost(createClaimField());
        claim.setDetentionTravelWaitingCosts(createClaimField());
        claim.setJrFormFillingCost(createClaimField());
        claim.setAdjournedHearing(createClaimField());
        claim.setCmrhTelephone(createClaimField());
        claim.setCmrhOral(createClaimField());
        claim.setHoInterview(createClaimField());
        claim.setSubstantiveHearing(createClaimField());
        claim.setVatClaimed(createBooleanClaimField());

        claim.setAllowedTotalInclVat(createClaimField());
        claim.setAllowedTotalVat(createClaimField());

        return claim;
    }

    public static CrimeClaimDetails createMockCrimeClaim() {
        CrimeClaimDetails claim = new CrimeClaimDetails();
        claim.setClaimId("test-crime-claim-123");
        claim.setSubmissionId("test-submission-456");
        claim.setEscaped(true);

        claim.setNetProfitCost(createClaimField());
        claim.setTravelCosts(createClaimField());
        claim.setWaitingCosts(createClaimField());
        claim.setFixedFee(createClaimField());
        claim.setNetDisbursementAmount(createClaimField());
        claim.setDisbursementVatAmount(createClaimField());
        claim.setVatClaimed(createBooleanClaimField());

        claim.setAllowedTotalInclVat(createClaimField());
        claim.setAllowedTotalVat(createClaimField());

        return claim;
    }

    public static ClaimField createClaimField(AmendStatus status) {
        return new ClaimField(
            "foo",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(200),
            BigDecimal.valueOf(300),
            null,
            status
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

    public static ClaimField createBooleanClaimField() {
        return new ClaimField(
            "foo",
            true,
            false,
            true
        );
    }
}
