package uk.gov.justice.laa.amend.claim.mappers;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.amend.claim.models.CivilClaim;
import uk.gov.justice.laa.amend.claim.models.Claim2;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaim;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

import java.math.BigDecimal;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.*;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

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
    @Mapping(target = "assessmentOutcome", ignore = true)
    Claim2 mapBaseFields(ClaimResponse claimResponse);

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
    CivilClaim mapToCivilClaim(ClaimResponse claimResponse);

    // Crime-specific mapping
    @InheritConfiguration(name = "mapBaseFields")
    @Mapping(target = "matterTypeCode", source = "crimeMatterTypeCode")
    @Mapping(target = "travelCosts", expression = "java(mapTravelCosts(claimResponse))")
    @Mapping(target = "waitingCosts", expression = "java(mapWaitingCosts(claimResponse))")
    CrimeClaim mapToCrimeClaim(ClaimResponse claimResponse);

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

    default ClaimField mapTotalAmount(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getTotalAmount() : null;
        var submitted = claimResponse.getTotalValue();
        return new ClaimField(TOTAL, submitted, calculated, submitted);
    }

    default ClaimField mapFixedFee(ClaimResponse claimResponse) {
        var calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getFixedFeeAmount() : null;
        return new ClaimField(FIXED_FEE, "NA", calculated, "NA");
    }

    default ClaimField mapNetProfitCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetProfitCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetProfitCostsAmount() : null;
        return new ClaimField(NET_PROFIT_COST, submitted, calculated, submitted);
    }

    default ClaimField mapVatClaimed(ClaimResponse claimResponse) {
        var submitted = claimResponse.getIsVatApplicable();
        var calculated = claimResponse.getFeeCalculationResponse() != null
                && Boolean.TRUE.equals(claimResponse.getFeeCalculationResponse().getVatIndicator());
        return new ClaimField(VAT, submitted, calculated, submitted);
    }


    default ClaimField mapNetDisbursementAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetDisbursementAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDisbursementVatAmount() : null;
        return new ClaimField(NET_DISBURSEMENTS_COST, submitted, calculated, submitted);
    }

    default ClaimField mapDisbursementVatAmount(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDisbursementsVatAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDisbursementVatAmount() : null;
        return new ClaimField(DISBURSEMENT_VAT, submitted, calculated, submitted);
    }

    default ClaimField mapCounselsCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getNetCounselCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetCostOfCounselAmount() : null;
        return new ClaimField(COUNSELS_COST, submitted, calculated, submitted);
    }

    default ClaimField mapDetentionTravelWaitingCosts(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getDetentionTravelWaitingCostsAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getDetentionAndWaitingCostsAmount() : null;
        return new ClaimField(DETENTION_TRAVEL_COST, submitted, calculated, submitted);
    }

    default ClaimField mapJrFormFillingCost(ClaimResponse claimResponse) {
        BigDecimal submitted = claimResponse.getJrFormFillingAmount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getJrFormFillingAmount() : null;
        return new ClaimField(JR_FORM_FILLING, submitted, calculated, submitted);
    }

    default ClaimField mapAdjournedHearingFee(ClaimResponse claimResponse) {
        var submitted = claimResponse.getAdjournedHearingFeeAmount() != null;
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnAdjournedHearingFee() : null;
        return new ClaimField(ADJOURNED_FEE, submitted, calculated, submitted);
    }

    default ClaimField mapCmrhTelephone(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhTelephoneCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhTelephoneFee() : null;
        return new ClaimField(CMRH_TELEPHONE, submitted, calculated, submitted);
    }

    default ClaimField mapCmrhOral(ClaimResponse claimResponse) {
        var submitted = claimResponse.getCmrhOralCount();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnCmrhOralFee() : null;
        return new ClaimField(CMRH_ORAL, submitted, calculated, submitted);
    }

    default ClaimField mapHoInterview(ClaimResponse claimResponse) {
        var submitted = claimResponse.getHoInterview();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnHomeOfficeInterviewFee() : null;
        return new ClaimField(HO_INTERVIEW, submitted, calculated, submitted);
    }

    default ClaimField mapSubstantiveHearing(ClaimResponse claimResponse) {
        var submitted = claimResponse.getHoInterview();
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                && claimResponse.getFeeCalculationResponse().getBoltOnDetails() != null
                ? claimResponse.getFeeCalculationResponse().getBoltOnDetails().getBoltOnHomeOfficeInterviewFee() : null;
        return new ClaimField(SUBSTANTIVE_HEARING, submitted, calculated, submitted);
    }

    default ClaimField mapTravelCosts(ClaimResponse claimResponse) {
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetTravelCostsAmount() : null;
        var submitted = claimResponse.getTravelWaitingCostsAmount() != null
                ? claimResponse.getTravelWaitingCostsAmount() : null;
        return new ClaimField(TRAVEL_COSTS, submitted, calculated, submitted);
    }

    default ClaimField mapWaitingCosts(ClaimResponse claimResponse) {
        var submitted = claimResponse.getNetWaitingCostsAmount() != null
                ? claimResponse.getNetWaitingCostsAmount() : null;
        BigDecimal calculated = claimResponse.getFeeCalculationResponse() != null
                ? claimResponse.getFeeCalculationResponse().getNetWaitingCostsAmount() : null;
        return new ClaimField(WAITING_COSTS, submitted, calculated, submitted);
    }
}