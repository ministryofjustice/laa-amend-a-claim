package uk.gov.justice.laa.amend.claim.mappers;

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
import static uk.gov.justice.laa.amend.claim.utils.NumberUtils.add;

import java.math.BigDecimal;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.AllowedClaimField;
import uk.gov.justice.laa.amend.claim.models.AssessedClaimField;
import uk.gov.justice.laa.amend.claim.models.BoltOnClaimField;
import uk.gov.justice.laa.amend.claim.models.CalculatedTotalClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.models.CostClaimField;
import uk.gov.justice.laa.amend.claim.models.FixedFeeClaimField;
import uk.gov.justice.laa.amend.claim.models.VatLiabilityClaimField;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

@Component
public class ClaimMapperHelper {

    @Named("mapTotalAmount")
    public ClaimField mapTotalAmount(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getTotalAmount()
                : null;
        return new CalculatedTotalClaimField(calculated);
    }

    @Named("mapFixedFee")
    public ClaimField mapFixedFee(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getFixedFeeAmount()
                : null;
        return new FixedFeeClaimField(calculated);
    }

    @Named("mapNetProfitCost")
    public ClaimField mapNetProfitCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetProfitCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetProfitCostsAmount()
                : null;
        return new CostClaimField(NET_PROFIT_COST, submitted, calculated, Cost.PROFIT_COSTS);
    }

    @Named("mapVatClaimed")
    public ClaimField mapVatClaimed(ClaimResponse claimResponse) {
        var submitted = claimResponse.getIsVatApplicable();
        var calculated = claimResponse.getFeeCalculationResponse() != null
                && Boolean.TRUE.equals(claimResponse.getFeeCalculationResponse().getVatIndicator());
        return new VatLiabilityClaimField(submitted, calculated);
    }

    @Named("mapNetDisbursementAmount")
    public ClaimField mapNetDisbursementAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetDisbursementAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDisbursementAmount()
                : null;
        return new CostClaimField(NET_DISBURSEMENTS_COST, submitted, calculated, Cost.DISBURSEMENTS);
    }

    @Named("mapDisbursementVatAmount")
    public ClaimField mapDisbursementVatAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDisbursementsVatAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDisbursementVatAmount()
                : null;
        return new CostClaimField(DISBURSEMENT_VAT, submitted, calculated, Cost.DISBURSEMENTS_VAT);
    }

    @Named("mapCounselsCost")
    public ClaimField mapCounselsCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetCounselCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetCostOfCounselAmount()
                : null;
        return new CostClaimField(COUNSELS_COST, submitted, calculated, Cost.COUNSEL_COSTS);
    }

    @Named("mapDetentionTravelWaitingCosts")
    public ClaimField mapDetentionTravelWaitingCosts(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDetentionTravelWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDetentionTravelAndWaitingCostsAmount()
                : null;
        return new CostClaimField(
                DETENTION_TRAVEL_COST, submitted, calculated, Cost.DETENTION_TRAVEL_AND_WAITING_COSTS);
    }

    @Named("mapJrFormFillingCost")
    public ClaimField mapJrFormFillingCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getJrFormFillingAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getJrFormFillingAmount()
                : null;
        return new CostClaimField(JR_FORM_FILLING, submitted, calculated, Cost.JR_FORM_FILLING_COSTS);
    }

    @Named("mapAdjournedHearingFee")
    public ClaimField mapAdjournedHearingFee(ClaimResponse claimResponse) {
        var submitted = claimResponse.getAdjournedHearingFeeAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                        && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnAdjournedHearingFee()
                : null;
        return new BoltOnClaimField(ADJOURNED_FEE, submitted, calculated);
    }

    @Named("mapCmrhTelephone")
    public ClaimField mapCmrhTelephone(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhTelephoneCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                        && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhTelephoneFee()
                : null;
        return new BoltOnClaimField(CMRH_TELEPHONE, submitted, calculated);
    }

    @Named("mapCmrhOral")
    public ClaimField mapCmrhOral(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhOralCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                        && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhOralFee()
                : null;
        return new BoltOnClaimField(CMRH_ORAL, submitted, calculated);
    }

    @Named("mapHoInterview")
    public ClaimField mapHoInterview(ClaimResponse claimResponse) {
        var submitted = claimResponse.getHoInterview();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                        && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnHomeOfficeInterviewFee()
                : null;
        return new BoltOnClaimField(HO_INTERVIEW, submitted, calculated);
    }

    @Named("mapSubstantiveHearing")
    public ClaimField mapSubstantiveHearing(ClaimResponse claimResponse) {
        var submitted = claimResponse.getIsSubstantiveHearing();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                        && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnSubstantiveHearingFee()
                : null;
        return new BoltOnClaimField(SUBSTANTIVE_HEARING, submitted, calculated);
    }

    @Named("mapTravelCosts")
    public ClaimField mapTravelCosts(ClaimResponse claimResponse) {
        var submitted = claimResponse.getTravelWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetTravelCostsAmount()
                : null;
        return new CostClaimField(TRAVEL_COSTS, submitted, calculated, Cost.TRAVEL_COSTS);
    }

    @Named("mapWaitingCosts")
    public ClaimField mapWaitingCosts(ClaimResponse claimResponse) {
        var submitted = claimResponse.getNetWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetWaitingCostsAmount()
                : null;
        return new CostClaimField(WAITING_COSTS, submitted, calculated, Cost.WAITING_COSTS);
    }

    @Named("mapAssessedTotalVat")
    public ClaimField mapAssessedTotalVat() {
        return new AssessedClaimField(ASSESSED_TOTAL_VAT);
    }

    @Named("mapAssessedTotalInclVat")
    public ClaimField mapAssessedTotalInclVat() {
        return new AssessedClaimField(ASSESSED_TOTAL_INCL_VAT);
    }

    @Named("mapAllowedTotalVat")
    public ClaimField mapAllowedTotalVat(ClaimResponse claimResponse) {
        FeeCalculationPatch fee = claimResponse.getFeeCalculationResponse();
        BigDecimal calculated = fee != null ? add(fee.getCalculatedVatAmount(), fee.getDisbursementVatAmount()) : null;
        return new AllowedClaimField(ALLOWED_TOTAL_VAT, calculated);
    }

    @Named("mapAllowedTotalInclVat")
    public ClaimField mapAllowedTotalInclVat(ClaimResponse claimResponse) {
        FeeCalculationPatch fee = claimResponse.getFeeCalculationResponse();
        BigDecimal calculated = fee != null ? fee.getTotalAmount() : null;
        return new AllowedClaimField(ALLOWED_TOTAL_INCL_VAT, calculated);
    }
}
