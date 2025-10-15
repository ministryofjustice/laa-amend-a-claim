package uk.gov.justice.laa.amend.claim.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimType;
import uk.gov.justice.laa.amend.claim.viewmodels.Pagination;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;
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
    @Mapping(target = "caseReferenceNumber", source = "caseReferenceNumber")
    @Mapping(target = "clientSurname", source = "clientSurname")
    @Mapping(target = "dateSubmitted", source = "caseStartDate")
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "escaped", source = "feeCalculationResponse.boltOnDetails.escapeCaseFlag")
    @Mapping(target = "referenceNumber", ignore = true)
    @Mapping(target = "dateSubmittedForDisplay", ignore = true)
    @Mapping(target = "dateSubmittedForSorting", ignore = true)
    @Mapping(target = "status", ignore = true)
    Claim mapToClaim(ClaimResponse claimResponse);

    @AfterMapping
    default void setExtraFields(ClaimResponse source, @MappingTarget Claim target) {
        target.setAccount(getAccountNumber(source.getScheduleReference()));

        target.setType(null); // TODO once API exposes this value feeCalculationResponse.categoryOfLawDescription

        target.setReferenceNumber(getReferenceNumber(target.getUniqueFileNumber(), target.getCaseReferenceNumber()));

        target.setDateSubmittedForDisplay(getDateSubmittedForDisplay(target.getDateSubmitted()));

        target.setDateSubmittedForSorting(getDateSubmittedForSorting(target.getDateSubmitted()));

        target.setStatus(getStatus(target.getEscaped()));
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

    default String getAccountNumber(String scheduleReference) {
        return scheduleReference != null ? scheduleReference.split("/")[0] : null;
    }

    default String getReferenceNumber(String uniqueFileNumber, String caseReferenceNumber) {
        return uniqueFileNumber != null ? uniqueFileNumber : caseReferenceNumber;
    }

    default String getDateSubmittedForDisplay(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : null;
    }

    default long getDateSubmittedForSorting(LocalDate date) {
        return date != null ? date.toEpochDay() : 0;
    }

    default ClaimType getStatus(Boolean escaped) {
        if (escaped != null) {
            if (escaped) {
                return ClaimType.ESCAPE;
            } else {
                return ClaimType.FIXED;
            }
        } else {
            return ClaimType.UNKNOWN;
        }
    }
}
