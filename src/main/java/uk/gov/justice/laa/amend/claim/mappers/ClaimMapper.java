package uk.gov.justice.laa.amend.claim.mappers;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;

@Mapper(
    componentModel = "spring",
    uses = {ClaimMapperHelper.class})
public interface ClaimMapper {
  @InheritConfiguration(name = "mapToClaim")
  @Mapping(target = "vatClaimed", source = ".", qualifiedByName = "mapVatClaimed")
  @Mapping(target = "fixedFee", source = ".", qualifiedByName = "mapFixedFee")
  @Mapping(target = "netProfitCost", source = ".", qualifiedByName = "mapNetProfitCost")
  @Mapping(
      target = "netDisbursementAmount",
      source = ".",
      qualifiedByName = "mapNetDisbursementAmount")
  @Mapping(target = "totalAmount", source = ".", qualifiedByName = "mapTotalAmount")
  @Mapping(
      target = "disbursementVatAmount",
      source = ".",
      qualifiedByName = "mapDisbursementVatAmount")
  @Mapping(target = "escaped", source = "feeCalculationResponse.boltOnDetails.escapeCaseFlag")
  @Mapping(target = "feeCode", source = "feeCalculationResponse.feeCode")
  @Mapping(target = "feeCodeDescription", source = "feeCalculationResponse.feeCodeDescription")
  @Mapping(target = "matterTypeCode", source = "matterTypeCode")
  @Mapping(
      target = "assessedTotalVat",
      expression = "java(claimMapperHelper.mapAssessedTotalVat())")
  @Mapping(
      target = "assessedTotalInclVat",
      expression = "java(claimMapperHelper.mapAssessedTotalInclVat())")
  @Mapping(target = "allowedTotalVat", source = ".", qualifiedByName = "mapAllowedTotalVat")
  @Mapping(target = "allowedTotalInclVat", source = ".", qualifiedByName = "mapAllowedTotalInclVat")
  @Mapping(target = "hasAssessment", source = "hasAssessment")
  @Mapping(target = "areaOfLaw", expression = "java(mapAreaOfLaw(claimResponse))")
  @Mapping(target = "providerName", ignore = true)
  @Mapping(target = "submittedDate", source = "dateSubmitted")
  @Mapping(target = "assessmentOutcome", ignore = true)
  @Mapping(target = "lastAssessment", ignore = true)
  @Mapping(target = "claimFields", ignore = true)
  @Mapping(target = "clientGender", source = "genderCode")
  @Mapping(target = "clientEthnicity", source = "ethnicityCode")
  @Mapping(target = "clientDisability", source = "disabilityCode")
  @Mapping(target = "stageReached", source = "stageReachedCode")
  @Mapping(target = "outcome", source = "outcomeCode")
  ClaimDetails mapToCommonDetails(ClaimResponseV2 claimResponse);

  @Mapping(target = "submissionId", source = "submissionId")
  @Mapping(target = "claimId", source = "id")
  @Mapping(target = "claimSummaryFeeId", source = "feeCalculationResponse.claimSummaryFeeId")
  @Mapping(target = "vatApplicable", source = "isVatApplicable")
  @Mapping(target = "uniqueFileNumber", source = "uniqueFileNumber")
  @Mapping(target = "caseReferenceNumber", source = "caseReferenceNumber")
  @Mapping(target = "clientSurname", source = "clientSurname")
  @Mapping(target = "clientForename", source = "clientForename")
  @Mapping(target = "caseStartDate", source = "caseStartDate")
  @Mapping(target = "caseEndDate", source = "caseConcludedDate")
  @Mapping(target = "officeCode", source = "officeCode")
  @Mapping(target = "submissionPeriod", expression = "java(mapSubmissionPeriod(claimResponse))")
  @Mapping(target = "categoryOfLaw", source = "feeCalculationResponse.categoryOfLaw")
  @Mapping(target = "escaped", source = "feeCalculationResponse.boltOnDetails.escapeCaseFlag")
  @Mapping(target = "uniqueCaseId", source = "uniqueCaseId")
  Claim mapToClaim(ClaimResponseV2 claimResponse);

  @InheritConfiguration(name = "mapToCommonDetails")
  @Mapping(
      target = "detentionTravelWaitingCosts",
      source = "claimResponse",
      qualifiedByName = "mapDetentionTravelWaitingCosts")
  @Mapping(
      target = "jrFormFillingCost",
      source = "claimResponse",
      qualifiedByName = "mapJrFormFillingCost")
  @Mapping(
      target = "adjournedHearing",
      source = "claimResponse",
      qualifiedByName = "mapAdjournedHearingFee")
  @Mapping(target = "cmrhTelephone", source = "claimResponse", qualifiedByName = "mapCmrhTelephone")
  @Mapping(target = "cmrhOral", source = "claimResponse", qualifiedByName = "mapCmrhOral")
  @Mapping(target = "hoInterview", source = "claimResponse", qualifiedByName = "mapHoInterview")
  @Mapping(
      target = "substantiveHearing",
      source = "claimResponse",
      qualifiedByName = "mapSubstantiveHearing")
  @Mapping(target = "counselsCost", source = "claimResponse", qualifiedByName = "mapCounselsCost")
  @Mapping(target = "uniqueClientNumber", source = "uniqueClientNumber")
  @Mapping(target = "clientDateOfBirth", source = "clientDateOfBirth")
  @Mapping(target = "clientPostcode", source = "clientPostcode")
  @Mapping(target = "isEligibleClient", source = "isEligibleClient")
  @Mapping(target = "clientType", source = "clientTypeCode")
  @Mapping(target = "homeOfficeClientNumber", source = "homeOfficeClientNumber")
  CivilClaimDetails mapToCivilClaimDetails(ClaimResponseV2 claimResponse);

  @InheritConfiguration(name = "mapToCommonDetails")
  @Mapping(
      target = "detentionTravelWaitingCosts",
      source = "claimResponse",
      qualifiedByName = "mapDetentionTravelWaitingCosts")
  @Mapping(
      target = "jrFormFillingCost",
      source = "claimResponse",
      qualifiedByName = "mapJrFormFillingCost")
  @Mapping(
      target = "adjournedHearing",
      source = "claimResponse",
      qualifiedByName = "mapAdjournedHearingFee")
  @Mapping(target = "cmrhTelephone", source = "claimResponse", qualifiedByName = "mapCmrhTelephone")
  @Mapping(target = "cmrhOral", source = "claimResponse", qualifiedByName = "mapCmrhOral")
  @Mapping(target = "hoInterview", source = "claimResponse", qualifiedByName = "mapHoInterview")
  @Mapping(
      target = "substantiveHearing",
      source = "claimResponse",
      qualifiedByName = "mapSubstantiveHearing")
  @Mapping(target = "counselsCost", source = "claimResponse", qualifiedByName = "mapCounselsCost")
  @Mapping(target = "uniqueClientNumber", source = "uniqueClientNumber")
  @Mapping(target = "clientPostcode", source = "clientPostcode")
  @Mapping(target = "isClientLegallyAided", source = "isLegallyAided")
  @Mapping(target = "isClientPostalApplicationAccepted", source = "isPostalApplicationAccepted")
  @Mapping(target = "client2Forename", source = "client2Forename")
  @Mapping(target = "client2Surname", source = "client2Surname")
  @Mapping(target = "client2DateOfBirth", source = "client2DateOfBirth")
  @Mapping(target = "client2Ucn", source = "client2Ucn")
  @Mapping(target = "client2Postcode", source = "client2Postcode")
  @Mapping(target = "client2Gender", source = "client2GenderCode")
  @Mapping(target = "client2Ethnicity", source = "client2EthnicityCode")
  @Mapping(target = "client2Disability", source = "client2DisabilityCode")
  @Mapping(target = "isClient2LegallyAided", source = "client2IsLegallyAided")
  @Mapping(
      target = "isClient2PostalApplicationAccepted",
      source = "isClient2PostalApplicationAccepted")
  MediationClaimDetails mapToMediationClaimDetails(ClaimResponseV2 claimResponse);

  @InheritConfiguration(name = "mapToCommonDetails")
  @Mapping(target = "travelCosts", source = "claimResponse", qualifiedByName = "mapTravelCosts")
  @Mapping(target = "waitingCosts", source = "claimResponse", qualifiedByName = "mapWaitingCosts")
  @Mapping(target = "representationOrderDate", source = "representationOrderDate")
  @Mapping(target = "standardFeeCategory", source = "standardFeeCategoryCode")
  @Mapping(target = "suspectsDefendantsCount", source = "suspectsDefendantsCount")
  @Mapping(
      target = "policeStationCourtAttendancesCount",
      source = "policeStationCourtAttendancesCount")
  @Mapping(target = "maatId", source = "maatId")
  @Mapping(target = "dsccNumber", source = "dsccNumber")
  @Mapping(target = "prisonLawPriorApprovalNumber", source = "prisonLawPriorApprovalNumber")
  @Mapping(target = "isDutySolicitor", source = "isDutySolicitor")
  @Mapping(target = "isYouthCourt", source = "isYouthCourt")
  CrimeClaimDetails mapToCrimeClaimDetails(ClaimResponseV2 claimResponse);

  @ObjectFactory
  default ClaimDetails createClaimDetails(ClaimResponseV2 claimResponse) {
    if (claimResponse != null && claimResponse.getAreaOfLaw() != null) {
      return switch (claimResponse.getAreaOfLaw()) {
        case CRIME_LOWER -> new CrimeClaimDetails();
        case LEGAL_HELP -> new CivilClaimDetails();
        case MEDIATION -> new MediationClaimDetails();
      };
    }
    throw new IllegalArgumentException("Both claimResponse and areaOfLaw must be non-null");
  }

  default ClaimDetails mapToClaimDetails(ClaimResponseV2 claimResponse) {
    if (claimResponse != null) {
      return createClaimDetailsFromResponse(claimResponse);
    }
    throw new IllegalArgumentException("claimResponse must be non-null");
  }

  private ClaimDetails createClaimDetailsFromResponse(ClaimResponseV2 claimResponse) {
    if (claimResponse != null && claimResponse.getAreaOfLaw() != null) {
      return switch (claimResponse.getAreaOfLaw()) {
        case CRIME_LOWER -> mapToCrimeClaimDetails(claimResponse);
        case LEGAL_HELP -> mapToCivilClaimDetails(claimResponse);
        case MEDIATION -> mapToMediationClaimDetails(claimResponse);
      };
    }
    throw new IllegalArgumentException("Both claimResponse and areaOfLaw must be non-null");
  }

  default YearMonth mapSubmissionPeriod(ClaimResponseV2 claimResponse) {
    if (claimResponse.getSubmissionPeriod() != null) {
      try {
        DateTimeFormatter formatter =
            new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("MMM-yyyy")
                .toFormatter(Locale.ENGLISH);
        return YearMonth.parse(claimResponse.getSubmissionPeriod(), formatter);
      } catch (DateTimeParseException e) {
        return null;
      }
    } else {
      return null;
    }
  }

  default void enrichWithProviderName(ClaimDetails claim, String accountName) {
    if (claim != null) {
      claim.setProviderName(accountName);
    }
  }

  default AreaOfLaw mapAreaOfLaw(ClaimResponseV2 claimResponse) {
    if (claimResponse.getAreaOfLaw() == null) {
      return null;
    }
    return switch (claimResponse.getAreaOfLaw()) {
      case CRIME_LOWER -> AreaOfLaw.CRIME_LOWER;
      case LEGAL_HELP -> AreaOfLaw.LEGAL_HELP;
      case MEDIATION -> AreaOfLaw.MEDIATION;
    };
  }
}
