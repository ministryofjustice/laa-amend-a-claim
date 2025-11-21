package uk.gov.justice.laa.amend.claim.mappers;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldValue;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

import java.math.BigDecimal;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.COUNSELS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DETENTION_TRAVEL_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DISBURSEMENT_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.JR_FORM_FILLING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_DISBURSEMENTS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TOTAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TRAVEL_COSTS;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.WAITING_COSTS;

@Component
public class ClaimMapperHelper {

    @Named("mapTotalAmount")
    public ClaimField mapTotalAmount(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getTotalAmount() : null;
        var submitted = claimResponse.getTotalValue();
        return new ClaimField(TOTAL, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated));
    }

    @Named("mapFixedFee")
    public ClaimField mapFixedFee(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getFixedFeeAmount() : null;
        return new ClaimField(FIXED_FEE, ClaimFieldValue.of(null), ClaimFieldValue.of(calculated), ClaimFieldValue.of(null), null);
    }

    @Named("mapNetProfitCost")
    public ClaimField mapNetProfitCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetProfitCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetProfitCostsAmount() : null;
        return new ClaimField(NET_PROFIT_COST, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated), Cost.PROFIT_COSTS);
    }

    @Named("mapVatClaimed")
    public ClaimField mapVatClaimed(ClaimResponse claimResponse) {
        var submitted = claimResponse.getIsVatApplicable();
        var calculated = claimResponse.getFeeCalculationResponse() != null
                && Boolean.TRUE.equals(claimResponse.getFeeCalculationResponse().getVatIndicator());
        return new ClaimField(VAT, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated));
    }

    @Named("mapNetDisbursementAmount")
    public ClaimField mapNetDisbursementAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetDisbursementAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDisbursementAmount() : null;
        return new ClaimField(NET_DISBURSEMENTS_COST, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated), Cost.DISBURSEMENTS);
    }

    @Named("mapDisbursementVatAmount")
    public ClaimField mapDisbursementVatAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDisbursementsVatAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDisbursementVatAmount() : null;
        return new ClaimField(DISBURSEMENT_VAT, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated), Cost.DISBURSEMENTS_VAT);
    }

    @Named("mapCounselsCost")
    public ClaimField mapCounselsCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetCounselCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetCostOfCounselAmount() : null;
        return new ClaimField(COUNSELS_COST, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated), Cost.COUNSEL_COSTS);
    }

    @Named("mapDetentionTravelWaitingCosts")
    public ClaimField mapDetentionTravelWaitingCosts(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDetentionTravelWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDetentionTravelAndWaitingCostsAmount() : null;
        return new ClaimField(DETENTION_TRAVEL_COST, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated), Cost.DETENTION_TRAVEL_AND_WAITING_COSTS);
    }

    @Named("mapJrFormFillingCost")
    public ClaimField mapJrFormFillingCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getJrFormFillingAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getJrFormFillingAmount() : null;
        return new ClaimField(JR_FORM_FILLING, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated), Cost.JR_FORM_FILLING_COSTS);
    }

    @Named("mapAdjournedHearingFee")
    public ClaimField mapAdjournedHearingFee(ClaimResponse claimResponse) {
        var submitted = claimResponse.getAdjournedHearingFeeAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnAdjournedHearingFee() : null;
        return new ClaimField(ADJOURNED_FEE, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated), (Cost) null);
    }

    @Named("mapCmrhTelephone")
    public ClaimField mapCmrhTelephone(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhTelephoneCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhTelephoneFee() : null;
        return new ClaimField(CMRH_TELEPHONE, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated));
    }

    @Named("mapCmrhOral")
    public ClaimField mapCmrhOral(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhOralCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhOralFee() : null;
        return new ClaimField(CMRH_ORAL, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated));
    }

    @Named("mapHoInterview")
    public ClaimField mapHoInterview(ClaimResponse claimResponse) {
        var submitted = claimResponse.getHoInterview();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnHomeOfficeInterviewFee() : null;
        return new ClaimField(HO_INTERVIEW, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated));
    }

    @Named("mapSubstantiveHearing")
    public ClaimField mapSubstantiveHearing(ClaimResponse claimResponse) {
        var submitted = claimResponse.getHoInterview();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnHomeOfficeInterviewFee() : null;
        return new ClaimField(SUBSTANTIVE_HEARING, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated), (Cost) null);
    }

    @Named("mapTravelCosts")
    public ClaimField mapTravelCosts(ClaimResponse claimResponse) {
        var submitted = claimResponse.getTravelWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetTravelCostsAmount() : null;
        return new ClaimField(TRAVEL_COSTS, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated), Cost.TRAVEL_COSTS);
    }

    @Named("mapWaitingCosts")
    public ClaimField mapWaitingCosts(ClaimResponse claimResponse) {
        var submitted = claimResponse.getNetWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetWaitingCostsAmount() : null;
        return new ClaimField(WAITING_COSTS, ClaimFieldValue.of(submitted), ClaimFieldValue.of(calculated), Cost.WAITING_COSTS);
    }
}
