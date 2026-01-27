package uk.gov.justice.laa.amend.claim.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentGet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentOutcome;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

import java.math.BigDecimal;
import java.util.function.Function;

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
    @Mapping(target = "assessedTotalVat", expression = "java(mapAssessedTotalVat(claim))")
    @Mapping(target = "assessedTotalInclVat", expression = "java(mapAssessedTotalInclVat(claim))")
    @Mapping(target = "allowedTotalVat", expression = "java(mapAllowedTotalVat(claim))")
    @Mapping(target = "allowedTotalInclVat", expression = "java(mapAllowedTotalInclVat(claim))")
    AssessmentPost mapClaimToAssessment(ClaimDetails claim, @Context String userId);

    @InheritConfiguration(name = "mapClaimToAssessment")
    @Mapping(target = "netCostOfCounselAmount", expression = "java(mapNetCostOfCounselAmount(claim))")
    @Mapping(target = "detentionTravelAndWaitingCostsAmount", expression = "java(mapDetentionTravelAndWaitingCostsAmount(claim))")
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


    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "assessmentOutcome", source = "lastAssessmentOutcome")
    @Mapping(target = "vatClaimed", ignore = true)
    @Mapping(target = "fixedFee", ignore = true)
    @Mapping(target = "netDisbursementAmount", ignore = true)
    @Mapping(target = "disbursementVatAmount", ignore = true)
    @Mapping(target = "netProfitCost", ignore = true)
    @Mapping(target = "assessedTotalVat", ignore = true)
    @Mapping(target = "assessedTotalInclVat", ignore = true)
    @Mapping(target = "allowedTotalVat", ignore = true)
    @Mapping(target = "allowedTotalInclVat", ignore = true)
    ClaimDetails mapToClaim(AssessmentInfo assessmentInfo, @MappingTarget ClaimDetails claimDetails);

    @InheritConfiguration(name = "mapToClaim")
    @Mapping(target = "hoInterview", ignore = true)
    @Mapping(target = "cmrhOral", ignore = true)
    @Mapping(target = "cmrhTelephone", ignore = true)
    @Mapping(target = "jrFormFillingCost", ignore = true)
    @Mapping(target = "detentionTravelWaitingCosts", ignore = true)
    @Mapping(target = "adjournedHearing", ignore = true)
    @Mapping(target = "substantiveHearing", ignore = true)
    @Mapping(target = "counselsCost", ignore = true)
    CivilClaimDetails mapToCivilClaim(AssessmentInfo assessmentInfo, @MappingTarget CivilClaimDetails claimDetails);

    @InheritConfiguration(name = "mapToClaim")
    @Mapping(target = "travelCosts", ignore = true)
    @Mapping(target = "waitingCosts", ignore = true)
    CrimeClaimDetails mapToCrimeClaim(AssessmentInfo assessmentInfo, @MappingTarget CrimeClaimDetails claimDetails);

    @AfterMapping
    default void mapToClaimAfterMapping(
        AssessmentInfo source,
        @MappingTarget ClaimDetails target
    ) {
        map(source, target.getVatClaimed(), AssessmentInfo::getIsVatApplicable);
        map(source, target.getFixedFee(), AssessmentInfo::getFixedFeeAmount);
        map(source, target.getNetDisbursementAmount(), AssessmentInfo::getDisbursementAmount);
        map(source, target.getDisbursementVatAmount(), AssessmentInfo::getDisbursementVatAmount);
        map(source, target.getNetProfitCost(), AssessmentInfo::getNetProfitCostsAmount);
        map(source, target.getAssessedTotalVat(), AssessmentInfo::getAssessedTotalVat);
        map(source, target.getAssessedTotalInclVat(), AssessmentInfo::getAssessedTotalInclVat);
        map(source, target.getAllowedTotalVat(), AssessmentInfo::getAllowedTotalVat);
        map(source, target.getAllowedTotalInclVat(), AssessmentInfo::getAllowedTotalInclVat);
    }

    @AfterMapping
    default void mapToCivilClaimAfterMapping(
        AssessmentInfo source,
        @MappingTarget CivilClaimDetails target
    ) {
        map(source, target.getJrFormFillingCost(), AssessmentInfo::getJrFormFillingAmount);
        map(source, target.getDetentionTravelWaitingCosts(), AssessmentInfo::getDetentionTravelAndWaitingCostsAmount);
        map(source, target.getCounselsCost(), AssessmentInfo::getNetCostOfCounselAmount);
        map(source, target.getHoInterview(), AssessmentInfo::getBoltOnHomeOfficeInterviewFee);
        map(source, target.getCmrhOral(), AssessmentInfo::getBoltOnCmrhOralFee);
        map(source, target.getCmrhTelephone(), AssessmentInfo::getBoltOnCmrhTelephoneFee);
        map(source, target.getAdjournedHearing(), AssessmentInfo::getBoltOnAdjournedHearingFee);
        map(source, target.getSubstantiveHearing(), AssessmentInfo::getBoltOnSubstantiveHearingFee);
    }

    @AfterMapping
    default void mapToCrimeClaimAfterMapping(
        AssessmentInfo source,
        @MappingTarget CrimeClaimDetails target
    ) {
        map(source, target.getTravelCosts(), AssessmentInfo::getNetTravelCostsAmount);
        map(source, target.getWaitingCosts(), AssessmentInfo::getNetWaitingCostsAmount);
    }

    private void map(AssessmentInfo source, ClaimField target, Function<AssessmentInfo, Object> f) {
        if (source == null || target == null) {
            return;
        }
        target.setAssessed(f.apply(source));
    }

    /**
     * Maps AssessmentGet response object into AssessmentInfo
     */
    @Named("toAssessmentInfo")
    @Mapping(target = "lastAssessedBy", source = "createdByUserId")
    @Mapping(target = "lastAssessmentDate", source = "createdOn")
    @Mapping(target = "lastAssessmentOutcome", source = "assessmentOutcome", qualifiedByName = "mapToOutcome")
    AssessmentInfo mapAssessment(AssessmentGet source);

    /**
     * Updates ClaimDetails with the last assessment response object as AssessmentInfo object.
     */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "assessmentOutcome", source = "assessmentOutcome", qualifiedByName = "mapToOutcome")
    @Mapping(target = "lastAssessment", source = "assessmentGet", qualifiedByName = "toAssessmentInfo")
    ClaimDetails updateClaim(AssessmentGet assessmentGet, @MappingTarget ClaimDetails claimDetails);


    /**
     * Maps existing lastAssessment Details into the ClaimDetails object as assessed values.
     */
    default ClaimDetails mapAssessmentToClaimDetails(@MappingTarget ClaimDetails claimDetails) {
        if (claimDetails == null || claimDetails.getLastAssessment() == null) {
            throw new IllegalArgumentException("AssessmentGet and ClaimDetails must be non-null");
        }
        return switch (claimDetails) {
            case CrimeClaimDetails crime -> mapToCrimeClaim(claimDetails.getLastAssessment(), crime);
            case CivilClaimDetails civil -> mapToCivilClaim(claimDetails.getLastAssessment(), civil);
            default -> throw new IllegalArgumentException("Unsupported Claim details");
        };
    }

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

    @Named("mapToOutcome")
    default OutcomeType mapToOutcome(AssessmentOutcome outcome) {
        if (outcome == null) {
            return null;
        }
        return switch (outcome) {
            case PAID_IN_FULL           -> OutcomeType.PAID_IN_FULL;
            case REDUCED_STILL_ESCAPED  -> OutcomeType.REDUCED;
            case REDUCED_TO_FIXED_FEE   -> OutcomeType.REDUCED_TO_FIXED_FEE;
            case NILLED                 -> OutcomeType.NILLED;
        };
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

    /**
     *
     * @param claim the claim being mapped to an Assessment
     * @return the assessed total VAT, unless this is not modifiable in the UI (i.e. null) in which case we return the allowed total VAT
     */
    default BigDecimal mapAssessedTotalVat(ClaimDetails claim) {
        BigDecimal assessedValue = mapToBigDecimal(claim.getAssessedTotalVat());
        return assessedValue != null ? assessedValue : mapToBigDecimal(claim.getAllowedTotalVat());
    }

    /**
     *
     * @param claim the claim being mapped to an Assessment
     * @return the assessed total (including VAT), unless this is not modifiable in the UI (i.e. null) in which case we return the allowed total (including VAT)
     */
    default BigDecimal mapAssessedTotalInclVat(ClaimDetails claim) {
        BigDecimal assessedValue = mapToBigDecimal(claim.getAssessedTotalInclVat());
        return assessedValue != null ? assessedValue : mapToBigDecimal(claim.getAllowedTotalInclVat());
    }

    default BigDecimal mapAllowedTotalVat(ClaimDetails claim) {
        return mapToBigDecimal(claim.getAllowedTotalVat());
    }

    default BigDecimal mapAllowedTotalInclVat(ClaimDetails claim) {
        return mapToBigDecimal(claim.getAllowedTotalInclVat());
    }

    private BigDecimal mapToBigDecimal(ClaimField field) {
        if (field != null) {
            return mapToBigDecimal(field.getAssessed());
        }
        return null;
    }

    private BigDecimal mapToBigDecimal(Object amended) {
        if (amended instanceof BigDecimal value) {
            return value;
        }
        return null;
    }
}