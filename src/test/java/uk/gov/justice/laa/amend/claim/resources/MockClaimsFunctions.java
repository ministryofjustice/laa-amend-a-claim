package uk.gov.justice.laa.amend.claim.resources;

import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.AllowedClaimField;
import uk.gov.justice.laa.amend.claim.models.AssessedClaimField;
import uk.gov.justice.laa.amend.claim.models.BoltOnClaimField;
import uk.gov.justice.laa.amend.claim.models.CalculatedTotalClaimField;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.models.CostClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.FixedFeeClaimField;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.models.VatLiabilityClaimField;

import java.math.BigDecimal;
import java.util.UUID;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ALLOWED_TOTAL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ASSESSED_TOTAL_INCL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ASSESSED_TOTAL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.COUNSELS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DETENTION_TRAVEL_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DISBURSEMENT_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.JR_FORM_FILLING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_DISBURSEMENTS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TRAVEL_COSTS;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.WAITING_COSTS;

public class MockClaimsFunctions {

    private static final ClaimStatusHandler claimStatusHandler = new ClaimStatusHandler();
    public static CivilClaimDetails createMockCivilClaim(){
        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setClaimId(UUID.randomUUID().toString());
        claim.setSubmissionId(UUID.randomUUID().toString());
        claim.setClaimSummaryFeeId(UUID.randomUUID().toString());
        claim.setEscaped(true);

        claim.setFixedFee(createFixedFeeField());
        claim.setNetProfitCost(createNetProfitCostField());
        claim.setNetDisbursementAmount(createDisbursementCostField());
        claim.setDisbursementVatAmount(createDisbursementVatCostField());
        claim.setTotalAmount(createTotalAmountField());
        claim.setCounselsCost(createCounselCostField());
        claim.setDetentionTravelWaitingCosts(createDetentionCostField());
        claim.setJrFormFillingCost(createJrFormFillingCostField());
        claim.setAdjournedHearing(createAdjournedHearingField());
        claim.setCmrhTelephone(createCmrhTelephoneField());
        claim.setCmrhOral(createCmrhOralField());
        claim.setHoInterview(createHoInterviewField());
        claim.setSubstantiveHearing(createSubstantiveHearingField());
        claim.setVatClaimed(createVatClaimedField());
        claim.setAssessmentOutcome(OutcomeType.REDUCED);

        claim.setAssessedTotalVat(createAssessedTotalVatField());
        claim.setAssessedTotalInclVat(createAssessedTotalInclVatField());
        claim.setAllowedTotalVat(createAllowedTotalVatField());
        claim.setAllowedTotalInclVat(createAllowedTotalInclVatField());
        return claim;
    }

    public static CrimeClaimDetails createMockCrimeClaim() {
        CrimeClaimDetails claim = new CrimeClaimDetails();
        claim.setClaimId(UUID.randomUUID().toString());
        claim.setSubmissionId(UUID.randomUUID().toString());
        claim.setClaimSummaryFeeId(UUID.randomUUID().toString());
        claim.setEscaped(true);

        claim.setNetProfitCost(createNetProfitCostField());
        claim.setTravelCosts(createTravelCostField());
        claim.setWaitingCosts(createWaitingCostField());
        claim.setFixedFee(createFixedFeeField());
        claim.setNetDisbursementAmount(createDisbursementCostField());
        claim.setDisbursementVatAmount(createDisbursementVatCostField());
        claim.setVatClaimed(createVatClaimedField());
        claim.setTotalAmount(createTotalAmountField());

        claim.setAssessedTotalVat(createAssessedTotalVatField());
        claim.setAssessedTotalInclVat(createAssessedTotalInclVatField());

        claim.setAllowedTotalVat(createAllowedTotalVatField());
        claim.setAllowedTotalInclVat(createAllowedTotalInclVatField());
        claim.setAssessmentOutcome(OutcomeType.REDUCED_TO_FIXED_FEE);

        return claim;
    }

    public static void updateStatus(ClaimDetails claim, OutcomeType outcome) {
        claimStatusHandler.updateFieldStatuses(claim, outcome);
    }

    public static ClaimField updateClaimFieldSubmittedValue(ClaimField claimField, Object submitted) {
        claimField.setSubmitted(submitted);
        return claimField;
    }

    public static CostClaimField createNetProfitCostField() {
        CostClaimField field = createCostField(NET_PROFIT_COST, Cost.PROFIT_COSTS);
        field.setCalculated(null);
        return field;
    }

    public static CostClaimField createDisbursementCostField() {
        return createCostField(NET_DISBURSEMENTS_COST, Cost.DISBURSEMENTS);
    }

    public static CostClaimField createDisbursementVatCostField() {
        return createCostField(DISBURSEMENT_VAT, Cost.DISBURSEMENTS_VAT);
    }

    public static CostClaimField createCounselCostField() {
        return createCostField(COUNSELS_COST, Cost.COUNSEL_COSTS);
    }

    public static CostClaimField createTravelCostField() {
        return createCostField(TRAVEL_COSTS, Cost.TRAVEL_COSTS);
    }

    public static CostClaimField createWaitingCostField() {
        return createCostField(WAITING_COSTS, Cost.WAITING_COSTS);
    }

    public static CostClaimField createJrFormFillingCostField() {
        return createCostField(JR_FORM_FILLING, Cost.JR_FORM_FILLING_COSTS);
    }

    public static CostClaimField createDetentionCostField() {
        return createCostField(DETENTION_TRAVEL_COST, Cost.DETENTION_TRAVEL_AND_WAITING_COSTS);
    }
    
    private static CostClaimField createCostField(String key, Cost cost) {
        return CostClaimField
            .builder()
            .key(key)
            .submitted(BigDecimal.valueOf(100))
            .calculated(BigDecimal.valueOf(200))
            .assessed(BigDecimal.valueOf(300))
            .cost(cost)
            .build();
    }

    public static FixedFeeClaimField createFixedFeeField() {
        return FixedFeeClaimField
            .builder()
            .calculated(BigDecimal.valueOf(200))
            .assessed(BigDecimal.valueOf(300))
            .build();
    }

    public static CalculatedTotalClaimField createTotalAmountField() {
        return CalculatedTotalClaimField
            .builder()
            .submitted(BigDecimal.valueOf(100))
            .calculated(BigDecimal.valueOf(200))
            .assessed(BigDecimal.valueOf(300))
            .build();
    }

    public static VatLiabilityClaimField createVatClaimedField() {
        return VatLiabilityClaimField
            .builder()
            .submitted(true)
            .calculated(false)
            .assessed(true)
            .build();
    }

    public static BoltOnClaimField createAdjournedHearingField() {
        return createBoltOnField(ADJOURNED_FEE);
    }

    public static BoltOnClaimField createCmrhOralField() {
        return createBoltOnField(CMRH_ORAL);
    }

    public static BoltOnClaimField createCmrhTelephoneField() {
        return createBoltOnField(CMRH_TELEPHONE);
    }

    public static BoltOnClaimField createHoInterviewField() {
        return createBoltOnField(HO_INTERVIEW);
    }

    public static BoltOnClaimField createSubstantiveHearingField() {
        return createBoltOnField(SUBSTANTIVE_HEARING);
    }

    private static BoltOnClaimField createBoltOnField(String key) {
        return BoltOnClaimField
            .builder()
            .key(key)
            .submitted(BigDecimal.valueOf(100))
            .calculated(BigDecimal.valueOf(200))
            .assessed(BigDecimal.valueOf(300))
            .build();
    }

    public static AssessedClaimField createAssessedTotalVatField() {
        return createAssessedTotalField(ASSESSED_TOTAL_VAT);
    }

    public static AssessedClaimField createAssessedTotalInclVatField() {
        return createAssessedTotalField(ASSESSED_TOTAL_INCL_VAT);
    }

    private static AssessedClaimField createAssessedTotalField(String key) {
        return AssessedClaimField
            .builder()
            .key(key)
            .submitted(BigDecimal.valueOf(100))
            .calculated(BigDecimal.valueOf(200))
            .assessed(BigDecimal.valueOf(300))
            .build();
    }

    public static AllowedClaimField createAllowedTotalVatField() {
        return createAllowedTotalField(ALLOWED_TOTAL_VAT);
    }

    public static AllowedClaimField createAllowedTotalInclVatField() {
        return createAllowedTotalField(ALLOWED_TOTAL_INCL_VAT);
    }

    private static AllowedClaimField createAllowedTotalField(String key) {
        return AllowedClaimField
            .builder()
            .key(key)
            .submitted(BigDecimal.valueOf(100))
            .calculated(BigDecimal.valueOf(200))
            .assessed(BigDecimal.valueOf(300))
            .build();
    }
}
