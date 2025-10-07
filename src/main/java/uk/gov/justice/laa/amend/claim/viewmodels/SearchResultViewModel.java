package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import java.util.List;

@Getter
public class SearchResultViewModel {
    private List<Claim> claims;
    private Pagination pagination;

    public SearchResultViewModel(ClaimResultSet response, String providerAccountNumber) {
        this.claims = response.getContent().stream().map(x -> new Claim(x, providerAccountNumber)).toList();
        this.pagination = new Pagination(
            response.getTotalElements() != null ? response.getTotalElements() : 0,
            response.getSize() != null ? response.getSize() : 10,
            response.getNumber() != null ? response.getNumber() : 1,
            "/"
        );
    }
}
