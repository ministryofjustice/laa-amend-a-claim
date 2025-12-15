package uk.gov.justice.laa.amend.claim.resources;

import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldStatus;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import java.math.BigDecimal;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.*;

public class MockClaimsFunctions {

    private static final ClaimStatusHandler claimStatusHandler = new ClaimStatusHandler();
    public static CivilClaimDetails createMockCivilClaim(){
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setClaimId("test-civil-claim-123");
        claim.setSubmissionId("test-submission-456");
        claim.setEscaped(true);

        claim.setFixedFee(createClaimField(FIXED_FEE));
        claim.setNetProfitCost(createClaimField(NET_PROFIT_COST));
        claim.setNetDisbursementAmount(createClaimField(NET_DISBURSEMENTS_COST));
        claim.setDisbursementVatAmount(createClaimField(DISBURSEMENT_VAT));
        claim.setTotalAmount(createClaimField(TOTAL));
        claim.setCounselsCost(createClaimField(COUNSELS_COST));
        claim.setDetentionTravelWaitingCosts(createClaimField(DETENTION_TRAVEL_COST));
        claim.setJrFormFillingCost(createClaimField(JR_FORM_FILLING));
        claim.setAdjournedHearing(createClaimField(ADJOURNED_FEE));
        claim.setCmrhTelephone(createClaimField(CMRH_TELEPHONE));
        claim.setCmrhOral(createClaimField(CMRH_ORAL));
        claim.setHoInterview(createClaimField(HO_INTERVIEW));
        claim.setSubstantiveHearing(createClaimField(SUBSTANTIVE_HEARING));
        claim.setVatClaimed(createBooleanClaimField(VAT));
        claim.setAssessmentOutcome(OutcomeType.REDUCED);

        claim.setAssessedTotalVat(createClaimField(ASSESSED_TOTAL_VAT));
        claim.setAssessedTotalInclVat(createClaimField(ASSESSED_TOTAL_INCL_VAT));
        claim.setAllowedTotalInclVat(createClaimField(ALLOWED_TOTAL_INCL_VAT));
        claim.setAllowedTotalVat(createClaimField(ALLOWED_TOTAL_VAT));
        return claim;
    }

    public static CrimeClaimDetails createMockCrimeClaim() {
        CrimeClaimDetails claim = new CrimeClaimDetails();
        claim.setClaimId("test-crime-claim-123");
        claim.setSubmissionId("test-submission-456");
        claim.setEscaped(true);

        claim.setNetProfitCost(createClaimField(NET_PROFIT_COST));
        claim.setTravelCosts(createClaimField(TRAVEL_COSTS));
        claim.setWaitingCosts(createClaimField(WAITING_COSTS));
        claim.setFixedFee(createClaimField(FIXED_FEE));
        claim.setNetDisbursementAmount(createClaimField(NET_DISBURSEMENTS_COST));
        claim.setDisbursementVatAmount(createClaimField(DISBURSEMENT_VAT));
        claim.setVatClaimed(createBooleanClaimField(VAT));

        claim.setAssessedTotalVat(createClaimField(ASSESSED_TOTAL_VAT));
        claim.setAssessedTotalInclVat(createClaimField(ASSESSED_TOTAL_INCL_VAT));

        claim.setAllowedTotalInclVat(createClaimField(ALLOWED_TOTAL_INCL_VAT));
        claim.setAllowedTotalVat(createClaimField(ALLOWED_TOTAL_VAT));
        claim.setAssessmentOutcome(OutcomeType.REDUCED_TO_FIXED_FEE);

        return claim;
    }

    public static ClaimField createClaimField(ClaimFieldStatus status) {
        return new ClaimField(
            "foo",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(200),
            BigDecimal.valueOf(300),
            null,
            status
        );
    }

    public static ClaimField createClaimField(String key) {
        return new ClaimField(
                key,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(300)
        );
    }

    public static ClaimField createBooleanClaimField(String key) {
        return new ClaimField(
                key,
                true,
                false,
                true
        );
    }

    public static void updateStatus(ClaimDetails claim, OutcomeType outcome) {
        claimStatusHandler.updateFieldStatuses(claim, outcome);
    }
}
