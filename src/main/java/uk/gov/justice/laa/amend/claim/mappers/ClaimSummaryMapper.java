package uk.gov.justice.laa.amend.claim.mappers;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimSummary;
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

@Mapper(componentModel = "spring")
public interface ClaimSummaryMapper {

    // Base mapping
    @Mapping(target = "vatClaimed", expression = "java(mapVatClaimed(claimResponse))")
    @Mapping(target = "fixedFee", expression = "java(mapFixedFee(claimResponse))")
    @Mapping(target = "netProfitCost", expression = "java(mapNetProfitCost(claimResponse))")
    @Mapping(target = "netDisbursementAmount", expression = "java(mapNetDisbursementAmount(claimResponse))")
    @Mapping(target = "totalAmount", expression = "java(mapTotalAmount(claimResponse))")
    @Mapping(target = "disbursementVatAmount", expression = "java(mapDisbursementVatAmount(claimResponse))")
    @Mapping(target = "uniqueFileNumber", source = "uniqueFileNumber")
    @Mapping(target = "caseReferenceNumber", source = "caseReferenceNumber")
    @Mapping(target = "clientSurname", source = "clientSurname")
    @Mapping(target = "clientForename", source = "clientForename")
    @Mapping(target = "caseStartDate", source = "caseStartDate")
    @Mapping(target = "caseEndDate", source = "caseConcludedDate")
    @Mapping(target = "feeScheme", source = "feeCalculationResponse.feeCodeDescription")
    // TODO use feeSchemeCodeDescription when available
    @Mapping(target = "categoryOfLaw", source = "feeCalculationResponse.categoryOfLaw")
    // TODO use categoryOfLawDescription when available
    @Mapping(target = "submittedDate", constant = "TODO")
    @Mapping(target = "escaped", source = "feeCalculationResponse.boltOnDetails.escapeCaseFlag")
    @Mapping(target = "providerAccountNumber", constant = "TODO")
    @Mapping(target = "providerName", constant = "TODO")
    // TODO use providerAccountNumber when available
    @Mapping(target = "submissionId", source = "submissionId")
    @Mapping(target = "claimId", source = "id")
    @Mapping(target = "vatApplicable", source = "isVatApplicable")
    ClaimSummary mapBaseFields(ClaimResponse claimResponse);

    // Civil-specific mapping
    @InheritConfiguration(name = "mapBaseFields")
    @Mapping(target = "detentionTravelWaitingCosts", expression = "java(mapDetentionTravelWaitingCosts(claimResponse))")
    @Mapping(target = "jrFormFillingCost", expression = "java(mapJrFormFillingCost(claimResponse))")
    @Mapping(target = "adjournedHearing", expression = "java(mapAdjournedHearingFee(claimResponse))")
    @Mapping(target = "cmrhTelephone", expression = "java(mapCmrhTelephone(claimResponse))")
    @Mapping(target = "cmrhOral", expression = "java(mapCmrhOral(claimResponse))")
    @Mapping(target = "hoInterview", expression = "java(mapHoInterview(claimResponse))")
    @Mapping(target = "substantiveHearing", expression = "java(mapSubstantiveHearing(claimResponse))")
    @Mapping(target = "counselsCost", expression = "java(mapCounselsCost(claimResponse))")
    @Mapping(target = "matterTypeCodeOne", expression = "java(mapMatterTypeCodeOne(claimResponse))")
    @Mapping(target = "matterTypeCodeTwo", expression = "java(mapMatterTypeCodeTwo(claimResponse))")
    CivilClaimSummary mapToCivilClaimSummary(ClaimResponse claimResponse);

    // Crime-specific mapping
    @InheritConfiguration(name = "mapBaseFields")
    @Mapping(target = "matterTypeCode", source = "crimeMatterTypeCode")
    @Mapping(target = "travelCosts", expression = "java(mapTravelCosts(claimResponse))")
    @Mapping(target = "waitingCosts", expression = "java(mapWaitingCosts(claimResponse))")
    CrimeClaimSummary mapToCrimeClaimSummary(ClaimResponse claimResponse);

    default String mapMatterTypeCodeOne(ClaimResponse claimResponse) {
        if (StringUtils.isNotEmpty(claimResponse.getMatterTypeCode())) {
            var matterType = claimResponse.getMatterTypeCode().split("[+:]");
            return matterType.length > 0 ? matterType[0] : null;
        }
        return null;
    }

    default String mapMatterTypeCodeTwo(ClaimResponse claimResponse) {
        if (StringUtils.isNotEmpty(claimResponse.getMatterTypeCode())) {
            var matterType = claimResponse.getMatterTypeCode().split("[+:]");
            return matterType.length > 1 ? matterType[1] : null;
        }
        return null;
    }

    default ClaimFieldRow mapTotalAmount(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getTotalAmount() : null;
        var submitted = claimResponse.getTotalValue();
        return new ClaimFieldRow(TOTAL, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapFixedFee(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getFixedFeeAmount() : null;
        return new ClaimFieldRow(FIXED_FEE, "NA", calculated, null);
    }

    default ClaimFieldRow mapNetProfitCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetProfitCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetProfitCostsAmount() : null;
        return new ClaimFieldRow(NET_PROFIT_COST, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapVatClaimed(ClaimResponse claimResponse) {
        var submitted = claimResponse.getIsVatApplicable();
        var calculated = claimResponse.getFeeCalculationResponse() != null
                && Boolean.TRUE.equals(claimResponse.getFeeCalculationResponse().getVatIndicator());
        return new ClaimFieldRow(VAT, submitted, calculated, submitted);
    }


    default ClaimFieldRow mapNetDisbursementAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetDisbursementAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDisbursementVatAmount() : null;
        return new ClaimFieldRow(NET_DISBURSEMENTS_COST, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapDisbursementVatAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDisbursementsVatAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDisbursementVatAmount() : null;
        return new ClaimFieldRow(DISBURSEMENT_VAT, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapCounselsCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetCounselCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetCostOfCounselAmount() : null;
        return new ClaimFieldRow(COUNSELS_COST, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapDetentionTravelWaitingCosts(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDetentionTravelWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDetentionAndWaitingCostsAmount() : null;
        return new ClaimFieldRow(DETENTION_TRAVEL_COST, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapJrFormFillingCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getJrFormFillingAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getJrFormFillingAmount() : null;
        return new ClaimFieldRow(JR_FORM_FILLING, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapAdjournedHearingFee(ClaimResponse claimResponse) {
        var submitted = claimResponse.getAdjournedHearingFeeAmount() != null;
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnAdjournedHearingFee() : null;
        return new ClaimFieldRow(ADJOURNED_FEE, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapCmrhTelephone(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhTelephoneCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhTelephoneFee() : null;
        return new ClaimFieldRow(CMRH_TELEPHONE, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapCmrhOral(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhOralCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhOralFee() : null;
        return new ClaimFieldRow(CMRH_ORAL, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapHoInterview(ClaimResponse claimResponse) {
        var submitted = claimResponse.getHoInterview();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnHomeOfficeInterviewFee() : null;
        return new ClaimFieldRow(HO_INTERVIEW, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapSubstantiveHearing(ClaimResponse claimResponse) {
        var submitted = claimResponse.getHoInterview();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnHomeOfficeInterviewFee() : null;
        return new ClaimFieldRow(SUBSTANTIVE_HEARING, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapTravelCosts(ClaimResponse claimResponse) {
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetTravelCostsAmount() : null;
        var submitted = claimResponse.getTravelWaitingCostsAmount() != null
                ? claimResponse.getTravelWaitingCostsAmount() : null;
        return new ClaimFieldRow(TRAVEL_COSTS, submitted, calculated, submitted);
    }

    default ClaimFieldRow mapWaitingCosts(ClaimResponse claimResponse) {
        var submitted = claimResponse.getNetWaitingCostsAmount() != null
                ? claimResponse.getNetWaitingCostsAmount() : null;
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetWaitingCostsAmount() : null;
        return new ClaimFieldRow(WAITING_COSTS, submitted, calculated, submitted);
    }
}