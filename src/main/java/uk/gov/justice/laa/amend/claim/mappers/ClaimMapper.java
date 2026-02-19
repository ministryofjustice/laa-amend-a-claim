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
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;

@Mapper(
        componentModel = "spring",
        uses = {ClaimMapperHelper.class})
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
    @Mapping(target = "areaOfLaw", expression = "java(mapAreaOfLaw(claimResponse))")
    @Mapping(target = "providerAccountNumber", ignore = true)
    @Mapping(target = "providerName", ignore = true)
    @Mapping(target = "submittedDate", ignore = true)
    @Mapping(target = "assessmentOutcome", ignore = true)
    @Mapping(target = "lastAssessment", ignore = true)
    @Mapping(target = "claimFields", ignore = true)
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
    @Mapping(target = "scheduleReference", source = "scheduleReference")
    @Mapping(target = "submissionPeriod", expression = "java(mapSubmissionPeriod(claimResponse))")
    @Mapping(target = "categoryOfLaw", source = "feeCalculationResponse.categoryOfLaw")
    @Mapping(target = "escaped", source = "feeCalculationResponse.boltOnDetails.escapeCaseFlag")
    Claim mapToClaim(ClaimResponseV2 claimResponse);

    @InheritConfiguration(name = "mapToCommonDetails")
    @Mapping(
            target = "detentionTravelWaitingCosts",
            source = "claimResponse",
            qualifiedByName = "mapDetentionTravelWaitingCosts")
    @Mapping(target = "jrFormFillingCost", source = "claimResponse", qualifiedByName = "mapJrFormFillingCost")
    @Mapping(target = "adjournedHearing", source = "claimResponse", qualifiedByName = "mapAdjournedHearingFee")
    @Mapping(target = "cmrhTelephone", source = "claimResponse", qualifiedByName = "mapCmrhTelephone")
    @Mapping(target = "cmrhOral", source = "claimResponse", qualifiedByName = "mapCmrhOral")
    @Mapping(target = "hoInterview", source = "claimResponse", qualifiedByName = "mapHoInterview")
    @Mapping(target = "substantiveHearing", source = "claimResponse", qualifiedByName = "mapSubstantiveHearing")
    @Mapping(target = "counselsCost", source = "claimResponse", qualifiedByName = "mapCounselsCost")
    @Mapping(target = "uniqueClientNumber", source = "uniqueClientNumber")
    CivilClaimDetails mapToCivilClaimDetails(ClaimResponseV2 claimResponse);

    @InheritConfiguration(name = "mapToCommonDetails")
    @Mapping(target = "travelCosts", source = "claimResponse", qualifiedByName = "mapTravelCosts")
    @Mapping(target = "waitingCosts", source = "claimResponse", qualifiedByName = "mapWaitingCosts")
    CrimeClaimDetails mapToCrimeClaimDetails(ClaimResponseV2 claimResponse);

    @ObjectFactory
    default ClaimDetails createClaimDetails(ClaimResponseV2 claimResponse) {
        if (claimResponse != null && claimResponse.getAreaOfLaw() != null) {
            return switch (claimResponse.getAreaOfLaw()) {
                case CRIME_LOWER -> new CrimeClaimDetails();
                case LEGAL_HELP, MEDIATION -> new CivilClaimDetails();
            };
        }
        throw new IllegalArgumentException("Both claimResponse and areaOfLaw must be non-null");
    }

    default ClaimDetails mapToClaimDetails(ClaimResponseV2 claimResponse, SubmissionResponse submissionResponse) {
        if (claimResponse != null && submissionResponse != null) {
            var claimDetails = createClaimDetailsFromResponse(claimResponse);
            enrichWithSubmission(claimDetails, submissionResponse);
            return claimDetails;
        }
        throw new IllegalArgumentException("Both claimResponse and submissionResponse must be non-null");
    }

    private ClaimDetails createClaimDetailsFromResponse(ClaimResponseV2 claimResponse) {
        if (claimResponse != null && claimResponse.getAreaOfLaw() != null) {
            return switch (claimResponse.getAreaOfLaw()) {
                case CRIME_LOWER -> mapToCrimeClaimDetails(claimResponse);
                case LEGAL_HELP, MEDIATION -> mapToCivilClaimDetails(claimResponse);
            };
        }
        throw new IllegalArgumentException("Both claimResponse and areaOfLaw must be non-null");
    }

    default YearMonth mapSubmissionPeriod(ClaimResponseV2 claimResponse) {
        if (claimResponse.getSubmissionPeriod() != null) {
            try {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
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

    private void enrichWithSubmission(ClaimDetails claim, SubmissionResponse submissionResponse) {
        if (submissionResponse != null) {
            claim.setProviderAccountNumber(submissionResponse.getOfficeAccountNumber());
            claim.setSubmittedDate(
                    submissionResponse.getSubmitted() != null
                            ? submissionResponse.getSubmitted().toLocalDateTime()
                            : null);
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
