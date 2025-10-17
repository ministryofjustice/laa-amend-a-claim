package uk.gov.justice.laa.amend.claim.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.viewmodels.Pagination;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_NUMBER;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_SIZE;

@Mapper(componentModel = "spring")
public interface ClaimResultMapper {

    /**
     * Maps ClaimResultSet to a SearchResultViewModel.
     * @param claimResultSet The ClaimResultSet input.
     * @return A SearchResultViewModel.
     */

    @Mapping(target = "pagination", source = ".", qualifiedByName = "toPagination")
    @Mapping(target = "claims", source = "content")
    SearchResultViewModel toDto(ClaimResultSet claimResultSet, @Context String href);


    @IterableMapping(elementTargetType = Claim.class)
    default List<Claim> map(List<ClaimResponse>  claimResponses) {
        return claimResponses.stream().map(this::mapToClaim).collect(Collectors.toList());
    }

    /**
     * Maps ClaimResponse to Claim object.
     * Provides support for enums and ensures null safety.
     * @param claimResponse The ClaimResponse input.
     * @return A fully mapped Claim.
     */
    @Mapping(target = "uniqueFileNumber", source = "uniqueFileNumber")
    @Mapping(target = "submissionId", source = "submissionId")
    @Mapping(target = "claimId", source = "id")
    @Mapping(target = "caseReferenceNumber", source = "caseReferenceNumber")
    @Mapping(target = "clientSurname", source = "clientSurname")
    @Mapping(target = "clientForename", source = "clientForename")
    @Mapping(target = "submissionPeriod", ignore = true)
    @Mapping(target = "caseStartDate", source = "caseStartDate")
    @Mapping(target = "caseEndDate", source = "caseConcludedDate")
    @Mapping(target = "feeScheme", source = "feeSchemeCode") // TODO use feeSchemeCodeDescription when available
    @Mapping(target = "categoryOfLaw", source = "feeCalculationResponse.categoryOfLaw") // TODO use categoryOfLawDescription when available
    @Mapping(target = "matterTypeCode", source = "matterTypeCode")
    @Mapping(target = "scheduleReference", source = "scheduleReference")
    @Mapping(target = "escaped", source = "feeCalculationResponse.boltOnDetails.escapeCaseFlag")
    @Mapping(target = "providerAccountNumber", constant = "TODO") // TODO use providerAccountNumber when available
    Claim mapToClaim(ClaimResponse claimResponse);

    @AfterMapping
    default void setExtraFields(ClaimResponse source, @MappingTarget Claim target) {
        target.setSubmissionPeriod(getSubmissionPeriod(source.getSubmissionPeriod()));
    }

    /**
     * Converts ClaimResultSet to a Pagination object.
     * Handles pagination-related data and safety checks for null values.
     * @param claimResultSet The ClaimResultSet input.
     * @return The Pagination component.
     */
    @Named("toPagination")
    default Pagination toPagination(ClaimResultSet claimResultSet, @Context String href) {
        return new Pagination(
                claimResultSet.getTotalElements() != null ? claimResultSet.getTotalElements() : 0,
                claimResultSet.getSize() != null ? claimResultSet.getSize() : DEFAULT_PAGE_SIZE,
                claimResultSet.getNumber() != null ? claimResultSet.getNumber() + 1 : DEFAULT_PAGE_NUMBER,
                href
        );
    }

    default YearMonth getSubmissionPeriod(String submissionPeriod) {
        if (submissionPeriod != null) {
            try {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("MMM-yyyy").toFormatter();
                return YearMonth.parse(submissionPeriod, formatter);
            } catch (DateTimeParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
