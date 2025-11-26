package uk.gov.justice.laa.amend.claim.mappers;

import org.mapstruct.Context;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentOutcome;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface AssessmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assessmentOutcome", expression = "java(mapAssessmentOutcome(claim))")
    @Mapping(target = "fixedFeeAmount", expression = "java(mapFixedFeeAmount(claim))")
    @Mapping(target = "netTravelCostsAmount", ignore = true)
    @Mapping(target = "netWaitingCostsAmount", ignore = true)
    @Mapping(target = "netProfitCostsAmount", expression = "java(mapNetProfitCostsAmount(claim))")
    @Mapping(target = "disbursementAmount", expression = "java(mapDisbursementAmount(claim))")
    @Mapping(target = "disbursementVatAmount", expression = "java(mapDisbursementVatAmount(claim))")
    @Mapping(target = "netCostOfCounselAmount", ignore = true)
    @Mapping(target = "detentionTravelAndWaitingCostsAmount", ignore = true)
    @Mapping(target = "isVatApplicable", source = "vatApplicable")
    @Mapping(target = "boltOnAdjournedHearingFee", ignore = true)
    @Mapping(target = "jrFormFillingAmount", ignore = true)
    @Mapping(target = "boltOnCmrhOralFee", ignore = true)
    @Mapping(target = "boltOnCmrhTelephoneFee", ignore = true)
    @Mapping(target = "boltOnSubstantiveHearingFee", ignore = true)
    @Mapping(target = "boltOnHomeOfficeInterviewFee", ignore = true)
    @Mapping(target = "createdByUserId", expression = "java(userId)")
    AssessmentPost mapClaimToAssessment(ClaimDetails claim, @Context String userId);

    @InheritConfiguration(name = "mapClaimToAssessment")
    @Mapping(target = "netCostOfCounselAmount", expression = "java(mapNetCostOfCounselAmount(claim))")
    @Mapping(target = "detentionTravelAndWaitingCostsAmount", expression = "java(mapDetentionTravelAndWaitingCostsAmount(claim))") // TODO
    @Mapping(target = "boltOnAdjournedHearingFee", expression = "java(mapBoltOnAdjournedHearingFee(claim))")
    @Mapping(target = "jrFormFillingAmount", expression = "java(mapJrFormFillingAmount(claim))")
    @Mapping(target = "boltOnCmrhOralFee", expression = "java(mapBoltOnCmrhOralFee(claim))")
    @Mapping(target = "boltOnCmrhTelephoneFee", expression = "java(mapBoltOnCmrhTelephoneFee(claim))")
    @Mapping(target = "boltOnSubstantiveHearingFee", expression = "java(mapBoltOnSubstantiveHearingFee(claim))")
    @Mapping(target = "boltOnHomeOfficeInterviewFee", expression = "java(mapBoltOnHomeOfficeInterviewFee(claim))")
    AssessmentPost mapCivilClaimToAssessment(CivilClaimDetails claim, @Context String userId);

    @InheritConfiguration(name = "mapClaimToAssessment")
    @Mapping(target = "netTravelCostsAmount", expression = "java(mapNetTravelCostsAmount(claim))")
    @Mapping(target = "netWaitingCostsAmount", expression = "java(mapNetWaitingCostsAmount(claim))")
    AssessmentPost mapCrimeClaimToAssessment(CrimeClaimDetails claim, @Context String userId);

    default AssessmentOutcome mapAssessmentOutcome(ClaimDetails claim) {
        if (claim.getAssessmentOutcome() != null) {
            return switch (claim.getAssessmentOutcome()) {
                case PAID_IN_FULL -> AssessmentOutcome.PAID_IN_FULL;
                case REDUCED -> AssessmentOutcome.REDUCED_STILL_ESCAPED;
                case REDUCED_TO_FIXED_FEE -> AssessmentOutcome.REDUCED_TO_FIXED_FEE;
                case NILLED -> AssessmentOutcome.NILLED;
            };
        }
        return null;
    }

    default BigDecimal mapFixedFeeAmount(ClaimDetails claim) {
        return mapToBigDecimal(claim.getFixedFee());
    }

    default BigDecimal mapNetProfitCostsAmount(ClaimDetails claim) {
        return mapToBigDecimal(claim.getNetProfitCost());
    }

    default BigDecimal mapDisbursementAmount(ClaimDetails claim) {
        return mapToBigDecimal(claim.getNetDisbursementAmount());
    }

    default BigDecimal mapDisbursementVatAmount(ClaimDetails claim) {
        return mapToBigDecimal(claim.getDisbursementVatAmount());
    }

    default BigDecimal mapNetCostOfCounselAmount(CivilClaimDetails claim) {
        return mapToBigDecimal(claim.getCounselsCost());
    }

    default BigDecimal mapBoltOnAdjournedHearingFee(CivilClaimDetails claim) {
        return mapToBigDecimal(claim.getAdjournedHearing());
    }

    default BigDecimal mapJrFormFillingAmount(CivilClaimDetails claim) {
        return mapToBigDecimal(claim.getJrFormFillingCost());
    }

    default BigDecimal mapBoltOnCmrhOralFee(CivilClaimDetails claim) {
        return mapToBigDecimal(claim.getCmrhOral());
    }

    default BigDecimal mapBoltOnCmrhTelephoneFee(CivilClaimDetails claim) {
        return mapToBigDecimal(claim.getCmrhTelephone());
    }

    default BigDecimal mapBoltOnHomeOfficeInterviewFee(CivilClaimDetails claim) {
        return mapToBigDecimal(claim.getHoInterview());
    }

    default BigDecimal mapNetTravelCostsAmount(CrimeClaimDetails claim) {
        return mapToBigDecimal(claim.getTravelCosts());
    }

    default BigDecimal mapNetWaitingCostsAmount(CrimeClaimDetails claim) {
        return mapToBigDecimal(claim.getWaitingCosts());
    }

    default BigDecimal mapDetentionTravelAndWaitingCostsAmount(CivilClaimDetails claim) {
        return mapToBigDecimal(claim.getDetentionTravelWaitingCosts());
    }

    default BigDecimal mapBoltOnSubstantiveHearingFee(CivilClaimDetails claim) {
        return mapToBigDecimal(claim.getSubstantiveHearing());
    }

    private BigDecimal mapToBigDecimal(ClaimField field) {
        if (field != null && field.getAmended() instanceof BigDecimal value) {
            return value;
        }
        return null;
    }
}