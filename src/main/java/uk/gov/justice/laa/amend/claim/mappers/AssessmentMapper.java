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
    @Mapping(target = "createdByUserId", expression="java(userId)")
    AssessmentPost mapClaimToAssessment(ClaimDetails claim, @Context String userId);

    @InheritConfiguration(name = "mapClaimToAssessment")
    @Mapping(target = "netCostOfCounselAmount", expression = "java(mapNetCostOfCounselAmount(claim))")
    @Mapping(target = "travelWaitingCostsAmount", ignore = true)
    @Mapping(target = "travelAndWaitingCostsAmount", ignore = true)
    @Mapping(target = "adjournedHearingFeeAmount", ignore = true)
    @Mapping(target = "jrFormFillingAmount", ignore = true)
    @Mapping(target = "cmrhOralCount", ignore = true)
    @Mapping(target = "cmrhTelephoneCount", ignore = true)
    @Mapping(target = "isSubstantiveHearing", ignore = true)
    @Mapping(target = "hoInterview", ignore = true)
    AssessmentPost mapCivilClaimToAssessment(CivilClaimDetails claim, @Context String userId);

    @InheritConfiguration(name = "mapClaimToAssessment")
    @Mapping(target = "netTravelCostsAmount", ignore = true)
    @Mapping(target = "netWaitingCostsAmount", ignore = true)
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
        return claim != null ? mapToBigDecimal(claim.getFixedFee()) : null;
    }

    default BigDecimal mapNetProfitCostsAmount(ClaimDetails claim) {
        return claim != null ? mapToBigDecimal(claim.getNetProfitCost()) : null;
    }

    default BigDecimal mapDisbursementAmount(ClaimDetails claim) {
        return claim != null ? mapToBigDecimal(claim.getNetDisbursementAmount()) : null;
    }

    default BigDecimal mapDisbursementVatAmount(ClaimDetails claim) {
        return claim != null ? mapToBigDecimal(claim.getDisbursementVatAmount()) : null;
    }

    default BigDecimal mapNetCostOfCounselAmount(CivilClaimDetails claim) {
        return claim != null ? mapToBigDecimal(claim.getCounselsCost()) : null;
    }

    private BigDecimal mapToBigDecimal(ClaimField field) {
        if (field != null && field.getAmended() instanceof BigDecimal value) {
            return value;
        }
        return null;
    }
}