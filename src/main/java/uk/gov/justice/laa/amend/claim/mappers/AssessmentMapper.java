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
    @Mapping(target = "travelWaitingCostsAmount", ignore = true)
    @Mapping(target = "travelAndWaitingCostsAmount", ignore = true)
    @Mapping(target = "isVatApplicable", source = "vatApplicable")
    @Mapping(target = "adjournedHearingFeeAmount", ignore = true)
    @Mapping(target = "jrFormFillingAmount", ignore = true)
    @Mapping(target = "cmrhOralCount", ignore = true)
    @Mapping(target = "cmrhTelephoneCount", ignore = true)
    @Mapping(target = "isSubstantiveHearing", ignore = true)
    @Mapping(target = "hoInterview", ignore = true)
    @Mapping(target = "createdByUserId", expression = "java(userId)")
    AssessmentPost mapClaimToAssessment(ClaimDetails claim, @Context String userId);

    @InheritConfiguration(name = "mapClaimToAssessment")
    @Mapping(target = "netCostOfCounselAmount", expression = "java(mapNetCostOfCounselAmount(claim))")
    @Mapping(target = "travelWaitingCostsAmount", ignore = true) // TODO
    @Mapping(target = "travelAndWaitingCostsAmount", ignore = true) // TODO
    @Mapping(target = "adjournedHearingFeeAmount", expression = "java(mapAdjournedHearingFeeAmount(claim))")
    @Mapping(target = "jrFormFillingAmount", expression = "java(mapJrFormFillingAmount(claim))")
    @Mapping(target = "cmrhOralCount", expression = "java(mapCmrhOralCount(claim))")
    @Mapping(target = "cmrhTelephoneCount", expression = "java(mapCmrhTelephoneCount(claim))")
    @Mapping(target = "isSubstantiveHearing", ignore = true) // TODO
    @Mapping(target = "hoInterview", expression = "java(mapHoInterview(claim))")
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

    default Integer mapAdjournedHearingFeeAmount(CivilClaimDetails claim) {
        return mapToInteger(claim.getAdjournedHearing());
    }

    default BigDecimal mapJrFormFillingAmount(CivilClaimDetails claim) {
        return mapToBigDecimal(claim.getJrFormFillingCost());
    }

    default Integer mapCmrhOralCount(CivilClaimDetails claim) {
        return mapToInteger(claim.getCmrhOral());
    }

    default Integer mapCmrhTelephoneCount(CivilClaimDetails claim) {
        return mapToInteger(claim.getCmrhTelephone());
    }

    default Integer mapHoInterview(CivilClaimDetails claim) {
        return mapToInteger(claim.getHoInterview());
    }

    default BigDecimal mapNetTravelCostsAmount(CrimeClaimDetails claim) {
        return mapToBigDecimal(claim.getTravelCosts());
    }

    default BigDecimal mapNetWaitingCostsAmount(CrimeClaimDetails claim) {
        return mapToBigDecimal(claim.getWaitingCosts());
    }

    private BigDecimal mapToBigDecimal(ClaimField field) {
        if (field != null && field.getAmended() instanceof BigDecimal value) {
            return value;
        }
        return null;
    }

    private Integer mapToInteger(ClaimField field) {
        if (field != null && field.getAmended() instanceof Integer value) {
            return value;
        }
        return null;
    }
}