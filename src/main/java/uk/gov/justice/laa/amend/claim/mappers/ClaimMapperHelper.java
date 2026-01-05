package uk.gov.justice.laa.amend.claim.mappers;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.math.BigDecimal;

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
import static uk.gov.justice.laa.amend.claim.utils.NumberUtils.add;

@Component
public class ClaimMapperHelper {

    public static final String ALLOWED_TOTALS_URL = "/submissions/%s/claims/%s/allowed-totals";
    public static final String ASSESSED_TOTALS_URL = "/submissions/%s/claims/%s/assessed-totals";

    @Named("mapTotalAmount")
    public ClaimField mapTotalAmount(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getTotalAmount() : null;
        var submitted = claimResponse.getTotalValue();
        return mapToClaimField(submitted, calculated, TOTAL);
    }

    @Named("mapFixedFee")
    public ClaimField mapFixedFee(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getFixedFeeAmount() : null;
        return mapToClaimField(null, calculated, FIXED_FEE);
    }

    @Named("mapNetProfitCost")
    public ClaimField mapNetProfitCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetProfitCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetProfitCostsAmount() : null;
        return mapToAssessableClaimField(submitted, calculated, NET_PROFIT_COST, Cost.PROFIT_COSTS.getChangeUrl());
    }


    private ClaimField mapToAssessableClaimField(Object submitted, Object calculated, String key, String changeUrl) {
        return ClaimField.builder()
                .key(key)
                .submitted(submitted)
                .calculated(calculated)
                .assessed(submitted)
                .changeUrl(changeUrl)
                .build();
    }


    private ClaimField mapToClaimField(Object submitted, Object calculated, String key) {
        return ClaimField.builder()
                .key(key)
                .submitted(submitted)
                .calculated(calculated)
                .assessed(submitted)
                .build();
    }

    @Named("mapVatClaimed")
    public ClaimField mapVatClaimed(ClaimResponse claimResponse) {
        var submitted = claimResponse.getIsVatApplicable();
        var calculated = claimResponse.getFeeCalculationResponse() != null
                && Boolean.TRUE.equals(claimResponse.getFeeCalculationResponse().getVatIndicator());
        return mapToAssessableClaimField(submitted, calculated, VAT, "/submissions/%s/claims/%s/assessment-outcome");
    }

    @Named("mapNetDisbursementAmount")
    public ClaimField mapNetDisbursementAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetDisbursementAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDisbursementAmount() : null;
        return mapToAssessableClaimField(submitted, calculated, NET_DISBURSEMENTS_COST, Cost.DISBURSEMENTS.getChangeUrl());
    }

    @Named("mapDisbursementVatAmount")
    public ClaimField mapDisbursementVatAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDisbursementsVatAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDisbursementVatAmount() : null;
        return mapToAssessableClaimField(submitted, calculated, DISBURSEMENT_VAT, Cost.DISBURSEMENTS_VAT.getChangeUrl());
    }

    @Named("mapCounselsCost")
    public ClaimField mapCounselsCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetCounselCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetCostOfCounselAmount() : null;
        return mapToAssessableClaimField(submitted, calculated, COUNSELS_COST, Cost.COUNSEL_COSTS.getChangeUrl());
    }

    @Named("mapDetentionTravelWaitingCosts")
    public ClaimField mapDetentionTravelWaitingCosts(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDetentionTravelWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDetentionTravelAndWaitingCostsAmount() : null;
        return mapToAssessableClaimField(submitted, calculated, DETENTION_TRAVEL_COST, Cost.DETENTION_TRAVEL_AND_WAITING_COSTS.getChangeUrl());
    }

    @Named("mapJrFormFillingCost")
    public ClaimField mapJrFormFillingCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getJrFormFillingAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getJrFormFillingAmount() : null;
        return mapToAssessableClaimField(submitted, calculated, JR_FORM_FILLING, Cost.JR_FORM_FILLING_COSTS.getChangeUrl());
    }

    @Named("mapAdjournedHearingFee")
    public ClaimField mapAdjournedHearingFee(ClaimResponse claimResponse) {
        var submitted = claimResponse.getAdjournedHearingFeeAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnAdjournedHearingFee() : null;
        return mapToClaimField(submitted, calculated, ADJOURNED_FEE);
    }

    @Named("mapCmrhTelephone")
    public ClaimField mapCmrhTelephone(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhTelephoneCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhTelephoneFee() : null;
        return mapToClaimField(submitted, calculated, CMRH_TELEPHONE);
    }

    @Named("mapCmrhOral")
    public ClaimField mapCmrhOral(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhOralCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhOralFee() : null;
        return mapToClaimField(submitted, calculated, CMRH_ORAL);
    }

    @Named("mapHoInterview")
    public ClaimField mapHoInterview(ClaimResponse claimResponse) {
        var submitted = claimResponse.getHoInterview();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnHomeOfficeInterviewFee() : null;
        return mapToClaimField(submitted, calculated, HO_INTERVIEW);
    }

    @Named("mapSubstantiveHearing")
    public ClaimField mapSubstantiveHearing(ClaimResponse claimResponse) {
        var submitted = claimResponse.getIsSubstantiveHearing();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnHomeOfficeInterviewFee() : null;
        return mapToClaimField(submitted, calculated, SUBSTANTIVE_HEARING);
    }

    @Named("mapTravelCosts")
    public ClaimField mapTravelCosts(ClaimResponse claimResponse) {
        var submitted = claimResponse.getTravelWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetTravelCostsAmount() : null;
        return mapToAssessableClaimField(submitted, calculated, TRAVEL_COSTS, Cost.TRAVEL_COSTS.getChangeUrl());
    }

    @Named("mapWaitingCosts")
    public ClaimField mapWaitingCosts(ClaimResponse claimResponse) {
        var submitted = claimResponse.getNetWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetWaitingCostsAmount() : null;
        return mapToAssessableClaimField(submitted, calculated, WAITING_COSTS, Cost.WAITING_COSTS.getChangeUrl());
    }

    @Named("mapAssessedTotalVat")
    public ClaimField mapAssessedTotalVat() {
        ClaimField claimField = new ClaimField();
        claimField.setKey(ASSESSED_TOTAL_VAT);
        claimField.setChangeUrl(ASSESSED_TOTALS_URL);
        return claimField;
    }

    @Named("mapAssessedTotalInclVat")
    public ClaimField mapAssessedTotalInclVat() {
        ClaimField claimField = new ClaimField();
        claimField.setKey(ASSESSED_TOTAL_INCL_VAT);
        claimField.setChangeUrl(ASSESSED_TOTALS_URL);
        return claimField;
    }

    @Named("mapAllowedTotalVat")
    public ClaimField mapAllowedTotalVat(ClaimResponse claimResponse) {
        ClaimField claimField = new ClaimField();
        claimField.setKey(ALLOWED_TOTAL_VAT);
        claimField.setChangeUrl(ALLOWED_TOTALS_URL);
        FeeCalculationPatch fee = claimResponse.getFeeCalculationResponse();
        BigDecimal calculated = fee != null ? add(fee.getCalculatedVatAmount(), fee.getDisbursementVatAmount()) : null;
        claimField.setCalculated(calculated);
        return claimField;
    }

    @Named("mapAllowedTotalInclVat")
    public ClaimField mapAllowedTotalInclVat(ClaimResponse claimResponse) {
        ClaimField claimField = new ClaimField();
        claimField.setKey(ALLOWED_TOTAL_INCL_VAT);
        claimField.setChangeUrl(ALLOWED_TOTALS_URL);
        FeeCalculationPatch fee = claimResponse.getFeeCalculationResponse();
        BigDecimal calculated = fee != null ? fee.getTotalAmount() : null;
        claimField.setCalculated(calculated);
        return claimField;
    }
}
