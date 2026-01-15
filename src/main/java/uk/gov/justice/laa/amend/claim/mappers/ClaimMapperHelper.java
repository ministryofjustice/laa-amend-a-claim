package uk.gov.justice.laa.amend.claim.mappers;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldType;
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

    @Named("mapTotalAmount")
    public ClaimField mapTotalAmount(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
            ? claimResponse.getFeeCalculationResponse().getTotalAmount() : null;
        var submitted = claimResponse.getTotalValue();
        return mapToClaimField(submitted, calculated, TOTAL, ClaimFieldType.TOTAL);
    }

    @Named("mapFixedFee")
    public ClaimField mapFixedFee(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
            ? claimResponse.getFeeCalculationResponse().getFixedFeeAmount() : null;
        return mapToClaimField(null, calculated, FIXED_FEE, ClaimFieldType.FIXED_FEE);
    }

    @Named("mapNetProfitCost")
    public ClaimField mapNetProfitCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetProfitCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            ? claimResponse.getFeeCalculationResponse().getNetProfitCostsAmount() : null;
        return mapToClaimField(submitted, calculated, NET_PROFIT_COST, ClaimFieldType.NORMAL);
    }

    private ClaimField mapToClaimField(Object submitted, Object calculated, String key, ClaimFieldType type) {
        return ClaimField.builder()
            .key(key)
            .submitted(submitted)
            .calculated(calculated)
            .assessed(submitted)
            .type(type)
            .build();
    }

    @Named("mapVatClaimed")
    public ClaimField mapVatClaimed(ClaimResponse claimResponse) {
        var submitted = claimResponse.getIsVatApplicable();
        var calculated = claimResponse.getFeeCalculationResponse() != null
            && Boolean.TRUE.equals(claimResponse.getFeeCalculationResponse().getVatIndicator());
        return mapToClaimField(submitted, calculated, VAT, ClaimFieldType.NORMAL);
    }

    @Named("mapNetDisbursementAmount")
    public ClaimField mapNetDisbursementAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetDisbursementAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            ? claimResponse.getFeeCalculationResponse().getDisbursementAmount() : null;
        return mapToClaimField(submitted, calculated, NET_DISBURSEMENTS_COST, ClaimFieldType.NORMAL);
    }

    @Named("mapDisbursementVatAmount")
    public ClaimField mapDisbursementVatAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDisbursementsVatAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            ? claimResponse.getFeeCalculationResponse().getDisbursementVatAmount() : null;
        return mapToClaimField(submitted, calculated, DISBURSEMENT_VAT, ClaimFieldType.NORMAL);
    }

    @Named("mapCounselsCost")
    public ClaimField mapCounselsCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetCounselCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            ? claimResponse.getFeeCalculationResponse().getNetCostOfCounselAmount() : null;
        return mapToClaimField(submitted, calculated, COUNSELS_COST, ClaimFieldType.NORMAL);
    }

    @Named("mapDetentionTravelWaitingCosts")
    public ClaimField mapDetentionTravelWaitingCosts(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDetentionTravelWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            ? claimResponse.getFeeCalculationResponse().getDetentionTravelAndWaitingCostsAmount() : null;
        return mapToClaimField(submitted, calculated, DETENTION_TRAVEL_COST, ClaimFieldType.NORMAL);
    }

    @Named("mapJrFormFillingCost")
    public ClaimField mapJrFormFillingCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getJrFormFillingAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            ? claimResponse.getFeeCalculationResponse().getJrFormFillingAmount() : null;
        return mapToClaimField(submitted, calculated, JR_FORM_FILLING, ClaimFieldType.NORMAL);
    }

    @Named("mapAdjournedHearingFee")
    public ClaimField mapAdjournedHearingFee(ClaimResponse claimResponse) {
        var submitted = claimResponse.getAdjournedHearingFeeAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
            ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnAdjournedHearingFee() : null;
        return mapToClaimField(submitted, calculated, ADJOURNED_FEE, ClaimFieldType.BOLT_ON);
    }

    @Named("mapCmrhTelephone")
    public ClaimField mapCmrhTelephone(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhTelephoneCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
            ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhTelephoneFee() : null;
        return mapToClaimField(submitted, calculated, CMRH_TELEPHONE, ClaimFieldType.BOLT_ON);
    }

    @Named("mapCmrhOral")
    public ClaimField mapCmrhOral(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhOralCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
            ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhOralFee() : null;
        return mapToClaimField(submitted, calculated, CMRH_ORAL, ClaimFieldType.BOLT_ON);
    }

    @Named("mapHoInterview")
    public ClaimField mapHoInterview(ClaimResponse claimResponse) {
        var submitted = claimResponse.getHoInterview();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
            ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnHomeOfficeInterviewFee() : null;
        return mapToClaimField(submitted, calculated, HO_INTERVIEW, ClaimFieldType.BOLT_ON);
    }

    @Named("mapSubstantiveHearing")
    public ClaimField mapSubstantiveHearing(ClaimResponse claimResponse) {
        var submitted = claimResponse.getIsSubstantiveHearing();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
            ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnHomeOfficeInterviewFee() : null;
        return mapToClaimField(submitted, calculated, SUBSTANTIVE_HEARING, ClaimFieldType.BOLT_ON);
    }

    @Named("mapTravelCosts")
    public ClaimField mapTravelCosts(ClaimResponse claimResponse) {
        var submitted = claimResponse.getTravelWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            ? claimResponse.getFeeCalculationResponse().getNetTravelCostsAmount() : null;
        return mapToClaimField(submitted, calculated, TRAVEL_COSTS, ClaimFieldType.NORMAL);
    }

    @Named("mapWaitingCosts")
    public ClaimField mapWaitingCosts(ClaimResponse claimResponse) {
        var submitted = claimResponse.getNetWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
            ? claimResponse.getFeeCalculationResponse().getNetWaitingCostsAmount() : null;
        return mapToClaimField(submitted, calculated, WAITING_COSTS, ClaimFieldType.NORMAL);
    }

    @Named("mapAssessedTotalVat")
    public ClaimField mapAssessedTotalVat() {
        return ClaimField.builder().key(ASSESSED_TOTAL_VAT).type(ClaimFieldType.ASSESSED).build();
    }

    @Named("mapAssessedTotalInclVat")
    public ClaimField mapAssessedTotalInclVat() {
        return ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).type(ClaimFieldType.ASSESSED).build();
    }

    @Named("mapAllowedTotalVat")
    public ClaimField mapAllowedTotalVat(ClaimResponse claimResponse) {
        ClaimField claimField = ClaimField.builder().key(ALLOWED_TOTAL_VAT).type(ClaimFieldType.ALLOWED).build();
        FeeCalculationPatch fee = claimResponse.getFeeCalculationResponse();
        BigDecimal calculated = fee != null ? add(fee.getCalculatedVatAmount(), fee.getDisbursementVatAmount()) : null;
        claimField.setCalculated(calculated);
        return claimField;
    }

    @Named("mapAllowedTotalInclVat")
    public ClaimField mapAllowedTotalInclVat(ClaimResponse claimResponse) {
        ClaimField claimField = ClaimField.builder().key(ALLOWED_TOTAL_INCL_VAT).type(ClaimFieldType.ALLOWED).build();
        FeeCalculationPatch fee = claimResponse.getFeeCalculationResponse();
        BigDecimal calculated = fee != null ? fee.getTotalAmount() : null;
        claimField.setCalculated(calculated);
        return claimField;
    }
}
