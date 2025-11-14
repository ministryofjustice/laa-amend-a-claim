package uk.gov.justice.laa.amend.claim.mappers;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimSummary;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;

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

@Mapper(componentModel = "spring", uses = {ClaimMapperHelper.class})
public interface ClaimSummaryMapper {
    // Base mapping
    @Mapping(target = "vatClaimed", source = "claimResponse", qualifiedByName = "mapVatClaimed")
    @Mapping(target = "fixedFee", source = "claimResponse", qualifiedByName = "mapFixedFee")
    @Mapping(target = "netProfitCost", source = "claimResponse", qualifiedByName = "mapNetProfitCost")
    @Mapping(target = "netDisbursementAmount", source = "claimResponse", qualifiedByName = "mapNetDisbursementAmount")
    @Mapping(target = "totalAmount", source = "claimResponse", qualifiedByName = "mapTotalAmount")
    @Mapping(target = "disbursementVatAmount", source = "claimResponse", qualifiedByName = "mapDisbursementVatAmount")
    @Mapping(target = "uniqueFileNumber", source = "uniqueFileNumber")
    @Mapping(target = "caseReferenceNumber", source = "caseReferenceNumber")
    @Mapping(target = "clientSurname", source = "clientSurname")
    @Mapping(target = "clientForename", source = "clientForename")
    @Mapping(target = "caseStartDate", source = "caseStartDate")
    @Mapping(target = "caseEndDate", source = "caseConcludedDate")
    @Mapping(target = "feeScheme", source = "feeCalculationResponse.feeCodeDescription")
    @Mapping(target = "categoryOfLaw", source = "feeCalculationResponse.categoryOfLaw")
    @Mapping(target = "escaped", source = "feeCalculationResponse.boltOnDetails.escapeCaseFlag")
    @Mapping(target = "providerName", constant = "TODO")
    @Mapping(target = "submissionId", source = "submissionId")
    @Mapping(target = "claimId", source = "id")
    @Mapping(target = "vatApplicable", source = "isVatApplicable")
    ClaimSummary mapBaseFields(ClaimResponse claimResponse);

    // Civil-specific mapping
    @InheritConfiguration(name = "mapBaseFields")
    @Mapping(target = "detentionTravelWaitingCosts", source = "claimResponse", qualifiedByName = "mapDetentionTravelWaitingCosts")
    @Mapping(target = "jrFormFillingCost", source = "claimResponse", qualifiedByName = "mapJrFormFillingCost")
    @Mapping(target = "adjournedHearing", source = "claimResponse", qualifiedByName = "mapAdjournedHearingFee")
    @Mapping(target = "cmrhTelephone", source = "claimResponse", qualifiedByName = "mapCmrhTelephone")
    @Mapping(target = "cmrhOral", source = "claimResponse", qualifiedByName = "mapCmrhOral")
    @Mapping(target = "hoInterview", source = "claimResponse", qualifiedByName = "mapHoInterview")
    @Mapping(target = "substantiveHearing", source = "claimResponse", qualifiedByName = "mapSubstantiveHearing")
    @Mapping(target = "counselsCost", source = "claimResponse", qualifiedByName = "mapCounselsCost")
    @Mapping(target = "matterTypeCodeOne", source = "claimResponse", qualifiedByName = "mapMatterTypeCodeOne")
    @Mapping(target = "matterTypeCodeTwo", source = "claimResponse", qualifiedByName = "mapMatterTypeCodeTwo")
    CivilClaimSummary mapToCivilClaimSummary(ClaimResponse claimResponse);

    // Crime-specific mapping
    @InheritConfiguration(name = "mapBaseFields")
    @Mapping(target = "matterTypeCode", source = "crimeMatterTypeCode")
    @Mapping(target = "travelCosts", source = "claimResponse", qualifiedByName = "mapTravelCosts")
    @Mapping(target = "waitingCosts", source = "claimResponse", qualifiedByName = "mapWaitingCosts")
    CrimeClaimSummary mapToCrimeClaimSummary(ClaimResponse claimResponse);


    @AfterMapping
    default void enrichWithSubmission(@MappingTarget ClaimSummary claimSummary, SubmissionResponse submissionResponse) {
        if (submissionResponse != null) {
            claimSummary.setSubmittedDate(submissionResponse.getSubmitted() != null ? submissionResponse.getSubmitted().toString() : null);
            claimSummary.setProviderAccountNumber(submissionResponse.getOfficeAccountNumber());
            claimSummary.setAreaOfLaw(submissionResponse.getAreaOfLaw());
        }
    }

    default ClaimSummary mapToClaimSummary(ClaimResponse claimResponse, SubmissionResponse submissionResponse) {
        if (claimResponse != null && submissionResponse != null) {
            ClaimSummary claimSummary = "CRIME".equals(submissionResponse.getAreaOfLaw())
                    ? mapToCrimeClaimSummary(claimResponse)
                    : mapToCivilClaimSummary(claimResponse);
            enrichWithSubmission(claimSummary, submissionResponse);
            claimSummary.setCrimeClaim("CRIME".equals(claimSummary.getAreaOfLaw()));
            return claimSummary;
        }
        throw new IllegalArgumentException("Both claimResponse and submissionResponse must be non-null");
    }
}