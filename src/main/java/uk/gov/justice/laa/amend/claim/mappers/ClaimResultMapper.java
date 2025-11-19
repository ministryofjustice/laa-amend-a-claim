package uk.gov.justice.laa.amend.claim.mappers;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimView;
import uk.gov.justice.laa.amend.claim.viewmodels.BaseClaimView;
import uk.gov.justice.laa.amend.claim.viewmodels.Pagination;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_NUMBER;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_SIZE;

@Mapper(componentModel = "spring", uses = ClaimMapper.class)
public interface ClaimResultMapper {

    /**
     * Maps ClaimResultSet to a SearchResultViewModel.
     * @param claimResultSet The ClaimResultSet input.
     * @return A SearchResultViewModel.
     */

    @Mapping(target = "pagination", source = ".", qualifiedByName = "toPagination")
    @Mapping(target = "claims", expression = "java(mapClaims(claimResultSet, claimMapper))")
    SearchResultView toDto(ClaimResultSet claimResultSet, @Context String href, @Context ClaimMapper claimMapper);

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

    default List<BaseClaimView<Claim>> mapClaims(ClaimResultSet claimResultSet, @Context ClaimMapper claimMapper) {
        List<ClaimResponse> claims = claimResultSet.getContent();
        if (claims == null) {
            return List.of();
        } else {
            return claims.stream().map(claimMapper::mapToClaim).map(ClaimView::new).collect(Collectors.toList());
        }
    }
}
