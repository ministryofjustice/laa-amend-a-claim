package uk.gov.justice.laa.amend.claim.resources;

import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
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

        claim.setFixedFee(createClaimField(FIXED_FEE, ClaimFieldType.FIXED_FEE));
        claim.setNetProfitCost(createClaimField(NET_PROFIT_COST, ClaimFieldType.OTHER));
        claim.setNetDisbursementAmount(createClaimField(NET_DISBURSEMENTS_COST, ClaimFieldType.OTHER));
        claim.setDisbursementVatAmount(createClaimField(DISBURSEMENT_VAT, ClaimFieldType.OTHER));
        claim.setTotalAmount(createClaimField(TOTAL, ClaimFieldType.CALCULATED_TOTAL));
        claim.setCounselsCost(createClaimField(COUNSELS_COST, ClaimFieldType.OTHER));
        claim.setDetentionTravelWaitingCosts(createClaimField(DETENTION_TRAVEL_COST, ClaimFieldType.OTHER));
        claim.setJrFormFillingCost(createClaimField(JR_FORM_FILLING, ClaimFieldType.OTHER));
        claim.setAdjournedHearing(createClaimField(ADJOURNED_FEE, ClaimFieldType.BOLT_ON));
        claim.setCmrhTelephone(createClaimField(CMRH_TELEPHONE, ClaimFieldType.BOLT_ON));
        claim.setCmrhOral(createClaimField(CMRH_ORAL, ClaimFieldType.BOLT_ON));
        claim.setHoInterview(createClaimField(HO_INTERVIEW, ClaimFieldType.BOLT_ON));
        claim.setSubstantiveHearing(createClaimField(SUBSTANTIVE_HEARING, ClaimFieldType.BOLT_ON));
        claim.setVatClaimed(createBooleanClaimField(VAT));
        claim.setAssessmentOutcome(OutcomeType.REDUCED);

        claim.setAssessedTotalVat(createClaimField(ASSESSED_TOTAL_VAT, ClaimFieldType.ASSESSED_TOTAL));
        claim.setAssessedTotalInclVat(createClaimField(ASSESSED_TOTAL_INCL_VAT, ClaimFieldType.ASSESSED_TOTAL));
        claim.setAllowedTotalInclVat(createClaimField(ALLOWED_TOTAL_INCL_VAT, ClaimFieldType.ALLOWED_TOTAL));
        claim.setAllowedTotalVat(createClaimField(ALLOWED_TOTAL_VAT, ClaimFieldType.ALLOWED_TOTAL));
        return claim;
    }

    public static CrimeClaimDetails createMockCrimeClaim() {
        CrimeClaimDetails claim = new CrimeClaimDetails();
        claim.setClaimId("test-crime-claim-123");
        claim.setSubmissionId("test-submission-456");
        claim.setEscaped(true);

        claim.setNetProfitCost(createClaimField(NET_PROFIT_COST, ClaimFieldType.OTHER));
        claim.setTravelCosts(createClaimField(TRAVEL_COSTS, ClaimFieldType.OTHER));
        claim.setWaitingCosts(createClaimField(WAITING_COSTS, ClaimFieldType.OTHER));
        claim.setFixedFee(createClaimField(FIXED_FEE, ClaimFieldType.FIXED_FEE));
        claim.setNetDisbursementAmount(createClaimField(NET_DISBURSEMENTS_COST, ClaimFieldType.OTHER));
        claim.setDisbursementVatAmount(createClaimField(DISBURSEMENT_VAT, ClaimFieldType.OTHER));
        claim.setVatClaimed(createBooleanClaimField(VAT));

        claim.setAssessedTotalVat(createClaimField(ASSESSED_TOTAL_VAT, ClaimFieldType.ASSESSED_TOTAL));
        claim.setAssessedTotalInclVat(createClaimField(ASSESSED_TOTAL_INCL_VAT, ClaimFieldType.ASSESSED_TOTAL));

        claim.setAllowedTotalInclVat(createClaimField(ALLOWED_TOTAL_INCL_VAT, ClaimFieldType.ALLOWED_TOTAL));
        claim.setAllowedTotalVat(createClaimField(ALLOWED_TOTAL_VAT, ClaimFieldType.ALLOWED_TOTAL));
        claim.setAssessmentOutcome(OutcomeType.REDUCED_TO_FIXED_FEE);

        return claim;
    }

    public static ClaimField createClaimField(ClaimFieldStatus status) {
        return ClaimField.builder()
            .key("foo")
            .submitted(BigDecimal.valueOf(100))
            .calculated(BigDecimal.valueOf(200))
            .assessed(BigDecimal.valueOf(300))
            .status(status)
            .type(ClaimFieldType.OTHER)
            .build();
    }

    public static ClaimField createClaimField(String key, ClaimFieldType type) {
        return ClaimField.builder()
            .key(key)
            .submitted(BigDecimal.valueOf(100))
            .calculated(BigDecimal.valueOf(200))
            .assessed(BigDecimal.valueOf(300))
            .type(type)
            .build();
    }

    public static ClaimField createBooleanClaimField(String key) {
        return ClaimField.builder()
            .key(key)
            .submitted(true)
            .calculated(false)
            .assessed(true)
            .type(ClaimFieldType.OTHER)
            .build();
    }

    public static void updateStatus(ClaimDetails claim, OutcomeType outcome) {
        claimStatusHandler.updateFieldStatuses(claim, outcome);
    }

    public static ClaimField updateClaimFieldSubmittedValue(ClaimField claimField, Object submitted) {
        claimField.setSubmitted(BigDecimal.ZERO);
        return claimField;
    }
}
