package uk.gov.justice.laa.amend.claim.mappers;

import org.mapstruct.Context;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Mapper(componentModel = "spring", uses = {ClaimMapperHelper.class})
public interface ClaimMapper {
    @InheritConfiguration(name = "mapToClaim")
    @Mapping(target = "vatClaimed", source = ".", qualifiedByName = "mapVatClaimed")
    @Mapping(target = "fixedFee", source = ".", qualifiedByName = "mapFixedFee")
    @Mapping(target = "netProfitCost", source = ".", qualifiedByName = "mapNetProfitCost")
    @Mapping(target = "netDisbursementAmount", source = ".", qualifiedByName = "mapNetDisbursementAmount")
    @Mapping(target = "totalAmount", source = ".", qualifiedByName = "mapTotalAmount")
    @Mapping(target = "disbursementVatAmount", source = ".", qualifiedByName = "mapDisbursementVatAmount")
    @Mapping(target = "escaped", source = "feeCalculationResponse.boltOnDetails.escapeCaseFlag")
    @Mapping(target = "feeCode", source = "feeCalculationResponse.feeCode")
    @Mapping(target = "feeCodeDescription", source = "feeCalculationResponse.feeCodeDescription")
    @Mapping(target = "matterTypeCode", source = "matterTypeCode")
    @Mapping(target = "assessedTotalVat", expression = "java(claimMapperHelper.mapAssessedTotalVat())")
    @Mapping(target = "assessedTotalInclVat", expression = "java(claimMapperHelper.mapAssessedTotalInclVat())")
    @Mapping(target = "allowedTotalVat", source = ".", qualifiedByName = "mapAllowedTotalVat")
    @Mapping(target = "allowedTotalInclVat", source = ".", qualifiedByName = "mapAllowedTotalInclVat")
    @Mapping(target = "hasAssessment", source = "hasAssessment")
    // Ignored and set later
    @Mapping(target = "areaOfLaw", ignore = true)
    @Mapping(target = "providerAccountNumber", ignore = true)
    @Mapping(target = "providerName", ignore = true)
    @Mapping(target = "submittedDate", ignore = true)
    @Mapping(target = "assessmentOutcome", ignore = true)
    @Mapping(target = "lastAssessment", ignore = true)
    @Mapping(target = "claimFields", ignore = true)
    ClaimDetails mapToCommonDetails(ClaimResponse claimResponse, @Context SubmissionResponse submissionResponse);


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
    @Mapping(target = "scheduleReference", source = "scheduleReference")
    @Mapping(target = "submissionPeriod", expression = "java(mapSubmissionPeriod(claimResponse))")
    @Mapping(target = "categoryOfLaw", source = "feeCalculationResponse.categoryOfLaw")
    @Mapping(target = "escaped", source = "feeCalculationResponse.boltOnDetails.escapeCaseFlag")
    Claim mapToClaim(ClaimResponse claimResponse);

    @InheritConfiguration(name = "mapToCommonDetails")
    @Mapping(target = "detentionTravelWaitingCosts", source = "claimResponse", qualifiedByName = "mapDetentionTravelWaitingCosts")
    @Mapping(target = "jrFormFillingCost", source = "claimResponse", qualifiedByName = "mapJrFormFillingCost")
    @Mapping(target = "adjournedHearing", source = "claimResponse", qualifiedByName = "mapAdjournedHearingFee")
    @Mapping(target = "cmrhTelephone", source = "claimResponse", qualifiedByName = "mapCmrhTelephone")
    @Mapping(target = "cmrhOral", source = "claimResponse", qualifiedByName = "mapCmrhOral")
    @Mapping(target = "hoInterview", source = "claimResponse", qualifiedByName = "mapHoInterview")
    @Mapping(target = "substantiveHearing", source = "claimResponse", qualifiedByName = "mapSubstantiveHearing")
    @Mapping(target = "counselsCost", source = "claimResponse", qualifiedByName = "mapCounselsCost")
    @Mapping(target = "uniqueClientNumber", source = "uniqueClientNumber")
    CivilClaimDetails mapToCivilClaimDetails(ClaimResponse claimResponse, @Context SubmissionResponse submissionResponse);

    @InheritConfiguration(name = "mapToCommonDetails")
    @Mapping(target = "travelCosts", source = "claimResponse", qualifiedByName = "mapTravelCosts")
    @Mapping(target = "waitingCosts", source = "claimResponse", qualifiedByName = "mapWaitingCosts")
    CrimeClaimDetails mapToCrimeClaimDetails(ClaimResponse claimResponse, @Context SubmissionResponse submissionResponse);

    @ObjectFactory
    default ClaimDetails createClaimDetails(@Context SubmissionResponse submissionResponse) {
        if (submissionResponse == null || submissionResponse.getAreaOfLaw() == null) {
            throw new IllegalArgumentException("SubmissionResponse must not be null");
        }
        return switch (submissionResponse.getAreaOfLaw()) {
            case CRIME_LOWER -> new CrimeClaimDetails();
            case LEGAL_HELP, MEDIATION -> new CivilClaimDetails();
        };
    }

    default ClaimDetails mapToClaimDetails(ClaimResponse claimResponse, SubmissionResponse submissionResponse) {
        if (claimResponse != null && submissionResponse != null && submissionResponse.getAreaOfLaw() != null) {
            ClaimDetails claimDetails = switch (submissionResponse.getAreaOfLaw()) {
                case CRIME_LOWER -> mapToCrimeClaimDetails(claimResponse, submissionResponse);
                case LEGAL_HELP, MEDIATION -> mapToCivilClaimDetails(claimResponse, submissionResponse);
            };
            enrichWithSubmission(claimDetails, submissionResponse);
            return claimDetails;
        }
        throw new IllegalArgumentException("Both claimResponse and submissionResponse must be non-null");
    }

    default YearMonth mapSubmissionPeriod(ClaimResponse claimResponse) {
        if (claimResponse.getSubmissionPeriod() != null) {
            try {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("MMM-yyyy").toFormatter(Locale.ENGLISH);
                return YearMonth.parse(claimResponse.getSubmissionPeriod(), formatter);
            } catch (DateTimeParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private void enrichWithSubmission(ClaimDetails claim, SubmissionResponse submissionResponse) {
        if (submissionResponse != null && submissionResponse.getAreaOfLaw() != null) {
            claim.setAreaOfLaw(submissionResponse.getAreaOfLaw().getValue());
            claim.setProviderAccountNumber(submissionResponse.getOfficeAccountNumber());
            claim.setProviderName(submissionResponse.getProviderUserId());
            claim.setSubmittedDate(submissionResponse.getSubmitted() != null ? submissionResponse.getSubmitted().toLocalDateTime() : null);
        }
    }
}