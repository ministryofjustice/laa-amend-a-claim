package uk.gov.justice.laa.amend.claim.mappers;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.viewmodels.Pagination;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_NUMBER;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_SIZE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;

@Mapper(componentModel = "spring")
public interface ClaimResultMapper {

    /**
     * Maps ClaimResultSet to a SearchResultViewModel.
     * @param claimResultSet The ClaimResultSet input.
     * @return A SearchResultViewModel.
     */

    @Mapping(target = "pagination", source = ".", qualifiedByName = "toPagination")
    @Mapping(target = "claims", source = "content")
    SearchResultViewModel toDto(ClaimResultSet claimResultSet);


    @IterableMapping(elementTargetType = Claim.class)
    default List<Claim> map(List<ClaimResponse>  claimResponses) {
        return claimResponses.stream().map(this::mapToClaim).collect(Collectors. toList());
    }

    /**
     * Maps ClaimResponse to Claim object.
     * Provides support for enums and ensures null safety.
     * @param claimResponse The ClaimResponse input.
     * @return A fully mapped Claim.
     */
    @Mapping(target = "uniqueFileNumber", source = "uniqueFileNumber")
    @Mapping(target = "caseReferenceNumber", source = "caseReferenceNumber", defaultValue = "Unknown")
    @Mapping(target = "clientSurname", source = "clientSurname", defaultValue = "Unknown")
    @Mapping(target = "dateSubmitted", source = "caseStartDate")
    @Mapping(target = "account", source = "scheduleReference")
    @Mapping(target = "type", source = "matterTypeCode")
    @Mapping(target = "status", source = "status", defaultValue = "Unknown")
    @Mapping(target = "dateSubmittedForDisplay", source = "caseStartDate", qualifiedByName = "toFormattedDate")
    Claim mapToClaim(ClaimResponse claimResponse);

    /**
     * Converts ClaimResultSet to a Pagination object.
     * Handles pagination-related data and safety checks for null values.
     * @param claimResultSet The ClaimResultSet input.
     * @return The Pagination component.
     */
    @Named("toPagination")
    default Pagination toPagination(ClaimResultSet claimResultSet) {
        return new Pagination(
                claimResultSet.getTotalElements() != null ? claimResultSet.getTotalElements() : 0,
                claimResultSet.getSize() != null ? claimResultSet.getSize() : DEFAULT_PAGE_SIZE,
                claimResultSet.getNumber() != null ? claimResultSet.getNumber() : DEFAULT_PAGE_NUMBER,
                "/"
        );
    }

    /**
     * Formats the date string into the required format (dd MMM yyyy)
     */
    @Named("toFormattedDate")
    default String convertToFormattedDate(String displayDate) {
        if (displayDate == null) {
            return null;
        }
        LocalDate date = LocalDate.parse(displayDate);
        return date.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

}
